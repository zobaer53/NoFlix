# NoFlix: migration plan to the latest Android toolchain (Android 17 / API 37)

_Researched 2026-10-14. Versions below are the latest stable releases on that date, read from Google Maven, Maven Central and services.gradle.org. Before each phase, check them again with Android Studio's upgrade hints._

The plan follows Google's [version upgrade strategy](https://developer.android.com/build/version-upgrade-strategies): take a baseline, upgrade the build tooling in dependency order, then the SDK, then libraries, comparing and testing after each step. The goal is an app that **builds with the current toolchain, targets API 37, runs correctly on Android 15–17 (including 16 KB page-size devices and large screens), and still works on minSdk 24**.

> **Release safety:** a push to `master` publishes a public release (see `.github/workflows/release.yml`). Do all of this work on the `upgrade` branch. Merge only after the test matrix in Phase 8 passes. Don't name the streaming site in commit messages, release notes or file names.

## Progress

Each step is tested with the Maestro suite in `.maestro/` (see "Testing harness" below), mostly on a Pixel 9 Pro API 36 emulator with **16 KB pages** (`Pixel_9_Pro_16k`), plus API 24 and API 30 emulators and a signed release build. **Status: Phases 1–6 and targetSdk 37 done (tested on an Android 17 Pixel 10 Pro emulator); tested on a real Pixel 6a (Android 16); minSdk raised to 26; a real Android 17 device is left (see "Open decisions" below).**

| Step                                                      | Status  | Maestro                          | Notes                                                                                                                         |
| --------------------------------------------------------- | ------- | -------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| Baseline (current app, AGP 8.0.2)                         | ✅ done | 5/5 pass                         | Harness written against the unchanged app                                                                                     |
| Phase 1: cleanup                                          | ✅ done | 5/5 pass                         | Unused deps and dead code removed, `WebViewActivity()` bug fixed                                                              |
| Phase 1: version catalog                                  | ✅ done | 5/5 pass                         | `gradle/libs.versions.toml`. The resolved-dependency diff showed only the intended removals. Groovy kept, KTS skipped for now |
| Phase 2 Step A: AGP 8.13 / Gradle 8.14 / Kotlin 2.3 / KSP | ✅ done | 5/5 pass + upgrade-in-place pass | See Step A notes below                                                                                                        |
| Phase 3 (part): `:ad-filter` without Anko / Mezzanine / HttpRequest; jcenter, Aliyun and JitPack removed | ✅ done | 5/5 pass | Done before AGP 9 so `legacy-kapt` is never needed. See notes |
| Phase 2 Step B: AGP 9.4.1 / Gradle 9.7.1 / Kotlin 2.4.21 / Hilt 2.60.1 | ✅ done | 5/5 pass + upgrade-in-place pass | Built-in Kotlin, configuration cache on, Jetifier off. See Step B notes |
| Phase 3: native lib (NDK r28, CMake 3.22, 16 KB) + DataStore 1.2.1 | ✅ done | 5/5 pass on API 36 **and** on a 16 KB page-size API 36 emulator; upgrade-in-place pass on both | The current release shows Android's "isn't 16 KB compatible" dialog. Fixed. See notes |
| Phase 4: targetSdk 33 → 34 | ✅ done | 5/5 pass (16 KB API 36 emulator) | No code changes needed |
| Phase 4: compileSdk 37 (all modules, from the catalog) | ✅ done | 5/5 pass (16 KB API 36 emulator) | `platforms;android-37.0` |
| Phase 4: targetSdk 35 (edge-to-edge enforced) | ✅ done | 5/5 pass (16 KB API 36); screenshots compared with target 34, gesture and 3-button nav | No visual change, no code changes needed |
| Phase 4: targetSdk 36 (predictive back, large-screen orientation) | ✅ done | 5/5 pass (16 KB API 36) + manual large-screen check | Back works unchanged. On sw ≥ 600dp the portrait lock is ignored and the UI stretches in landscape (usable) |
| Phase 5a: core/core-ktx 1.19.1, splashscreen 1.2.0, webkit 1.17.1, WorkManager 2.12.0, coroutines 1.11.0, datetime 0.8.0, timber 5.0.1, test libs; appcompat removed | ✅ done | 5/5 pass + upgrade-in-place pass (16 KB API 36) | Also `LocalDate.parse()` and `setAlgorithmicDarkeningAllowed(false)` |
| Phase 5b-1: Compose BOM 2023.06 → 2026.09.00, Accompanist removed, activity 1.13.0, `enableEdgeToEdge()`, Material ripple APIs | ✅ done | 5/5 pass (16 KB API 36), screenshots identical, new pager/tab swipe checks | See 5b-1 notes |
| Phase 5b-2: Material 2 → Material 3 (PullToRefreshBox, SecondaryTabRow, M3 `Text`), `compose-material` dropped | ✅ done | 5/5 pass (16 KB API 36), new pull-to-refresh step, screenshots checked | `material-icons-core` stays for one icon |
| Phase 5b-3: navigation 2.10.2, lifecycle 2.11.0, paging + paging-compose 3.5.3, `hilt-lifecycle-viewmodel-compose` 1.4.0 | ✅ done | 5/5 pass + upgrade-in-place pass (16 KB API 36) | Also M3 deprecation renames and a dead helper removed |
| Phase 5c: Retrofit 3.0.0 + first-party converter, OkHttp 5.5.0, Coil 3.6.3 (+ `coil-network-okhttp`), Lottie 6.7.1, Jsoup 1.23.2 | ✅ done | 5/5 pass (16 KB API 36), posters checked on screenshots | Custom `ResultCall` adapter unchanged |
| Phase 6: CI workflow (actions v7/v6/v6, SDK 37 + NDK r28c + CMake 3.22.1, 16 KB check), versionName 3.0.0, CLAUDE.md | ✅ done | CI steps reproduced locally; signed release 3.0.0 passes 5/5 (clean install); public 2.1.2 → 3.0.0 update keeps data | Workflow itself only runs on `master` |
| Phase 4: targetSdk 37 | ✅ done | 5/5 pass on Android 17 (`Pixel_10_Pro`, API 37.0, 4 KB pages), both before (target 36) and after the bump | No code changes. No CT/ECH, TLS or cleartext errors; background/resume and lock/unlock keep the player page. See the API 37 notes |
| Phase 8 (part): API 24 (minSdk) emulator | ✅ fixed a **pre-existing crash** | 5/5 UI flows pass on Android 7.0 | Core library desugaring added. Ad filter and playback still can't work on 7.x (TLS roots), decision needed |
| minSdk 24 → 26 (Android 8.0), core library desugaring removed | ✅ done | 6/6 on the Pixel 6a; 6/6 flows on a new API 26 emulator | Found and fixed a filter-worker deadlock (ANR) on the way, see the minSdk 26 notes |
| Room schema export (`androidx.room` plugin, `app/schemas/`) | ✅ done | 5/5 pass + upgrade-in-place pass | Identity hash `318b45f8…` matches existing installs |
| Phase 8 (part): API 30 (Android 11, 3-button nav) emulator | ✅ done | 5/5 pass (exit 0); re-verified on 16 KB API 36 | Restored the pre-migration nav-bar look (no white scrim) |
| Phase 8 (part): Android 17 emulator | ✅ done | 5/5 pass; 6/6 with the trailer flow under `-feature GuestAngle` | Pixel 10 Pro profile, Play image, 20 GB |
| Phase 8 (part): real phone (Pixel 6a, Android 16, 4 KB pages, WebView 151) | ✅ done | 6/6 pass; upgrade 2.1.2 → 3.0.0 passes | A stream plays full screen in landscape after switching server |
| Phase 8: remaining matrix (physical tablet, real Android 17 device, Android 17 with 16 KB pages) | ⏳ remaining | | |
| Phase 3: rest of the ad-block modules (deps bump) | ✅ done (in 5a) | | timber 5.0.1, coroutines/serialization/WorkManager/core-ktx from the catalog, appcompat removed |

### Open decisions / remaining work (need the owner or hardware)

1. **targetSdk 37 follow-ups:** (a) confirm a video actually streams on Android 17. **Partly resolved 2026-10-14:** on the Pixel 6a (Android 16) the site's default server ("Headhunter XXXI") also stays on "Connecting…", but picking the next server in the site's server list plays the movie full screen in landscape. So the stall is that one server, not the app. Still to do: the same check on Android 17 (emulator with `-feature GuestAngle`). On 2026-10-14 every title stayed on "Connecting…" in the app **and in Chrome** on the same emulator, and one upstream source host timed out from the Mac too, so this is upstream, not the app (see the API 37 notes). (b) ~~Re-run `.maestro/upgrade.sh` (2.1.2 → 3.0.0 at target 37) on a clean tree.~~ **Done 2026-10-14** on the Pixel 6a: public 2.1.2 release → signed 3.0.0 release (`fbaf989`, versionCode 30000, targetSdk 37, same `CN=zeD, O=NoFlix` cert), wish list item kept, no crash. (c) The 16 KB Android 17 image (`android-37.2 … ps16k`) isn't installed, so the 16 KB update-path finding is still only tested on API 36.
2. ~~**minSdk 24 vs 26**~~ **Decided 2026-10-14: minSdk 26** (see the minSdk 26 notes).
3. **Updating from 2.1.x on 16 KB devices** keeps Android's compat-mode warning (see the open 16 KB finding). Mention it in the 3.0.0 release notes, or test on a real 16 KB phone first.
4. **Release:** merging `upgrade` into `master` publishes **3.0.0** through CI. Do it after a real-phone smoke test.
5. **Phase 7 (optional):** R8/minify, Baseline Profiles, adaptive tablet layouts, refreshing the AdGuard Scriptlets/ExtendedCss JS, making the whole Play pill clickable (pre-existing: only the icon is).

### Testing harness (added during the migration)

- `.maestro/flows/01_home` … `05_play`: home loads, See all + paging, search + open result, details + wish list add/remove, Play opens the WebView (twice, so the second load runs with installed filters) and Back returns.
- `.maestro/run.sh [--no-build]`: builds and installs the debug APK, runs all flows, then fails on any `FATAL EXCEPTION`/`Fatal signal` in logcat, checks the Play screenshot is landscape, checks that 6 filter lists are installed in `files/filter_data`, and reports how many requests were blocked.
- `.maestro/upgrade.sh <old.apk>`: install the old APK, add a wish list item, install the current build over it, and check the item survived (Room/schema safety).
- Debug-only log `NoFlixAdBlock: blocked <url>` added in `ContentBlockingWebViewClient.kt` so the runner can count blocked requests.

### Notes and deviations

- **Pre-existing bug (not fixed, out of scope):** on the details screen only the 16dp play _icon_ is clickable, not the "Play" label or the pill around it (`ui/smallcomponent/Rating.kt`). The Maestro flow taps the icon (content description "Rating").
- **Step A used Kotlin 2.3.21 and Hilt 2.57.2, not 2.4.x / 2.60.x.** Hilt 2.60.1's Gradle plugin refuses AGP < 9.0, and Hilt 2.57.2 can't read Kotlin 2.4 metadata ("maximum supported version is 2.3.0"). Kotlin 2.4 and Hilt 2.60 come in Step B together with AGP 9.
- Moved earlier than planned, because Step A needed them: Room 2.5.1 → 2.8.5 (KSP2), kotlinx-serialization 1.5.1 → 1.11.0 (Kotlin 2 serialization plugin), app compileSdk 33 → 36 (37 isn't installed yet; targetSdk still 33), library modules Java 1.8 → 17 (Kotlin 2 defaults to JVM 17, and the mismatch fails the build).
- `kotlin-bom` was removed in Step A, not Phase 1. With Kotlin 1.7.20 it was still needed to stop duplicate stdlib-jdk7/jdk8 classes.
- K2 compiler fixes: `toLowerCase(Locale.ROOT)` → `lowercase()` in `adblock-client/.../ResourceType.kt` (deprecation is an error now). Named arguments on a function-type call (`errorContent(errorMessage = …)`) → positional in `ui/smallcomponent/Container.kt`.
- `kapt { arg("mezzanine.projectPath", projectDir) }` needs `projectDir.absolutePath` with Kotlin 2's kapt (it no longer accepts a `File`).
- The `wrapper` task can't run once the build needs a newer Gradle than the current wrapper, so edit `distributionUrl` in `gradle/wrapper/gradle-wrapper.properties` first, then run `./gradlew wrapper` to refresh the scripts.
- Dependency Guard and the lint baseline weren't added. A `./gradlew :app:dependencies --configuration releaseRuntimeClasspath` snapshot diff is used per step instead.
- **Mezzanine replacement verified byte-for-byte:** parsing the six string literals in Mezzanine's generated `MezzanineGenerator.java` and comparing them with the JS sources showed they were identical (no minification or stripping). The files now live in `ad-filter/src/main/assets/adfilter/` and are read by `script/JsAssets.kt`, initialized in `AdFilterImpl`. `scriptlets_src/gulpfile.js` now writes there too.
- **Behavior change (debug builds only):** `ScriptInjection` used Anko's `BuildConfig.DEBUG` (always false), so the injected JS never logged. It now uses `:ad-filter`'s own `BuildConfig`, so debug builds print `console.log` lines such as "element hiding finished". `run.sh` uses that as a check that the asset-loaded scripts run. Release builds are unchanged.
- `DownloadWorker` uses `HttpURLConnection` (10 s connect/read timeouts, non-2xx → failure, non-UTF-8 charsets re-encoded to UTF-8), the same contract as Edsuns `HttpRequest`. Verified: all 6 filter lists download and install after a clean start.
- `settings.gradle` now only has `google()`, `mavenCentral()` and `gradlePluginPortal()`. The build resolves with no JCenter, Aliyun or JitPack. CLAUDE.md still describes the Aliyun mirror; update it in the CI/docs step.
- **Step B: AGP 9 drops the compile/runtime classpath alignment** (`android.dependency.useConstraints` now defaults to false). Before, a transitive constraint quietly raised the Compose BOM from the declared 2023.05.00 to 2023.06.00 on every classpath. Under AGP 9 only the runtime classpath got it, so `compileDebugKotlin` saw material3 1.0.1 and failed (`TextFieldDefaults.colors` unresolved) while the APK shipped 1.1.0. Fix: declare `composeBom = "2023.06.00"`, which is what already shipped, so there's no runtime change. A compile-vs-runtime diff afterwards shows only harmless older-on-compile transitive versions (core-ktx, annotation-experimental, arch core-runtime). **Watch for this with every upgrade from now on:** declare the versions you actually run.
- Step B: with built-in Kotlin the Kotlin Gradle plugin version is pinned with `buildscript { dependencies { classpath libs.kotlin.gradle.plugin } }` in the root `build.gradle`. `./gradlew buildEnvironment` confirms AGP's bundled 2.2.10 resolves to 2.4.21. The `kotlin-android`/`kotlin-kapt` plugins are gone from every module, and so is the `kotlin { compilerOptions { jvmTarget } }` block (it now defaults to `compileOptions.targetCompatibility` = 17).
- Step B: library modules moved to the new DSL (`compileSdk 36`, `minSdk 24`, no `targetSdkVersion`/`versionCode`/`versionName`, `packaging.resources.pickFirsts`). `gradle.properties`: removed `android.useAndroidX` and `android.enableJetifier` (`./gradlew checkJetifier` found no support-library users), `configureondemand` → `org.gradle.configuration-cache=true`, `-Xmx4g`.
- Step B: AGP 9 prints `EXTERNAL_NATIVE_BUILD_CONFIGURATION` warnings for `:adblock-client` (CMake 3.10.2 / `cmake_minimum_required(3.4.1)`). Handled in the native step next.
- The baseline (pre-migration) debug APK is kept at `build/baseline/baseline-debug.apk` (gitignored) for `.maestro/upgrade.sh`.
- **16 KB results (Pixel 9 Pro, API 36, `system-images;android-36;google_apis_playstore_ps16k`, AVD `Pixel_9_Pro_16k`, `getconf PAGE_SIZE` = 16384):**
  - Pre-migration build: Android shows "This app isn't 16 KB compatible. APK and ELF alignment checks failed. This app will be run using page size compatible mode" on launch. **Users of the current release on 16 KB devices see this.**
  - After AGP 9 (NDK r28c is its default, so `libadblock-client.so` was already `2**14`): still "ELF alignment check failed". The culprit was **`libdatastore_shared_counter.so` from DataStore `1.1.0-alpha04`** (4 KB-aligned), not our library. DataStore → 1.2.1 (planned for Phase 5, pulled forward) ships it 16 KB-aligned. No dialog now, and all flows pass.
  - Lesson: 16 KB compliance depends on **every** `.so` in the APK, including AndroidX ones. `run.sh` now checks every 64-bit `.so` with `llvm-objdump` (fails unless all LOAD segments are `2**14`+), and `01_home` asserts that the "16 KB compatible" dialog never appears.
  - 32-bit (`armeabi-v7a`, `x86`) libraries stay 4 KB-aligned. That's fine, since 16 KB pages are 64-bit only.
- Native step: `ndkVersion` and the CMake version now come from the catalog (`ndk = "28.2.13676358"`, `cmake = "3.22.1"`), and `cmake_minimum_required(VERSION 3.22.1)`. AGP's `EXTERNAL_NATIVE_BUILD_CONFIGURATION` warnings are gone. Clang still prints one old warning in the vendored code (`ad_block_client.cc:51`, `-Wwritable-strings`): harmless, left as is.
- Test-flow fixes found on a fresh emulator: Android's one-time "Viewing full screen" hint (shown the first time an app hides the system bars) swallows the first Back, so `05_play` taps "Got it" if it's there. The upgrade flow dismisses the old build's 16 KB dialog ("OK") before anything else.
- **Open finding: updating over the old release keeps 16 KB compat mode (API 36 image).** `dumpsys package` shows `pageSizeCompat=0` for a clean install of the new build, but `6` (APK + ELF checks "failed") when the same APK is installed **over** the pre-migration build, even after a reboot. The old warning dialog keeps showing. Adding `android:pageSizeCompat="disabled"` to `<application>` set the value to `70` but did **not** remove the dialog, so it was reverted (the default keeps compat mode as a safety net if a misaligned `.so` ever slips in). The app still works in compat mode, it just shows the warning. Users can turn it off under App info → Advanced → "Run app with page size compat mode", or reinstall. **To do:** retest on the API 37 image. If it persists, mention it in the 3.0.0 release notes.
- `run.sh` now uninstalls before installing, so leftovers from `upgrade.sh` (like inherited compat mode) can't affect a run.
- `compileSdk` comes from the catalog (`compileSdk = "37"`) in all three modules. AGP 9.4 resolves it to the installed `platforms;android-37.0` package. build-tools 37.0.0 is installed too.
- **API 37 emulator problem (blocks on-device tests of targetSdk 37 behaviors):** the `system-images;android-37.0;google_apis_playstore;arm64-v8a` image (rev 6) starts with "hvf is not enabled on this aarch64 host" plus repeated `mprotect failed: Permission denied`, so it runs in software emulation and never finishes booting. This happens on emulator 36.6.11 and after updating to 37.2.12, both inside and outside the command sandbox, on an Apple M4 with macOS 26.6.2. The API 36 images get hardware acceleration in the same setup, and the AVD configs differ only in the system image. Workarounds to try: start `Pixel_9_Pro_API37` from Android Studio's Device Manager, try the non-Play `google_apis` API 37 image, or test target-37 behavior on a real Android 17 phone. Targets 35 and 36 are tested on the API 36 emulators. **Resolved 2026-10-14:** a new AVD, `Pixel_10_Pro` (same Play API 37.0 image, Pixel 10 Pro profile, 20 GB data, `-gpu host`), boots with hardware acceleration in about 8 s when started from the command line. The cause of the earlier failure is unknown; the earlier AVDs were deleted, so it can't be compared. AVDs on this Mac live on the external drive: `~/.android/avd` is a symlink to `/Volumes/external/AndroidStudioDownload/Library/.android/avd`, so Android Studio (which doesn't read `ANDROID_AVD_HOME` from `.zshrc`) and the command line see the same devices.
- **targetSdk 35:** the app already called `WindowCompat.setDecorFitsSystemWindows(window, false)` and applied insets, so enforced edge-to-edge changed nothing visible. Screenshots of home, list, search, details and wish list match target 34, and the bottom bar sits above the 3-button nav bar. The theme `statusBarColor`/`navigationBarColor` and Accompanist `SystemUiController` now do nothing. They get removed with Accompanist in Phase 5 (`enableEdgeToEdge()`).
- **targetSdk 36:** predictive back is on by default. Back from details, list, search results and the WebView works unchanged (the app already used Compose `BackHandler` and `enableOnBackInvokedCallback="true"`).
- **Large screens at targetSdk 36,** simulated on the phone emulator with `adb shell wm density 200` (1024dp wide) and landscape rotation: `dumpsys` shows `ignoreOrientationRequest=true`, so `zedMoviesActivity` runs in landscape despite `screenOrientation="portrait"`. The Compose UI lays out and works, but it's a stretched phone layout (a very wide pager banner, long single-row carousels, a full-width bottom bar). **Follow-up (Phase 7):** adaptive layouts (`material3-adaptive`, grid columns by window width). One ANR ("Waited 5003ms for FocusEvent") happened right after the density and rotation change, with the emulator at load ~12. It didn't recur on two more cold starts, so it's treated as emulator starvation. For reference, cold start is 3–4.3 s on a normal phone display vs 4.3–7 s wide (debug build, busy emulator). Revisit startup time with a release build and Baseline Profiles (Phase 7).
- **Phase 5a:** activity, lifecycle, navigation, hilt-navigation-compose and paging were deliberately left for 5b. Their current versions depend on much newer Compose artifacts, and mixing those with Compose 1.4/1.5 and Accompanist 0.29 is asking for runtime `NoSuchMethodError`s. `appcompat` was declared in both library modules but never used, so it's gone. `adblock-client` no longer depends on core-ktx (unused).
- **Emulator lessons (test environment, not app bugs):**
  - Under host memory pressure, the emulator silently switches to **software rendering** (`gles_mode_selected:swangle`, `vulkan_mode_selected:lavapipe`). That burns the guest CPUs (load 15–24), and the app then hits "start timeout" kills and ANRs. The A/B test confirmed it: the last-passing commit failed in the same state. **Launch the test emulator with `-gpu host`**, e.g. `emulator -avd Pixel_9_Pro_16k -gpu host -no-snapshot-load -no-audio -no-boot-anim`. After that the suite ran in 2m42s instead of 5+ minutes.
  - Stop old Gradle daemons after a Gradle upgrade (the 8.0 and 8.14.4 daemons stayed alive and held memory): `./gradlew --stop`, run with each old wrapper, or kill them.
- **Phase 5b-1 details:**
  - Pager: Accompanist `HorizontalPager(count = …)` → foundation `HorizontalPager` + `rememberPagerState(pageCount = { … })`. The page offset used for the scale/alpha transition is now `PagerState.offsetForPage(page)` = `(currentPage - page) + currentPageOffsetFraction` (in `ui/home/component/Pager.kt`). The indicator reads `currentPageOffsetFraction`.
  - Tab indicator: a private `pagerTabIndicatorOffset(pagerState, tabPositions)` in `TabPager.kt` (same algorithm as Accompanist's), so the indicator still follows the finger mid-swipe.
  - Placeholder: Accompanist `placeholder(visible = true, color, shape)` without highlight just painted the shape instead of the content. Now it's `drawWithContent { drawOutline(...) }` in `Placeholder.kt`.
  - System bars: `enableEdgeToEdge()` in `zedMoviesActivity`. Accompanist's `setSystemBarsColor(color)` is replaced by `SystemBarsAppearance.setIconsFor(color)` (`ui/SystemBarsAppearance.kt`), which keeps its luminance rule for dark/light icons (white icons over the details backdrop, dark elsewhere). Theme `statusBarColor`/`navigationBarColor` removed.
  - **Ripple had to move in 5b-1, not 5b-2:** `RippleTheme`, `LocalRippleTheme` and `rememberRipple` are deprecated at error level in the new Material. `LocalIndication` = material3 `ripple(color = accent)`. `LocalRippleConfiguration` (M3) and the M2 `LocalRippleConfiguration` (for the M2 tabs until 5b-2) carry the custom alpha values.
  - `LocalMinimumTouchTargetEnforcement provides false` → `LocalMinimumInteractiveComponentSize provides Dp.Unspecified`.
  - `material-icons-core` no longer comes in transitively. It's declared explicitly (frozen at 1.7.8, still in the BOM) for `Icons.Rounded.Close`.
  - `ui-util`/`runtime` pins (1.4.3) and `material` pin (1.0.0) removed. Everything Compose comes from the BOM.
  - `run.sh <flow…>` (a partial run) skips the ad-filter checks, which need `05_play`.
- **Phase 5b-2:** `ZedMoviesSwipeRefresh` wraps M3 `PullToRefreshBox` + `PullToRefreshDefaults.Indicator` (same colors and padding). The unused `scale` parameter is gone: the M3 indicator slides in instead of scaling. Tabs use `SecondaryTabRow` + `TabRowDefaults.SecondaryIndicator`, and the pager-following indicator now goes through M3's `Modifier.tabIndicatorLayout`. With no M2 components left, the M2 `LocalRippleConfiguration` and the `androidx.compose.material:material` dependency are removed. `RippleAlpha` still comes from `material-ripple` (via material3).
- **Phase 5b-3:** `hiltViewModel()` moved to `androidx.hilt.lifecycle.viewmodel.compose` (5 screens). Navigation's string routes still compile unchanged on 2.10. paging-compose left its separate alpha track and now shares the paging version. Mechanical M3 renames: `outlinedButtonBorder` → `outlinedButtonBorder(enabled = true)`, `centerAlignedTopAppBarColors` → `topAppBarColors`. `ui/webview/getMiddleScreenPosition.kt` (unused, deprecated `defaultDisplay` API) deleted.
- **Phase 5c:** Coil 3: package `coil` → `coil3`, and `painter.state` is now a `StateFlow`, so `Image.kt` collects it (`painter.state.collectAsState()`) before switching on loading/success/error. `coil-network-okhttp` is required for remote images (it registers itself). The JakeWharton converter is replaced by `com.squareup.retrofit2:converter-kotlinx-serialization` (import `retrofit2.converter.kotlinx.serialization.asConverterFactory`). `ResultAdapterFactory`/`ResultCall` compiled against Retrofit 3 without changes. The Jsoup scraping fallback was kept, just updated, since removing it would change behavior.
- **Phase 6 (CI):** `release.yml` uses `actions/checkout@v7`, `actions/setup-java@v6`, `gradle/actions/setup-gradle@v6` (caching + wrapper validation, replacing `setup-java`'s `cache: gradle`). It installs `platforms;android-37.0`, `build-tools;36.0.0`, `ndk;28.2.13676358` and `cmake;3.22.1`, and has a new **"Check 16 KB page-size alignment"** step (`zipalign -c -P 16` + `llvm-objdump` on every arm64/x86_64 `.so`). The workflow can't run off `master` without publishing, so its steps were reproduced locally: `assembleRelease -PappVersionCode=30000 -PappVersionName=3.0.0` → signed with `CN=zeD, O=NoFlix`, 16 KB step passes, `aapt2 badging` shows versionCode 30000 / 3.0.0 / compileSdk 37 / targetSdk 36. The 16 KB step correctly **fails** on the pre-migration APK (all 4 64-bit `.so` flagged). With `versionName` 3.0.0 above the latest tag (2.1.2), the next master push releases **3.0.0**.
- **Release-build tests:** the signed 3.0.0 release passes all 5 flows on a clean install (`pageSizeCompat=0`, no crashes). `.maestro/upgrade.sh build/baseline/release/NoFlix.apk app/build/outputs/apk/release/app-release.apk` (the public 2.1.2 release downloaded with `gh release download`, then 3.0.0 over it, same key) keeps the wish list. The update keeps `pageSizeCompat=6` (see the open 16 KB finding above). `upgrade.sh` now takes an optional new-APK argument.
- **CLAUDE.md** updated: toolchain, catalog, built-in Kotlin, 16 KB rule, classpath-alignment caveat, repositories, assets-loaded JS, system bars, and the Maestro suite. It also includes the user's own pending edit (the `navigation/` bullet).
- **API 24 (Android 7.0) findings, emulator `Pixel_API24` (`system-images;android-24;google_apis;arm64-v8a`):**
  - **Pre-existing crash, now fixed:** the app crashed about a second after launch on API 24–25 with `NoClassDefFoundError: java/time/LocalDate` (kotlinx-datetime is built on `java.time`, which only exists from API 26). The **pre-migration build dies at the same point**, so the app has never worked on Android 7.x despite `minSdk 24`. Fix: core library desugaring (`coreLibraryDesugaringEnabled true` + `com.android.tools:desugar_jdk_libs:2.1.5`). All 5 UI flows now pass on API 24, and API 36 / 16 KB is unaffected.
  - **Still not working on Android 7.x (platform limits, not fixed):** (1) filter-list downloads fail. `filters.adtidy.org` uses Let's Encrypt's new chain (`YE1` → `Root YE` → `ISRG Root X2`), which Android 7.0's root store doesn't trust, so no ads are blocked. TMDB (Amazon/Starfield chain) works. (2) The player page doesn't load in the API 24 WebView (blank, only `about:blank` scripts ran), for the same TLS reason and/or the 2016 WebView (Chrome 53) on this image. Real 7.x phones usually have an updated WebView from Play, but still use the 7.0 system root store.
  - **Decision for the owner:** either (a) raise `minSdk` to 26, which matches what actually works and lets the desugaring go, or (b) keep 24 for catalog browsing and ship ISRG roots for the filter host via `res/xml/network_security_config.xml` `<trust-anchors>` (fixes filter downloads only, not WebView playback). Option (a) is recommended: playback is the app's purpose, and Android 7.x is a tiny share of devices.
- **Process slip (recorded for honesty):** commit `52a8b4d` (Room schema export) was pushed although its first full-suite run had failed `03_search`. The command chain took its exit status from `tail`, not from `run.sh`. The failure was a test flake (Maestro typed "BBatman"). `03_search` now clears and retypes inside a `retry` block, and the re-run passed 5/5 (exit 0), which validates `52a8b4d`. From now on, commits are gated on `run.sh`'s own exit code.
- **API 30 (Android 11) findings:** everything works. Below API 35, `enableEdgeToEdge()`'s default style plus Android's navigation-bar contrast enforcement (on since API 29) put a whitish scrim behind 3-button navigation, where the old app showed its purple background. `zedMoviesActivity` now passes `navigationBarStyle = SystemBarStyle.auto(TRANSPARENT, TRANSPARENT)` and sets `window.isNavigationBarContrastEnforced = false` (API 29+), so the background shows through as before. `SystemBarsAppearance` still picks dark or light buttons. Re-tested on API 30 and on 16 KB API 36.
- **Disk space:** the external drive filled up (3 GB free) while creating test AVDs. The two API 37 AVDs and the non-Play API 37 image (created this session and unusable on this Mac) were deleted, which freed 13 GB. Test AVDs kept: `Pixel_9_Pro_16k` (API 36, 16 KB), `Pixel_API24`, `Pixel_API30`. The Play API 37 image is kept for an Android Studio attempt.
- Commits: each tested step is committed and pushed to `origin/upgrade`. The first push also included `doc/star-ratings-plan.md` (the user's own file, which happened to be in `doc/`).

---

## 1. Current state → target

### Toolchain

| Item                         | Current                                 | Target                                                            | Notes                                                                   |
| ---------------------------- | --------------------------------------- | ----------------------------------------------------------------- | ----------------------------------------------------------------------- |
| Android Gradle Plugin        | 8.0.2                                   | **9.4.1**                                                         | 9.4 supports up to API 37. Go through 8.13.2 first (Phase 2)            |
| Gradle wrapper               | 8.0                                     | **9.7.1**                                                         | AGP 9.4 needs at least 9.6.0. Kotlin 2.4.20 adds support for Gradle 9.7 |
| Kotlin                       | 1.7.20                                  | **2.4.21**                                                        | K2 compiler. AGP 9 needs KGP 2.2.10 or newer                            |
| Compose compiler             | `kotlinCompilerExtensionVersion 1.3.2`  | `org.jetbrains.kotlin.plugin.compose` (same version as Kotlin)    | Separate Compose compiler versions ended with Kotlin 2.0                |
| Annotation processing        | kapt (Room, Hilt, Mezzanine)            | **KSP 2.3.12**                                                    | kapt doesn't work with AGP 9's built-in Kotlin                          |
| Hilt plugin / library        | 2.44 / 2.46.1 (mismatched)              | **2.60.1** (both)                                                 |                                                                         |
| JDK                          | 17                                      | 17, or 21 if you prefer                                           | AGP 9 and Gradle 9 need at least 17                                     |
| NDK                          | not pinned (CI installs 25.1.8937393)   | **28.2.13676358 (r28c)**, pinned                                  | r28+ builds 16 KB-aligned libraries by default                          |
| CMake                        | 3.10.2, `cmake_minimum_required(3.4.1)` | **3.22.1** (or newer), min 3.22.1                                 | CMake 4 rejects a minimum below 3.5                                     |
| SDK Build Tools              | (AGP default)                           | 36.0.0+                                                           |                                                                         |
| compileSdk / targetSdk (app) | 33 / 33                                 | **37 / 37**                                                       | Android 17 has been stable since June 2026                              |
| compileSdk (libraries)       | 31                                      | **37**                                                            | targetSdk is ignored for libraries. Remove it                           |
| minSdk                       | app 24, libs 23                         | **26 everywhere**                                                 | Android 7.x can't download filters or play (TLS roots)                  |
| Java target                  | app 17, libs 1.8                        | 17 everywhere                                                     |                                                                         |
| Build scripts                | Groovy, versions inline                 | Groovy or KTS + **version catalog** (`gradle/libs.versions.toml`) | Moving to KTS is optional but recommended                               |

### Libraries (`:app`)

| Library                                                           | Current                              | Target                                                                                  | Effort                                                                          |
| ----------------------------------------------------------------- | ------------------------------------ | --------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- |
| Compose BOM                                                       | 2023.05.00                           | **2026.09.00** (UI/foundation 1.12.1, material3 1.4.0)                                  | High (see 6.1)                                                                  |
| `androidx.compose.material:material`                              | pinned 1.0.0 (!)                     | from BOM, then remove                                                                   | Medium                                                                          |
| `ui-util` / `runtime`                                             | pinned 1.4.3                         | from BOM                                                                                | Low                                                                             |
| `androidx.compose.compiler:compiler`                              | 1.4.4 as `implementation` (wrong)    | **remove**                                                                              | Low                                                                             |
| Accompanist pager / indicators / placeholder / systemuicontroller | 0.29.1-alpha                         | **remove** (replaced by foundation pager, custom placeholder and `enableEdgeToEdge`)    | High                                                                            |
| core / core-ktx                                                   | 1.10.1                               | 1.19.1                                                                                  | Low                                                                             |
| activity (+compose)                                               | 1.7.2                                | 1.13.0                                                                                  | Low                                                                             |
| lifecycle                                                         | 2.6.1                                | 2.11.0                                                                                  | Low                                                                             |
| navigation-compose                                                | 2.5.3                                | 2.10.2                                                                                  | Low (string routes still work)                                                  |
| hilt-navigation-compose                                           | 1.0.0                                | **`androidx.hilt:hilt-lifecycle-viewmodel-compose` 1.4.0**                              | Low (import change)                                                             |
| paging-runtime / paging-compose                                   | 3.1.1 / 1.0.0-alpha20                | 3.5.3 / 3.5.3                                                                           | Low (`itemKey` is already used)                                                 |
| paging-rxjava2 / rxjava3 / guava                                  | 3.1.1                                | **remove** (not used)                                                                   | None                                                                            |
| room (runtime/ktx/paging/compiler)                                | 2.5.1 (kapt)                         | 2.8.5 (KSP)                                                                             | Low                                                                             |
| datastore-preferences                                             | 1.1.0-alpha04                        | 1.2.1                                                                                   | Low                                                                             |
| core-splashscreen                                                 | 1.0.1                                | 1.2.0                                                                                   | Low                                                                             |
| webkit                                                            | 1.4.0                                | 1.17.1                                                                                  | Low                                                                             |
| startup-runtime                                                   | 1.1.1                                | **remove** (not referenced. WorkManager pulls it in itself)                             | None                                                                            |
| kotlinx-coroutines                                                | 1.7.1                                | 1.11.0                                                                                  | Low                                                                             |
| kotlinx-serialization-json                                        | 1.5.1                                | 1.11.0                                                                                  | Low                                                                             |
| kotlinx-datetime                                                  | 0.4.0                                | 0.8.0                                                                                   | Low (only `LocalDate` is used. `Instant`/`Clock` moved to `kotlin.time` in 0.7) |
| retrofit                                                          | 2.9.0                                | 3.0.0                                                                                   | Low                                                                             |
| JakeWharton kotlinx converter                                     | 1.0.0 (deprecated)                   | **`com.squareup.retrofit2:converter-kotlinx-serialization` 3.0.0**                      | Low (import change)                                                             |
| okhttp                                                            | 4.11.0 **and** 4.9.1 (duplicate)     | 5.5.0 (one declaration)                                                                 | Low                                                                             |
| coil-compose                                                      | 2.4.0                                | **Coil 3.6.3** (`io.coil-kt.coil3`) + `coil-network-okhttp`, or 2.7.0 as a stopgap      | Medium                                                                          |
| lottie-compose                                                    | 6.1.0                                | 6.7.1                                                                                   | Low                                                                             |
| jsoup                                                             | 1.14.3                               | 1.23.2, or remove along with the scraping fallback                                      | Low                                                                             |
| gson                                                              | 2.8.8                                | **remove** (not used)                                                                   | None                                                                            |
| play-services-basement                                            | 18.2.0                               | **remove** (its only use is a stray unused import in `ContentBlockingWebViewClient.kt`) | None                                                                            |
| kotlin-bom                                                        | 1.8.10 (doesn't match Kotlin 1.7.20) | **remove** (KGP aligns the stdlib)                                                      | None                                                                            |
| junit-ext / espresso                                              | 1.1.5 / 3.5.1                        | 1.3.0 / 3.7.0                                                                           | Low                                                                             |

### Libraries (`:ad-filter`, `:adblock-client`)

| Library                                              | Current                      | Target                                                    | Effort |
| ---------------------------------------------------- | ---------------------------- | --------------------------------------------------------- | ------ |
| Anko commons/design                                  | 0.10.8 (JCenter mirror only) | **remove** (the only use is a wrong `BuildConfig` import) | Low    |
| Mezzanine (kapt, JitPack)                            | 1.2.1                        | **remove**: load the JS from assets/raw (see 5.2)         | Medium |
| Edsuns HttpRequest (JitPack)                         | 0.2                          | **remove**: use OkHttp or `HttpURLConnection`             | Low    |
| kotlin-stdlib                                        | pinned 1.4.21                | remove (KGP adds it)                                      | None   |
| core-ktx / appcompat                                 | 1.3.2 / 1.2.0                | 1.19.1 / 1.8.0, or drop appcompat if unused               | Low    |
| timber                                               | 4.7.1                        | 5.0.1                                                     | Low    |
| kotlinx-coroutines                                   | 1.3.7 (!)                    | 1.11.0 (shared with app via catalog)                      | Low    |
| kotlinx-serialization-json                           | 1.0.1                        | 1.11.0                                                    | Low    |
| work-runtime-ktx                                     | 2.7.1                        | 2.12.0                                                    | Low    |
| Vendored JS (AdGuard Scriptlets / ExtendedCss, 2021) | old                          | optional refresh (Phase 7)                                | Medium |
| Vendored C++ engine (brave/ad-block)                 | deprecated upstream          | keep and rebuild for 16 KB. Long-term option in 4.4       | Medium |

---

## 2. Problems found in the codebase that the upgrade will expose

Each of these breaks the build, or behaves differently, once the new tools or SDK are in place:

1. **All three modules use the namespace `com.zobaer53.zedmovies`.** AGP 9 turns on `android.uniquePackageNames=true` by default, so this fails. Set `:ad-filter` to `io.github.edsuns.adfilter` and `:adblock-client` to `io.github.edsuns.adblockclient`. Neither library imports `R`. JNI symbol names come from the Kotlin class package (`io.github.edsuns.adblockclient.AdBlockClient`), so the native code is unaffected.
2. **`jcenter()` in `settings.gradle`** was removed in Gradle 9. The Aliyun JCenter mirror only exists for Anko. After Anko is gone, delete both, and also JitPack once Mezzanine and HttpRequest are replaced.
3. **kapt everywhere.** With AGP 9's built-in Kotlin, `kotlin-kapt` is an error. Room and Hilt support KSP. Mezzanine (in `:ad-filter`) has no KSP processor, so it has to be replaced.
4. **The `kotlin-android` plugin** (`org.jetbrains.kotlin.android` in app, `apply plugin: 'kotlin-android'` in the libraries) doesn't work with AGP 9's new DSL. Remove it. Change `kotlinOptions { jvmTarget }` to `kotlin { compilerOptions { … } }`, or drop it, since jvmTarget now defaults to `compileOptions.targetCompatibility`.
5. **`ext.kotlin_version = "1.4.21"`** inside `android {}` in `app/build.gradle`, and the `kotlin_version` ext in both library modules, are dead or misleading. Remove them.
6. **`buildFeatures.buildConfig = true` is set inside `defaultConfig`.** Move it into `android { buildFeatures { buildConfig true; compose true } }`.
7. **`ScriptInjection.kt` imports `org.jetbrains.anko.commons.BuildConfig`**, which is Anko's own BuildConfig, so its `DEBUG` flag has never reflected this app's build. Replace it with the module's own `BuildConfig.DEBUG` (turn on `buildConfig` in `:ad-filter`) or pass the flag in from `AdFilter.create`.
8. **Legacy library DSL**: `compileSdkVersion`/`minSdkVersion`/`targetSdkVersion`, `versionCode`/`versionName` in library `defaultConfig`, `packagingOptions`, and `apply plugin:`. Change these to `compileSdk`, `minSdk`, a `plugins {}` block and `packaging {}`, and remove the library version fields.
9. **CMake `cmake_minimum_required(VERSION 3.4.1)`** fails under CMake 4.x and is unsupported by current AGP CMake. Raise it to 3.22.1. Update `externalNativeBuild.cmake.version` to match.
10. **The native `.so` isn't guaranteed to be 16 KB aligned** (NDK 25 and no linker flags). On Android 15+ devices with 16 KB pages it runs in compat mode with a warning, and future releases will refuse it. Rebuild with NDK r28+ and check the alignment.
11. **Theme `android:statusBarColor` / `navigationBarColor`** (`values/themes.xml`, `values-night/themes.xml`) do nothing once targetSdk is 35 or higher (edge-to-edge is enforced). Accompanist `SystemUiController` (`ZedMoviesAppState.kt`) is deprecated and has been removed upstream.
12. **Orientation locks** (`android:screenOrientation="portrait"` on `zedMoviesActivity` and `requestedOrientation` in `WebView.kt`) are **ignored on displays with smallest width ≥ 600dp** at targetSdk 36. The temporary opt-out is **gone at targetSdk 37**. Tablets and foldables will rotate both activities freely.
13. **`WebViewActivity` fallback branch** calls `VideoWebView(url = this, lifecycleOwner = WebViewActivity())`, which constructs an Activity by hand and is a latent crash. It also uses Jsoup scraping of a legacy site. Pass `this` as the lifecycle owner, or remove the fallback (Phase 6.6).
14. **Dead code in `WebView.kt`**: an `OkHttpClient` with a hard-coded HTTP proxy IP (`91.211.245.176:8080`) is built and never used. Delete it, and the commented-out `AdBlockingWebViewClient`.
15. **`WebSettingsCompat.setForceDark`** is deprecated and does nothing for targetSdk 33+. Use `WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, false)` to keep the current "no forced dark" behavior.
16. **Material 2 APIs mixed into a Material 3 app**: `RippleTheme`/`LocalRippleTheme`/`rememberRipple` (`Ripple.kt`, `Theme.kt`), `pullRefresh` (`SwipeRefresh.kt`), M2 `TabRow`/`Tab`/`Text` (`TabPager.kt` and others). New Material versions **ignore** `LocalRippleTheme`, so the custom ripple silently stops working.
17. **`gradle.properties`**: `android.enableJetifier=true` should be unnecessary once Anko is gone (check with `./gradlew checkJetifier`). Jetifier slows builds and is being phased out. `android.useAndroidX` is now the default. `org.gradle.configureondemand` doesn't work with the configuration cache, so replace it with `org.gradle.configuration-cache=true`.

---

## 3. Phase 0: baseline (no behavior changes)

- [ ] Branch from `master`. All work stays on `upgrade` until Phase 8 passes.
- [x] Make sure the current project builds: `./gradlew :app:assembleDebug :app:assembleRelease`.
  > **Done:** debug build verified.
- [x] Record a **manual smoke-test baseline** on a real phone running the current release: home, list, search, details, wishlist, Play → video plays full screen in landscape, ads blocked, Back behavior. Take screenshots for comparison.
  > **Done:** automated as the Maestro suite (screenshots under `build/maestro/`).
- [ ] Add **Dependency Guard** (`com.dropbox.dependency-guard` 0.5.0) on `releaseRuntimeClasspath`. Commit the baseline `app/dependencies/releaseRuntimeClasspath.txt` (use `git add -f`, because `/app/` is gitignored).
- [ ] Create a **lint baseline** (`lint { baseline = file("lint-baseline.xml") }`) so new warnings are visible.
- [x] Save `./gradlew :app:dependencies > /tmp/deps-before.txt` to diff later.
  > **Done:** saved (releaseRuntimeClasspath) in the session scratchpad.
- [x] Keep a copy of the current release APK (`2.1.x`) to test upgrade-in-place in Phase 8.
  > **Done:** baseline debug APK kept and used by `.maestro/upgrade.sh`. Keep the 2.1.x release APK for the final signed-release check.

## 4. Phase 1: clean up before upgrading (still on AGP 8.0.2)

Small, low-risk commits that shrink the upgrade surface:

- [x] Remove unused dependencies: gson, paging-rxjava2, paging-rxjava3, paging-guava, startup-runtime, play-services-basement (and the stray `com.google.android.gms.common.api.Response` import), `okhttp:4.9.1` (keep one OkHttp), `androidx.compose.compiler:compiler` (implementation), `hilt-core` (pulled in by `hilt-android`), `kotlin-bom:1.8.10`.
- [x] Remove `ext.kotlin_version` from all three build files.
- [x] Delete the unused proxy `OkHttpClient` in `WebView.kt` and the commented-out client.
- [x] Fix the `WebViewActivity()` lifecycle-owner bug (pass `this`).
- [x] Introduce **`gradle/libs.versions.toml`** and move every version there. App and library modules then share one coroutines, serialization, OkHttp and core version.
- [ ] (Optional, recommended) Convert `settings.gradle`, `build.gradle` and the module scripts to **Kotlin DSL** (`.gradle.kts`). AGP 9's new DSL is typed, and errors are much clearer in KTS.
- [x] Build and smoke-test. Then run `dependencyGuard` and review the diff.
  > **Done:** Maestro 5/5. Dependency diff reviewed with `gradle dependencies`.

## 5. Phase 2: build toolchain (step by step, building after each)

Each step changes **build files only**. Fix compile errors as they appear, but don't change behavior.

### 5.1 Step A: AGP 8.13.2 + Gradle 8.14.x + Kotlin 2.2.x (the last 8.x line)

Going to the last AGP 8 release first gives deprecation **warnings** for everything AGP 9 turns into **errors**. Run Android Studio's **Tools → AGP Upgrade Assistant**.

- [x] Gradle wrapper → 8.14.x
  > **Done:** 8.14.4. (`./gradlew wrapper --gradle-version 8.14.3`).
- [x] AGP 8.0.2 → 8.13.2 (`com.android.application`/`com.android.library`).
- [x] Kotlin 1.7.20 → 2.2.x
  > **Done:** 2.3.21 (see notes: Hilt 2.57.2 limits it). (or 2.4.21 directly if it supports AGP 8.13). Apply `org.jetbrains.kotlin.plugin.compose` and delete `composeOptions { kotlinCompilerExtensionVersion }`.
- [x] Serialization plugin: use `org.jetbrains.kotlin.plugin.serialization` at the Kotlin version in **both** `:app` and `:ad-filter` (`:ad-filter` uses the old `kotlinx-serialization` id).
- [x] **Namespaces**: `:ad-filter` → `io.github.edsuns.adfilter`, `:adblock-client` → `io.github.edsuns.adblockclient`.
- [x] **kapt → KSP**
  > **Done:** Room and Hilt on KSP 2.3.12. Schema export not enabled yet. (`com.google.devtools.ksp`):
  - Room: `ksp("androidx.room:room-compiler")`. **(Schema export done later: `exportSchema = true`, `room { schemaDirectory "$projectDir/schemas" }`, schema v2 committed.)** Optionally add the Room Gradle plugin (`androidx.room`) with `schemaDirectory("$projectDir/schemas")` and set `exportSchema = true`, so future schema changes get real migrations. The DB is version 2 with no migrations and no destructive fallback, so any accidental schema change would crash on upgrade.
  - Hilt: plugin and library 2.60.1, `ksp("com.google.dagger:hilt-android-compiler")`.
  - `:ad-filter` still uses kapt for Mezzanine at this point. That's fine on AGP 8.13. It goes away in Phase 3.
- [x] Replace `kotlinOptions` with `kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_17 } }`.
- [x] Libraries: `compileSdk 31`
  > **Done:** Java 17 and `packaging` done; library compileSdk still 31 (moves in Phase 3). → 37 (or 36 until Phase 4), Java 1.8 → 17, `packagingOptions` → `packaging`.
- [x] Fix new **K2 compiler** errors and warnings. K2 is stricter about smart casts, deprecated-level errors and visibility.
- [x] Build, smoke-test, commit.
  > **Done:** built, Maestro 5/5, upgrade-in-place passed. Not committed.

### 5.2 Step B: AGP 9.4.1 + Gradle 9.7.1 + Kotlin 2.4.21

- [x] Gradle wrapper → 9.7.1 (AGP 9.4 needs at least 9.6.0).
- [x] AGP → 9.4.1.
- [x] **Built-in Kotlin**: remove `org.jetbrains.kotlin.android` / `kotlin-android` from every module and from the root `plugins {}`. Keep the Compose and serialization compiler plugins and KSP.
- [x] **No kapt left.** If Mezzanine isn't replaced yet, use `com.android.legacy-kapt` (same version as AGP) in `:ad-filter` as a temporary bridge. The real fix is in Phase 3.
- [x] Remove `jcenter()` from both repository blocks (Gradle 9 removed it).
  > **Done:** in the ad-filter step, along with the Aliyun mirror and JitPack.
- [x] Check the AGP 9 default changes against this project:
  - `android.uniquePackageNames=true` → fixed in Step A.
  - `android.newDsl=true` → there are no `applicationVariants`/`variantFilter` uses, so no change needed.
  - `android.sdk.defaultTargetSdkToCompileSdkIfUnset` → set targetSdk explicitly anyway.
  - `android.defaults.buildfeatures.resvalues=false` → no `resValue` uses. OK.
  - `android.onlyEnableUnitTestForTheTestedBuildType=true` → unit tests run only for debug. OK.
  - `android.r8.*` strict defaults → `minifyEnabled false` today. If R8 gets turned on later (recommended, but as a separate change), note that `-keep class A` no longer keeps the default constructor.
  - `getDefaultProguardFile('proguard-android-optimize.txt')` is still allowed. `proguard-android.txt` is not.
- [x] `gradle.properties`: remove `android.useAndroidX` (now the default). Remove `android.enableJetifier` after `./gradlew checkJetifier` reports nothing. Replace `org.gradle.configureondemand=true` with `org.gradle.configuration-cache=true`. Raise `org.gradle.jvmargs` to `-Xmx4g`.
- [x] Don't add the `android.builtInKotlin=false` / `android.newDsl=false` opt-outs. AGP 10 removes them.
- [x] Build, smoke-test, `dependencyGuard`, commit.
  > **Done:** Maestro 5/5, upgrade-in-place passed, compile/runtime classpath diff reviewed.

## 6. Phase 3: modernize the vendored ad-blocking modules

### 6.1 `:adblock-client` (native engine): required for 16 KB devices

- [x] Pin `ndkVersion = "28.2.13676358"` (AGP 9.4's default, r28c). r28+ links with 16 KB max-page-size by default.
- [x] `CMakeLists.txt`: `cmake_minimum_required(VERSION 3.22.1)`. Set `externalNativeBuild.cmake.version` to `"3.22.1"`.
- [ ] Optional: set `abiFilters` to `arm64-v8a`, `armeabi-v7a`, `x86_64` (drop 32-bit x86) to save APK size.
- [x] Fix any new clang warnings or errors from the newer NDK in `third-party/ad-block` (old C++; watch for `-Wdeprecated`, implicit conversions and `register` keywords). Keep `parse()` inputs NUL-terminated, as CLAUDE.md warns.
- [x] **Check alignment** on the release APK:
  - `zipalign -c -P 16 -v 4 app-release.apk` → "Verification successful".
  - `llvm-objdump -p lib/arm64-v8a/libadblock-client.so | grep LOAD` → every segment `align 2**14`.
  - Or Studio's **Build → Analyze APK** (Alignment column), or Google's `check_elf_alignment.sh`.
- [x] Run on a **16 KB page-size emulator image** (Android 15+ "16 KB Page Size" system image) and confirm there is no compat-mode warning dialog.
- [x] Android 17 checks (verified): no `SetStatic*Field` JNI writes in the C++ (the static-final restriction doesn't apply). The library loads with `System.loadLibrary` from the APK, so the new native dynamic-code-loading rule doesn't apply.

### 6.2 `:ad-filter`

- [x] **Remove Anko**: fix the `BuildConfig` import in `ScriptInjection.kt` (problem 7) and delete both Anko dependencies. Then delete the Aliyun mirror from `settings.gradle` and update CLAUDE.md, which currently says not to remove it.
- [x] **Replace Mezzanine.** It reads six JS files at compile time through `@FileStream`: `inject.js`, `scriptlets.min.js`, `scriptlets_inject.js`, `elemhide_blocked.js`, `extended-css.min.js` and `element_hiding.js`. The simplest replacement:
  1. Move `src/main/js/*.js` to `src/main/assets/adfilter/` (or `res/raw`).
  2. Add a small `JsAssets` loader that reads each file once (`context.assets.open(...).bufferedReader().readText()`) and caches it. Both `AdFilter.create(context)` and `ElementHiding`/`Scriptlet` already have a `Context`.
  3. Replace `MezzanineGenerator.X().js()` calls and delete the `@FileStream` interfaces, the `kapt {}` block and the Mezzanine dependencies.

  (The alternative is a Gradle task that generates Kotlin `const val` strings from the JS files. It's more build code for no runtime benefit.)

- [x] **Replace Edsuns `HttpRequest`** in `DownloadWorker.kt` with OkHttp (catalog version, shared with app) or `HttpURLConnection`. Keep the 10 s timeout.
- [x] Once both are gone, **remove JitPack** from `settings.gradle`, unless something else still needs it (check with `./gradlew :app:dependencies`).
- [x] Bump timber 5.0.1, coroutines 1.11.0, serialization 1.11.0, work 2.12.0 and core-ktx 1.19.1. Remove `kotlin-stdlib`. Remove `appcompat` if nothing in the module uses it.
- [ ] Optional: change `DownloadWorker`/`InstallationWorker` from `Worker` + `runBlocking` to `CoroutineWorker`.
- [ ] Confirm filters still download and install on a **fresh install** (WorkManager), and that blocking and cosmetic hiding still work on the streaming page.

### 6.3 Long-term option (not part of this migration)

`brave/ad-block` (the C++ engine vendored in `:adblock-client`) is deprecated upstream in favor of Brave's `adblock-rust`, and Edsuns/AdblockAndroid has no releases. Keeping the vendored copy is fine once it's rebuilt for 16 KB. Moving to `adblock-rust` through JNI would bring current filter-syntax support, but it's a separate project with real effort: Rust NDK toolchain, new JNI bindings and serialization format.

## 7. Phase 4: compileSdk / targetSdk 33 → 37

Use **Tools → Android SDK Upgrade Assistant**. It walks through each API level. Raise `compileSdk` to 37 first and build. Then raise `targetSdk` **one level at a time** (34 → 35 → 36 → 37), testing after each, because each level turns on new behavior.

### API 34 (Android 14)

Little impact. The app has no foreground services, broadcast receivers, implicit internal intents or exact alarms. `WebViewActivity` is launched with an explicit intent. Nothing to change. Smoke-test only.

### API 35 (Android 15): edge-to-edge enforced

- [x] Call `enableEdgeToEdge()` (activity 1.13) in `zedMoviesActivity.onCreate` in place of `WindowCompat.setDecorFitsSystemWindows(window, false)`. Delete Accompanist `SystemUiController` from `ZedMoviesAppState.kt`. Set light or dark status/navigation-bar icons with `enableEdgeToEdge(SystemBarStyle.auto(...))` or `WindowInsetsControllerCompat.isAppearanceLightStatusBars`.
- [x] Remove `android:statusBarColor` / `android:navigationBarColor` from `values/themes.xml` and `values-night/themes.xml`. They do nothing now.
- [ ] Check that every screen handles insets: the top of home/details under the status bar, the bottom bar (`ZedMoviesBottomBar.kt`) above the gesture/3-button nav bar, the search field, and dialogs. M2 components don't apply insets automatically. Use `Modifier.windowInsetsPadding(WindowInsets.systemBars)` or `Scaffold(contentWindowInsets = …)`.
- [x] `WebViewActivity`: `LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES` is now treated as `ALWAYS`. Check that full-screen landscape video still looks right on a phone with a notch or punch-hole. Checked on the Pixel 10 Pro profile (Android 17), see the API 37 notes.
- [ ] OpenJDK changes: there are no `removeFirst()`/`removeLast()` calls (verified). `String.format` validation is stricter, so check any format strings.
- [ ] TLS 1.0/1.1 are disallowed. TMDB and AdGuard filter hosts use TLS 1.2+, so this is expected to be fine.

### API 36 (Android 16)

- [x] **Edge-to-edge opt-out gone.** Don't add `windowOptOutEdgeToEdgeEnforcement`.
- [x] **Predictive back on by default.** `onBackPressed()` is no longer called and `KEYCODE_BACK` is no longer dispatched. The app already uses Compose `BackHandler` (`WebView.kt`) and `enableOnBackInvokedCallback="true"`, which are compatible. Test: Back exits full-screen video, then goes back in WebView history, then closes the activity. Check that the back-to-home animation doesn't fire while a custom view is showing.
- [x] **Orientation and resizability ignored on sw ≥ 600dp** (tablets, unfolded foldables, desktop windowing):
  - `zedMoviesActivity` (`screenOrientation="portrait"`) will rotate and resize. Confirm the Compose layouts stay usable in landscape and wide windows (lists and grids, details header), and that state survives the recreation (ViewModels, `rememberSaveable`, pager position).
  - `WebView.kt`'s `requestedOrientation` lock does nothing on those devices. That's acceptable because the video still fills the screen. `WebViewActivity` already declares `configChanges` for orientation, screen size and layout, so the page won't reload.
  - Phones (sw < 600dp) keep the current portrait/landscape locking.
- [ ] `elegantTextHeight` is ignored. Only matters for some scripts (Thai, Tamil, …), so spot-check if needed.
- [ ] Local network protection is opt-in at 36. Not relevant: the app only uses internet hosts.

### API 37 (Android 17)

- [x] **No orientation opt-out at all** for sw ≥ 600dp (already handled in API 36 above). Don't rely on `PROPERTY_COMPAT_ALLOW_RESTRICTED_RESIZABILITY`. Nothing in the app uses it.
- [x] **Certificate Transparency on by default** and **ECH** for WebView/OkHttp. Test that TMDB, image CDNs, AdGuard filter downloads and the streaming site's embedded player hosts still load. A host with non-CT certificates fails, so look for `net::ERR_CERTIFICATE_TRANSPARENCY_REQUIRED` in `onReceivedError` logs. **Verified at target 37 on Android 17:** TMDB and images load, all 6 filter lists download on a clean install, and a DevTools network trace of the player page (via the debug WebView's `webview_devtools_remote_<pid>` socket) shows every site, API and stream-provider request completing over TLS. The only failures are analytics the ad filter blocks on purpose. No `net::ERR_*` in logcat.
- [x] **Background audio hardening**: playback and audio focus outside a valid lifecycle fail silently. NoFlix only plays video in the foreground, so check that pausing and resuming the activity and the lock screen behave the same as before. **Checked:** Home → back to the app, and screen off → on with the player open: same process, no page reload, still landscape, no crash. Audio itself couldn't be checked because no stream played that day (see the note below).
- [x] **Display cutout** (`WebViewActivity`, `SHORT_EDGES` now treated as `ALWAYS`): on the Pixel 10 Pro profile (punch-hole) the landscape player fills the screen and nothing important sits under the cutout.
- **Note (2026-10-14), streams stuck on "Connecting…":** every title tried (a new 2026 film and The Dark Knight), on two servers, stayed on "Connecting… Establishing stream connection" until the player's 12 s timeout. That happened with target 36 **and** target 37, and **in Chrome** on the same emulator. The trace shows one stream-source request taking 17 s and another never answering, and that provider also timed out (40 s, no bytes) from the Mac. So it's upstream, not the app. Retest playback when the providers recover.
- **minSdk 26 notes (2026-10-14):**
  - `minSdk 26` in `:app`, `:ad-filter` and `:adblock-client`. Core library desugaring and `desugar_jdk_libs` are gone (`java.time` exists from 26). `mipmap-anydpi-v26` → `mipmap-anydpi`. README, the site (`docs/index.html`, including the JSON-LD `operatingSystem`) now say Android 8.0+. The site changes go live when this reaches `master`.
  - Lint (`lintDebug`, all three modules) reports no `NewApi` issue from dropping desugaring. Its 4 errors predated the migration and are **fixed** (0 errors now): the two `ContextCastToActivity` use `LocalActivity`, and the splash theme now extends core-splashscreen's `Theme.SplashScreen` (with `postSplashScreenTheme`) instead of setting the API 31 `android:windowSplashScreen*` attributes. That was a real bug: `installSplashScreen()` found none of its attributes, so Android 8–11 had no splash. Now the icon on `primaryDark` shows there too (checked on API 26).
  - Tested on the Pixel 6a (6/6, all checks) and on a new `Pixel_API26` emulator (`system-images;android-26;google_apis;arm64-v8a`, Pixel 9 Pro screen; the old 1080×1920 `pixel` profile is too short for `01_home`). On API 26 all 6 flows pass, and the filter lists download (they couldn't on 7.x), but the image's built-in **WebView 58** can't be updated (no Play Store) and renders the stream page and YouTube black. So `run.sh`'s element-hiding check fails there. Real Android 8 phones update WebView from Play (Chrome/WebView support for Android 8–9 ended with 138).
  - **Filter-worker deadlock, found on API 26 and fixed:** if the process dies while a filter list is downloading, WorkManager resumes the work in the next process. The worker's constructor called `AdFilter.get(context)`, which (holding the singleton lock) builds `FilterViewModelImpl`, whose init calls `getWorkInfosForUniqueWork(id).get()`. That query is queued on WorkManager's serial executor behind the worker itself, so it never finishes, and the next `WebViewActivity.onCreate` (`AdFilter.create`) waits for the lock → **ANR**. Reproduced 2/2 with the old build (kill -9 right after opening the player, relaunch, wait 30 s, open the player again). Fix: the workers only need the data store, so they open `BinaryDataStore.of(context)` and never create `AdFilter` (which also must not be created off the main thread: `AdFilterImpl` calls `observeForever`). With the fix: 3/3 no ANR, including a run where the resumed workers ran. The bug predates the migration and affects every Android version; it just never showed on 7.x because the downloads failed there.
  - **Filter install out of memory, fixed:** `InstallationWorker` read a whole list into a ByteArray and a String and normalized it with regexes (the 11.8 MB Tracking Protection list became 23.6 MB allocations, ~70 MB in all), and the processed data (up to 27 MB) went back through a Java `byte[]`. On the API 26 emulator (48 MB heap growth limit) that logged 9 `OutOfMemoryError`s and some lists never installed. Now nothing big touches the Java heap: `DownloadWorker` streams the body to the file, `Checksum.of(InputStream)` computes the same normalized MD5 in one streaming pass (and hands the `!` comment lines to the title/license regexes), and `AdBlockClient` gained `loadBasicData(File)`, `saveProcessedData(File)` and `loadProcessedData(File)`, which do the I/O in native code. Files are written through a temp file and renamed, so a killed process can't leave a truncated list for the native parser. Verified: `ChecksumTest` (JVM) matches the old implementation on edge cases (CRLF, blank lines, whitespace lines before the checksum, a checksum line in the middle, no trailing newline, non-ASCII) and a 200k-line list, and the streaming checksum validated all 6 live AdGuard lists; `AdBlockClientTest` has a file round trip (12/12 on the Pixel 6a); on API 26 all 6 lists install with 0 `OutOfMemoryError`.
  - `run.sh`: the first `adb logcat -c` after an API 26 boot sometimes fails ("failed to clear the 'main' log"); it now retries once.
- **Note (2026-10-14), emulator GL crash on video:** on the API 37 emulator (`-gpu host`, WebView 145.0.7632.218) actual video playback (the trailer work hit it) kills the WebView GPU process (`CrGpuMain`): `[FATAL:ui/gl/gl_version_info.cc:73] Chrome runs only on top of OpenGL ES through either ANGLE or native: VERSION = `, then SIGTRAP. Chrome's GPU process fails the same way, and the emulator itself warns "Guest Angle is still unstable for API > 35", so this is the emulator's GL layer, not the app. The Maestro suite didn't hit it only because no stream started that day. **Resolved (2026-10-14):** the real cause is the emulator's GLES version. With `-gpu host` on macOS the host translator sits on OpenGL 4.1 and only offers **ES 3.0** (`ro.opengles.version=196608`, `ANDROID_EMU_gles_max_version_3_0`). Android 17 requires ES 3.1 (the emulator log says so), and Chromium 145 asks for an ES 3.1 context for some content: logcat shows `GFXSTREAM: EGL_BAD_CONFIG: no ES 3.1 support` → `eglCreateContext failed` → the FATAL. A plain MP4 plays in Chrome; a YouTube page crashes its GPU process. Starting the emulator with **`-gpu host -feature GuestAngle`** (ANGLE in the guest, over Vulkan/MoltenVK) gives ES 3.1 (`196609`). Then YouTube plays in Chrome, and the Maestro suite passes 6/6 on `upgrade` HEAD (9c1e685, debug, clean worktree), with the trailer playing in landscape (VP9 via `c2.goldfish.vp9.decoder`), no FATAL or `EGL_BAD_CONFIG` in the whole-run logcat, and the 16 KB, filter-list and element-hiding checks OK. The stream itself still stops at "Connecting…" (upstream, see the previous note), so real stream playback on Android 17 is still unverified.
- [ ] **`ACCESS_LOCAL_NETWORK`** is enforced. The app doesn't access the LAN, so no change. (WebView inherits the app's permission state. A player that tries LAN casting would be blocked, which is acceptable.)
- [ ] **Cleartext deprecation** (all apps): there are no hard-coded `http://` URLs in Kotlin (verified). Consider adding `res/xml/network_security_config.xml` with cleartext disabled, so this is explicit before `usesCleartextTraffic` is removed.
- [ ] **App memory limits** (all apps): WebView video plus filter lists is the heaviest path. Watch `ApplicationExitInfo` and logcat for `MemoryLimiter` kills during long playback.
- [ ] Not applicable (checked): widgets/RemoteViews, SMS OTP, contacts provider, Bluetooth RFCOMM, `MessageQueue` reflection, `static final` reflection or JNI writes, `IntentSender` background activity launches.

### WebView-specific

- [x] `WebSettingsCompat.setForceDark(…, FORCE_DARK_OFF)` → `WebSettingsCompat.setAlgorithmicDarkeningAllowed(settings, false)` (guard with `WebViewFeature.ALGORITHMIC_DARKENING`).
- [ ] `addDocumentStartJavaScript` is unchanged in webkit 1.17. Keep the `DOCUMENT_START_SCRIPT` feature check.
- [ ] `setWebContentsDebuggingEnabled` stays limited to debuggable builds (already done).

## 8. Phase 5: app library upgrades with code changes

Do these as separate commits, roughly in this order. Each one should build and run.

### 8.1 Compose BOM 2023.05.00 → 2026.09.00 and leaving Material 2

- [x] Use the BOM for every `androidx.compose.*` artifact. Remove the pinned `material:1.0.0`, `ui-util:1.4.3` and `runtime:1.4.3`.
- [x] **Ripple** (`designsystem/theme/Ripple.kt`, `Theme.kt`): `RippleTheme`, `LocalRippleTheme` and `rememberRipple` are ignored or deprecated. Replace with `ripple(bounded, radius, color)` from material3. For theme-wide colors, either set `LocalRippleConfiguration` around the tree, or build a custom `IndicationNodeFactory` with `createRippleModifierNode()` as Google's ripple migration guide describes. `LocalUseFallbackRippleImplementation` exists only as a temporary bridge.
- [x] **Pull to refresh** (`designsystem/component/SwipeRefresh.kt`): M2 `pullRefresh`/`PullRefreshIndicator` → M3 `PullToRefreshBox` (or `Modifier.pullToRefresh` + `PullToRefreshDefaults.Indicator`).
- [x] **Tabs** (`smallcomponent/TabPager.kt`): M2 `TabRow`/`Tab`/`TabRowDefaults.Indicator`/`Divider` → M3 `PrimaryTabRow` or `SecondaryTabRow` with `TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(selectedIndex))`.
- [x] M2 `Text` (3 files) → M3 `Text`, styled with `zedMoviesTheme.typography`.
- [x] Icons: `Icons.Rounded.Close` comes from `material-icons-core`. Either add `androidx.compose.material:material-icons-core` explicitly (the icon artifacts are no longer actively developed), or switch to a vector drawable / Material Symbols. After this, **drop `androidx.compose.material:material`** entirely.
- [x] Remove the `ExperimentalMaterialApi` opt-ins and update any other `@OptIn` annotations whose APIs became stable.

### 8.2 Accompanist → Compose foundation

- [x] **Pager** (`ui/home/component/Pager.kt`, `smallcomponent/TabPager.kt` and callers): `com.google.accompanist.pager.*` → `androidx.compose.foundation.pager.HorizontalPager` / `rememberPagerState(pageCount = { n })`.
  - `HorizontalPager(count = n)` → page count now goes in `rememberPagerState { n }`.
  - `PagerScope` / `calculateCurrentOffsetForPage(page)` → `(pagerState.currentPage - page) + pagerState.currentPageOffsetFraction`, or `pagerState.getOffsetDistanceInPages(page)`.
  - `pagerState.currentPageOffset` → `currentPageOffsetFraction`.
  - `pagerTabIndicatorOffset` → M3 tab indicator offset (8.1).
  - Remove the `ExperimentalPagerApi` opt-ins.
- [x] **Indicators**: `DefaultHorizontalPagerIndicator` in `Pager.kt` is already custom. Just point it at the foundation `PagerState`.
- [x] **Placeholder** (`designsystem/component/Placeholder.kt`): Accompanist placeholder is deprecated. Write a small `Modifier.placeholder(visible, color, shape, highlight)` using `drawWithContent` and an `InfiniteTransition` shimmer (about 60 lines), or copy the Apache-2.0 Accompanist implementation into the design system.
- [x] **SystemUiController** → `enableEdgeToEdge()` (Phase 4, API 35).
- [x] Remove all `com.google.accompanist` dependencies.

### 8.3 Navigation, Hilt and lifecycle

- [x] navigation-compose 2.5.3 → 2.10.2. The string routes with `navArgument` (`DetailsDestination.kt`, `ListDestination.kt`) still compile. Moving to type-safe `@Serializable` routes is optional (a later refactor). Navigation 3 is out of scope.
- [x] `hilt-navigation-compose` → `androidx.hilt:hilt-lifecycle-viewmodel-compose:1.4.0`. Change `import androidx.hilt.navigation.compose.hiltViewModel` to `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel` in `DetailsScreen`, `HomeScreen`, `ListScreen`, `SearchScreen` and `WishlistScreen`.
- [x] lifecycle 2.11.0: if `LocalLifecycleOwner` is ever needed, import it from `androidx.lifecycle.compose`. The `compose.ui.platform` one is deprecated. None are used today.

### 8.4 Paging, Room, DataStore

- [x] paging-runtime and paging-compose → 3.5.3. `Container.kt` already uses `items(count, key = itemKey(), contentType = itemContentType())`, so expect only small changes.
- [x] Room 2.8.5 with KSP (done in Phase 2). Rebuild. The generated code changes, but the schema must not. Confirm `ZedMoviesDatabase` stays at `version = 2` with identical entities. Phase 8 tests upgrade-in-place.
- [x] DataStore 1.1.0-alpha04 → 1.2.1. API compatible.
  > **Done early** in the native step: its 1.1.0-alpha04 native library was the remaining 16 KB blocker. Preferences survive the update-in-place test.

### 8.5 Networking and serialization

- [x] Retrofit 2.9.0 → 3.0.0. Swap `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter` for `com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0`, and change the `asConverterFactory` import to `retrofit2.converter.kotlinx.serialization.asConverterFactory`. Check that the custom `ResultAdapterFactory`/`ResultCall`/`CallDelegate` still compile against Retrofit 3's `Call`/`CallAdapter`.
- [x] OkHttp → 5.5.0, one declaration in the catalog. The interceptor (`ZedMoviesAuthInterceptor`) and `toMediaType()` are unchanged in 5.x.
- [x] kotlinx-serialization 1.11.0, coroutines 1.11.0, datetime 0.8.0. The app only uses `LocalDate`, `toLocalDate()` and `toJavaLocalDate()`, so replace any deprecated `String.toLocalDate()` with `LocalDate.parse()` if the compiler flags it.

### 8.6 Images, animation and the remaining libraries

- [x] **Coil 2.4 → 3.6.3** (`designsystem/component/Image.kt` and any `AsyncImage` users): artifacts move to `io.coil-kt.coil3:coil-compose` + `io.coil-kt.coil3:coil-network-okhttp`. Package `coil` → `coil3` (`coil3.compose.SubcomposeAsyncImage`, `coil3.request.ImageRequest`, `coil3.request.crossfade`). Network loading needs the network artifact, or images silently fail to load. Follow the official guide at coil-kt.github.io/coil/upgrading_to_coil3/. To move faster, go to 2.7.0 now and Coil 3 in a later commit.
- [x] Lottie 6.7.1, splashscreen 1.2.0, webkit 1.17.1, core 1.19.1, activity 1.13.0.
  > **Partly done (5a):** splashscreen, webkit, core done. Lottie and activity go in 5b/5c. API compatible.
- [ ] Jsoup → 1.23.2, or, better, **remove the scraping fallback**. `WebViewActivity` only uses it when `tmdbId == 0`, which the Play button shouldn't send. Removing it drops Jsoup, `RunOn Background.kt` and `scrapeMovieData()`. Update CLAUDE.md if you do.
- [x] Tests: junit-ext 1.3.0, espresso 3.7.0.

## 9. Phase 6: CI and release workflow (`.github/workflows/release.yml`)

- [x] SDK components: replace `ndk;25.1.8937393` and `cmake;3.10.2.4988404` with `ndk;28.2.13676358`, `cmake;3.22.1`, `platforms;android-37` and `build-tools;36.0.0`.
- [x] Use `gradle/actions/setup-gradle` for caching and wrapper validation, instead of `setup-java`'s `cache: gradle`. Update action major versions (`actions/checkout`, `actions/setup-java`) to the current ones.
- [x] JDK: Temurin 17 still works. Moving to 21 is optional.
- [x] Add a **16 KB check** after the build: `zipalign -c -P 16 -v 4 app-release.apk` (from the newest build-tools), and fail the job if it fails.
- [x] Keep the `apksigner … CN=zeD, O=NoFlix` check. The signing key must not change, or users can't update in place.
- [ ] Optional: run `./gradlew lint :app:testDebugUnitTest` before `assembleRelease`.
- [x] Bump `versionName` in `app/build.gradle` to **3.0.0** so CI releases a new major line for the platform jump. The `sed` in the workflow reads `?: "X.Y.Z"`, so keep that pattern if the file becomes KTS, or update the regex.
- [ ] Release notes: describe it as "updated for Android 17, 16 KB page-size support, large-screen support". Don't name the site.

## 10. Phase 7: optional improvements (after the migration ships)

- Refresh the vendored **AdGuard Scriptlets** and **ExtendedCss** JS (`scriptlets_src` is built with gulp 4 / uglify-es from 2021). Newer AdGuard filter lists use scriptlet names and syntax the old bundle doesn't know. ExtendedCss v2 changed its API, so `element_hiding.js` needs updating along with it.
- Review the filter subscription list in `WebView.kt` (for example, whether "AdGuard Chinese" is needed).
- Turn on **R8** (`minifyEnabled true`, `shrinkResources true`) with keep rules for kotlinx-serialization models, Retrofit interfaces, the JNI class `AdBlockClient` and the `@JavascriptInterface` bridges in `ad-filter`. This makes the APK much smaller, but needs careful testing.
- Baseline Profiles (`androidx.profileinstaller` / `benchmark` 1.5.0) for faster startup.
- Type-safe navigation routes. Adaptive layouts (`material3-adaptive`) now that large screens can't be locked to portrait.
- Point Renovate or Dependabot at the version catalog for routine updates.

## 11. Phase 8: test matrix (must pass before merging to `master`)

**Devices/emulators**

| Config                                         | Why                                        |
| ---------------------------------------------- | ------------------------------------------ |
| API 26 emulator                                | minSdk still works                         |
| API 30–33 phone                                | typical older devices, pre-edge-to-edge    |
| API 35 phone, gesture nav **and** 3-button nav | edge-to-edge enforcement, nav-bar insets   |
| API 36 phone                                   | predictive back                            |
| API 37 phone (Pixel emulator)                  | target-level behavior, CT/ECH              |
| **16 KB page-size** system image (API 35+)     | native library alignment, no compat dialog |
| Tablet / foldable (sw ≥ 600dp), API 36/37      | orientation and resizability ignored       |
| Real arm64 phone                               | WebView video and ad blocking in practice  |

**Checklist**

- [ ] Fresh install: splash → home loads trending/popular, pager auto-scrolls, indicators move, placeholders shimmer while loading.
- [ ] List and search: paging loads more pages, pull to refresh works, tabs and pager stay in sync.
- [ ] Details: cast and crew, ratings, wishlist toggle. The wishlist survives an app restart.
- [ ] Offline: cached lists and details show from Room.
- [ ] Ripple, theme and dark mode look like the Phase 0 screenshots.
- [ ] Insets: nothing is hidden under the status bar, navigation bar or cutout on any screen.
- [ ] Play: the WebView opens, filter lists download on first run (check WorkManager in App Inspection), ads and popups are blocked, page style injection works, the document-start script suppresses the welcome popup.
- [ ] Video: full screen works. Phone locks to landscape on watch pages and back to portrait after. Tablet rotates freely without reloading. Back exits full screen, then history, then the activity. Predictive back animations look right.
- [ ] Rotate during playback: no reload (`configChanges`).
- [ ] **Upgrade in place**: install the saved 2.1.x release APK, add wishlist items, install the new signed APK over it. Data stays and filters stay installed.
- [ ] `./gradlew lint` (compare to baseline), `:app:testDebugUnitTest`, `:ad-filter:connectedDebugAndroidTest` (RuleIteratorTest), `:adblock-client:connectedDebugAndroidTest` (AdBlockClientTest).
- [ ] `dependencyGuard` diff reviewed. Check for unexpected transitive upgrades and new manifest permissions from merged AARs (open the merged manifest in Studio).
- [ ] Release APK passes the zipalign 16 KB check and the apksigner certificate check.

---

## 12. Suggested commit sequence

Each step builds and runs on its own, so a regression can be bisected:

1. Baseline: Dependency Guard, lint baseline.
2. Remove unused deps and dead code, fix the `WebViewActivity()` bug.
3. Version catalog (and optionally the KTS conversion).
4. AGP 8.13 + Gradle 8.14 + Kotlin 2.x + Compose compiler plugin.
5. Unique namespaces for the library modules.
6. kapt → KSP for Room and Hilt; Hilt 2.60.1.
7. `:ad-filter`: remove Anko, replace Mezzanine and HttpRequest; drop JitPack, the Aliyun mirror and jcenter.
8. `:adblock-client`: NDK r28, CMake 3.22.1, 16 KB verification.
9. AGP 9.4 + Gradle 9.7 + Kotlin 2.4 (built-in Kotlin, new DSL).
10. compileSdk 37; targetSdk 34 → 35 (edge-to-edge, remove SystemUiController).
11. targetSdk 36 (predictive back, large-screen layouts).
12. targetSdk 37 (CT/ECH checks, network security config).
13. Compose BOM + Material 2 → Material 3 (ripple, pull-to-refresh, tabs).
14. Accompanist pager and placeholder → foundation / custom.
15. Navigation, Hilt viewmodel-compose, lifecycle, paging, Room, DataStore.
16. Retrofit 3 + OkHttp 5 + kotlinx libraries.
17. Coil 3, Lottie, webkit, splashscreen; remove the scraping fallback (optional).
18. CI workflow updates; `versionName` 3.0.0; update CLAUDE.md (build requirements, Aliyun/JitPack notes, NDK/CMake versions).

## 13. Risks and decisions

| Risk                                                               | Impact                            | Mitigation                                                                                                                               |
| ------------------------------------------------------------------ | --------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Old brave/ad-block C++ fails to compile with NDK r28 clang         | Blocks 16 KB support              | Fix warnings in place (vendored code). Last resort: add `-Wno-…` flags in CMake                                                          |
| Mezzanine replacement changes the JS text (encoding, missing file) | Cosmetic filtering silently stops | Compare the loaded string with the old generated constant once, and test element hiding on a known ad page                               |
| Edge-to-edge insets missed on a screen                             | Content under the bars            | Test both nav modes on API 35+                                                                                                           |
| Tablets rotate the portrait-only UI                                | Broken or stretched layouts       | Test at sw ≥ 600dp, and plan adaptive layouts (Phase 7)                                                                                  |
| CT/ECH at API 37 breaks a player CDN                               | Video won't load                  | Test on API 37 before release. If needed, keep targetSdk 36 for one release (Play doesn't constrain this app, since it ships via GitHub) |
| Room generated code changes break upgrade-in-place                 | Crash on launch after update      | Upgrade-in-place test, schema export from now on                                                                                         |
| Signing key changes in CI                                          | Users can't update                | Keep the apksigner certificate check                                                                                                     |
| Coil 3 network artifact missing                                    | All posters blank                 | Add `coil-network-okhttp` in the same commit                                                                                             |

## Sources

- Android Developers, [Version upgrade strategies](https://developer.android.com/build/version-upgrade-strategies)
- [AGP release notes (9.4)](https://developer.android.com/build/releases/gradle-plugin) and [AGP 9.0 release notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes) (defaults, removed APIs, built-in Kotlin)
- [Migrate to built-in Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin) (kapt → KSP / legacy-kapt, `kotlinOptions` → `compilerOptions`)
- [Java versions in Android builds](https://developer.android.com/build/jdks)
- [Behavior changes: apps targeting Android 17](https://developer.android.com/about/versions/17/behavior-changes-17), [all apps on Android 17](https://developer.android.com/about/versions/17/behavior-changes-all), [Android 17 is here (blog)](https://android-developers.googleblog.com/2026/06/Android-17.html)
- [Behavior changes: Android 16](https://developer.android.com/about/versions/16/behavior-changes-16), [Android 15](https://developer.android.com/about/versions/15/behavior-changes-15)
- [Support 16 KB page sizes](https://developer.android.com/guide/practices/page-sizes)
- [Compose BOM mapping](https://developer.android.com/develop/ui/compose/bom/bom-mapping), [Migrate to Indication and Ripple APIs](https://developer.android.com/develop/ui/compose/touch-input/user-interactions/migrate-indication-ripple)
- [AndroidX versions](https://developer.android.com/jetpack/androidx/versions), [Hilt AndroidX release notes](https://developer.android.com/jetpack/androidx/releases/hilt)
- [Accompanist](https://google.github.io/accompanist/) (deprecations), [Coil 3 upgrade guide](https://coil-kt.github.io/coil/upgrading_to_coil3/)
- [Gradle 9 upgrade guide](https://docs.gradle.org/current/userguide/upgrading_major_version_9.html) (`jcenter()` removed, JVM 17+)
- [CMake 4.0 release notes](https://discourse.cmake.org/t/cmake-4-0-0-rc2-is-ready-for-testing/13635) (minimum < 3.5 removed)
- [Kotlin 2.4.20 release](https://blog.jetbrains.com/kotlin/2026/09/kotlin-2-4-20-released/), [Retrofit releases](https://github.com/square/retrofit/releases), [JakeWharton converter (moved into Retrofit)](https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter)
- [Edsuns/AdblockAndroid](https://github.com/Edsuns/AdblockAndroid). brave/ad-block's deprecation notice (README mirror) points to [adblock-rust](https://github.com/brave/adblock-rust)
