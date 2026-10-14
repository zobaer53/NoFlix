#!/usr/bin/env bash
# Install-over test: OLD apk + wish list item, then the current debug build installed on top.
# Usage: .maestro/upgrade.sh path/to/old.apk [path/to/new.apk]  (new defaults to the debug build)
set -euo pipefail
cd "$(dirname "$0")/.."
# With several devices attached, pick one with ANDROID_SERIAL (adb reads it; Maestro gets --device).
MAESTRO=(maestro ${ANDROID_SERIAL:+--device "$ANDROID_SERIAL"})
adb uninstall com.zobaer53.zedmovies >/dev/null 2>&1 || true
adb install "$1" >/dev/null
"${MAESTRO[@]}" test .maestro/upgrade/1_add_wishlist.yaml
adb install -r "${2:-app/build/outputs/apk/debug/app-debug.apk}" >/dev/null
adb logcat -c
"${MAESTRO[@]}" test .maestro/upgrade/2_check_wishlist.yaml
if adb logcat -d | grep -qE "FATAL EXCEPTION|IllegalStateException: .*(Room|migration)"; then
  echo "CRASH after upgrade:"; adb logcat -d | grep -A12 "FATAL EXCEPTION" | head -30; exit 1
fi
echo "UPGRADE CHECK PASSED"
