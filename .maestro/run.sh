#!/usr/bin/env bash
# Builds (unless --no-build), installs the debug APK, runs every Maestro flow, then checks
# logcat for crashes and that the Play screenshot is landscape.
# Usage: .maestro/run.sh [--no-build] [flow.yaml ...]
set -euo pipefail
cd "$(dirname "$0")/.."
# With several devices attached, pick one with ANDROID_SERIAL (adb reads it; Maestro gets --device).
MAESTRO=(maestro ${ANDROID_SERIAL:+--device "$ANDROID_SERIAL"})
PKG=com.zobaer53.zedmovies
OUT=build/maestro
if [ "${1:-}" = "--no-build" ]; then shift; else ./gradlew :app:assembleDebug --console=plain -q; fi
# Clean install: an update over an older build can inherit its state (e.g. 16 KB compat mode).
adb uninstall com.zobaer53.zedmovies >/dev/null 2>&1 || true
adb install app/build/outputs/apk/debug/app-debug.apk >/dev/null

# 16 KB page-size devices (Android 15+) need every 64-bit native library aligned to 2**14.
SDK=$(sed -n 's/^sdk.dir=//p' local.properties)
OBJDUMP=$(ls "$SDK"/ndk/*/toolchains/llvm/prebuilt/*/bin/llvm-objdump 2>/dev/null | sort -V | tail -1)
if [ -n "$OBJDUMP" ]; then
  libs=$(mktemp -d)
  unzip -q -o app/build/outputs/apk/debug/app-debug.apk 'lib/arm64-v8a/*' 'lib/x86_64/*' -d "$libs" 2>/dev/null || true
  for so in "$libs"/lib/*/*.so; do
    [ -e "$so" ] || continue
    if "$OBJDUMP" -p "$so" | grep LOAD | grep -qv 'align 2\*\*1[4-9]'; then
      echo "NOT 16 KB aligned: ${so#$libs/}"; align_failed=1
    fi
  done
  rm -rf "$libs"
  [ -z "${align_failed:-}" ] && echo "native libraries: 16 KB aligned"
fi
# Old emulators (API 26) sometimes fail to clear ("failed to clear the 'main' log"), several times
# in a row after a cold boot.
for attempt in 1 2 3 4 5; do adb logcat -c && break; [ $attempt = 5 ] && exit 1; sleep 2; done
rm -rf "$OUT"; mkdir -p "$OUT"
# Stream logcat for the whole run: Maestro clears the device log at the start of every flow, so a
# dump at the end would only cover the last flow. A clear can end the stream (seen on API 26), so
# restart it; a restart repeats the buffer, which the checks below tolerate (they only count > 0).
( while :; do adb logcat; sleep 0.2; done ) > "$OUT/logcat.txt" 2>/dev/null &
logcat_pid=$!
status=0
[ -n "${align_failed:-}" ] && status=1
if [ $# -gt 0 ]; then
  "${MAESTRO[@]}" test --test-output-dir "$OUT" "$@" || status=$?
else
  "${MAESTRO[@]}" test --test-output-dir "$OUT" .maestro || status=$?
fi
sleep 2; logcat_children=$(pgrep -P "$logcat_pid" || true)
kill "$logcat_pid" $logcat_children 2>/dev/null || true
wait "$logcat_pid" 2>/dev/null || true
if grep -E "FATAL EXCEPTION|Fatal signal|AndroidRuntime: Process: $PKG" "$OUT/logcat.txt" | grep -q .; then
  echo "CRASH found in logcat:"; grep -A15 -E "FATAL EXCEPTION|Fatal signal" "$OUT/logcat.txt" | head -40
  status=1
fi
shot=$(find "$OUT" -name 'play_webview.png' | head -1)
if [ -n "$shot" ]; then
  python3 -I - "$shot" <<'PY' || status=1
import struct, sys
with open(sys.argv[1], 'rb') as f:
    w, h = struct.unpack('>II', f.read(24)[16:24])
print(f"play screenshot {w}x{h}: {'landscape OK' if w > h else 'NOT landscape'}")
sys.exit(0 if w > h else 1)
PY
fi
# Ad-filter checks need the Play flow, so they only apply to a full run.
if [ $# -eq 0 ]; then
filters=$(adb shell run-as $PKG ls files/filter_data 2>/dev/null | wc -l | tr -d ' ')
blocked=$(grep -c "NoFlixAdBlock: blocked" "$OUT/logcat.txt" || true)
# Debug builds print the injected scripts' console logs (the {{DEBUG}} flag in ad-filter's JS).
scripts=$(grep -c "element hiding finished" "$OUT/logcat.txt" || true)
echo "filter lists installed: $filters, requests blocked: $blocked, element-hiding script runs: $scripts"
if [ "$scripts" -lt 1 ]; then echo "injected element-hiding script never finished"; status=1; fi
if [ "$filters" -lt 6 ]; then echo "expected 6 installed filter lists"; status=1; fi
fi
[ $status -eq 0 ] && echo "ALL CHECKS PASSED" || echo "CHECKS FAILED (see $OUT)"
exit $status
