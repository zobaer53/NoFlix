# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

NoFlix (Gradle root project `zeDMovies`, package `com.zobaer53.zedmovies`) is an Android app that browses movies/TV shows from The Movie DB and plays them ad-free in a WebView pointed at a third-party streaming site. Single-module app (`:app`) plus two vendored ad-blocking library modules (`:ad-filter`, `:adblock-client`, originally Edsuns/AdblockAndroid).

## Build & run

```bash
./gradlew :app:assembleDebug        # debug APK
./gradlew :app:installDebug         # install on connected device/emulator
./gradlew :app:assembleRelease      # signed release APK (needs signing keys in local.properties)
./gradlew :app:testDebugUnitTest    # JVM unit tests
./gradlew :app:connectedDebugAndroidTest   # instrumented tests (device required)
./gradlew :ad-filter:connectedDebugAndroidTest --tests 'io.github.edsuns.adfilter.RuleIteratorTest'  # single test class
.maestro/run.sh [--no-build] [flow.yaml ...]   # Maestro UI suite on the connected device (see below)
.maestro/upgrade.sh old.apk [new.apk]          # install-over test: data must survive an update
```

There is no lint/format config beyond Android defaults. JVM unit tests cover the search filters (`SearchFiltersMapperTest`, `TmdbPagingSourceTest`); the rest is placeholder (`ExampleUnitTest`, `RuleIteratorTest`, `AdBlockClientTest`). The real regression check is the **Maestro suite** in `.maestro/flows/` (home, see-all paging + pull-to-refresh, search + tabs, details + wish list, playback, trailer, search filters). `run.sh` clean-installs the debug APK, runs the flows, and fails on logcat crashes, a non-landscape player, any 64-bit `.so` that isn't 16 KB aligned, fewer than 6 installed filter lists, or the injected element-hiding script never finishing (the last checks rely on debug-only logs: `NoFlixAdBlock: blocked …` and the ad-filter JS `console.log`s). Maestro clears logcat at the start of every flow, so `run.sh` streams logcat to a file for the whole run instead of dumping it at the end; with several devices attached, set `ANDROID_SERIAL` (both scripts pass it to Maestro as `--device`). Start emulators with `-gpu host`: under memory pressure the emulator silently falls back to software rendering and the app then ANRs. Start the API 37 (Android 17) emulator with `-gpu host -feature GuestAngle`: plain `-gpu host` on a Mac only offers OpenGL ES 3.0, Chromium 145 asks for an ES 3.1 context (`EGL_BAD_CONFIG: no ES 3.1 support`) and its GPU process aborts (`FATAL: gl_version_info.cc … VERSION =`) on YouTube and the stream player.

Requirements:
- JDK 17+, Gradle 9.7.1, AGP 9.4.1 with **built-in Kotlin** (no `kotlin-android` plugin; never re-add it or `kotlin-kapt`), Kotlin 2.4.21 pinned through the root `buildscript` classpath, Compose compiler = the `org.jetbrains.kotlin.plugin.compose` plugin at the Kotlin version, KSP for Room and Hilt. All versions live in `gradle/libs.versions.toml`, including `compileSdk` (37), `ndk` and `cmake`.
- compileSdk 37, targetSdk 37, minSdk 26 (Android 8.0). Android 7.x can't download the filter lists or play (missing TLS roots), and with 26 `java.time` (kotlinx-datetime) needs no core library desugaring.
- NDK r28c (`28.2.13676358`) + CMake 3.22.1 for `:adblock-client`'s native library. NDK r28+ is what makes the 64-bit `.so` 16 KB-page aligned. Every native library in the APK must stay 16 KB aligned (AndroidX ones too), or Android 15+ 16 KB devices run the app in compat mode with a warning.
- AGP 9 no longer aligns compile and runtime classpath versions (`android.dependency.useConstraints=false`), so declare the versions you actually ship (for example the Compose BOM) rather than relying on a transitive bump.
- `local.properties` must contain `API_KEY=<TMDB key>`; `app/build.gradle` reads it into `BuildConfig.API_KEY` and fails to configure without the file. Optional `RELEASE_STORE_FILE`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD` enable release signing; without them the release build is unsigned.
- Repositories are only `google()`, `mavenCentral()` and `gradlePluginPortal()`. JCenter, the Aliyun mirror and JitPack are gone (Anko, Mezzanine and Edsuns HttpRequest were removed).

## Releases / CI

`.github/workflows/release.yml` runs on every push to `master` (except pushes that only touch `docs/`, `tools/` or Markdown): it computes the next version (latest `X.Y.Z` git tag with patch +1, or `versionName` from `app/build.gradle` if that is higher), passes `-PappVersionCode`/`-PappVersionName` to Gradle (versionCode = `X*10000 + Y*100 + Z`), verifies the APK is signed with the release cert (`CN=zeD, O=NoFlix`) and that it's 16 KB page-size aligned (`zipalign -P 16` + `llvm-objdump` on every 64-bit `.so`), and publishes a GitHub release with `NoFlix.apk`. To start a new minor/major line, bump `versionName` in `app/build.gradle`. Pushing to `master` therefore ships a public release.

The root `.gitignore` ignores `/app/` (existing files are tracked anyway), so new files under `app/` must be added with `git add -f`.

`docs/` is the GitHub Pages site (https://zobaer53.github.io/NoFlix/) served from `master` `/docs`: a single static `index.html` with SEO meta tags and JSON-LD, plus `sitemap.xml`/`robots.txt`. Keep its FAQ text in sync with the README's FAQ and the `FAQPage` JSON-LD in the same file.

Launcher icon, README logo, social preview and website images are generated by `tools/branding/generate.py` (cairosvg + Pillow; see its docstring). The icon shapes are duplicated in `tools/branding/render.py` and `app/src/main/res/drawable/zed_movies_round_*.xml`, so change both together.

Do not name the streaming website in commit messages, release notes, or identifiers/file names — use neutral names like `STREAM_BASE_URL` / `StreamPageStyle.kt`. The URL itself stays in code.

## Architecture

Follows Google's recommended layered architecture (data → domain → ui) with Hilt DI, all inside `:app`:

- **data/** — Retrofit (kotlinx-serialization) TMDB services in `data/network`, Room DB in `data/database`, Preferences DataStore in `data/datastore`. Repositories (`data/repository/*Impl`) combine them:
  - Lists use Paging 3 `RemoteMediator`s (`data/paging`) that cache TMDB pages into Room with remote-key tables; search and filtered discover (Search tab filters, `doc/search-filters-plan.md`) use `TmdbPagingSource` (network only).
  - Details use `networkBoundResource` (`data/network/common/NetworkBoundResource.kt`): emit cached Room data, fetch, save, re-emit from Room.
  - Retrofit calls return `ZedMoviesResult<T>` (success/failure/loading) via a custom `ResultAdapterFactory`/`ResultCall` instead of throwing.
  - Each layer has its own model types (`Network*` → `*Entity` → `data/model` → `domain/model/*Model`), with mappers in `data/mapper`, `data/database/mapper`, and `ui/smallcomponent/mapper`.
- **domain/** — repository interfaces and one-method use cases consumed by ViewModels.
- **di/** — Hilt modules binding repositories, DB, DataStore and network (API key comes from `BuildConfig.API_KEY` via `NetworkModule`).
- **navigation/** (top-level package, not under `ui/`) — `ZedMoviesNavHost`, `TopLevelDestination` (bottom-bar tabs, rendered by `ui/component/ZedMoviesBottomBar.kt`) and the destination base interface.
- **ui/** — Jetpack Compose. `zedMoviesActivity` → `zedMoviesApp` (state in `ZedMoviesAppState`) → `ZedMoviesNavHost`. Each feature (`home`, `list`, `search`, `details`, `wishlist`) has `Screen`, `ViewModel`, `UiState`, `Event`, and a `navigation/*Destination.kt`. Shared design system lives in `ui/designsystem` (theme accessed via `zedMoviesTheme.colors/spacing/typography`); reusable widgets in `ui/smallcomponent`.

Naming quirk: many classes/functions use a lowercase `zedMovies` prefix (`zedMoviesActivity`, `zedMoviesApplication`, `zedMoviesApi`, `zedMoviesTheme`) while their files are `ZedMovies*.kt`. The manifest references the lowercase names.

### Playback (WebView) path

Playback is separate from the Compose nav graph:

1. The "Play" button (`ui/smallcomponent/Rating.kt`) starts `ui/webview/WebViewActivity` with extras `movieName`, `movieYear`, `type`, `tmdbId`.
2. With a `tmdbId`, `buildStreamUrl()` (`ui/webview/scrapeMovieData.kt`) builds `STREAM_BASE_URL/watch/<id>` (movies) or `/tv/<id>` (TV). The site is a client-rendered SPA keyed by TMDB id, so it can't be scraped. The older Jsoup scraping path (`RunOn Background.kt`, `scrapeMovieData()`) is only a fallback when `tmdbId` is 0.
3. `VideoWebView` (`ui/webview/WebView.kt`) hosts an Android `WebView` in Compose: installs AdGuard filter subscriptions on first run, wires `AdFilter` into `MyWebClient` (`ContentBlockingWebViewClient.kt`) for request blocking and cosmetic scripts, handles HTML5 full-screen via `onShowCustomView`, and locks orientation: landscape on `/watch/` pages and full-screen video, portrait everywhere else.
4. `StreamPageStyle.kt` injects CSS/JS into the site: a document-start script (sets the site's welcome-popup flag before its scripts run) and an idempotent page script that hides site chrome and stretches the player iframe on `/watch/` pages. Because the site is an SPA, it hooks `history.pushState`/`replaceState` and uses a `MutationObserver`. Selectors depend on the live site's DOM and break when it changes.

`WebViewActivity` declares `configChanges` for orientation/screen size in the manifest so rotation doesn't recreate it and reload the page.

### Trailers

The details screen shows a "Trailer" chip next to Play when TMDB has a YouTube trailer. The details request appends `videos` with `include_video_language=<content language>,en,null` (without it TMDB returns no videos for non-English content languages). `data/mapper/VideoMapper.kt` picks the key (trailers before teasers, content language before English, official first), and it's cached in Room as `trailer_key` (DB v3, AutoMigration 2→3). `ui/trailer/TrailerActivity` plays it with `com.pierfrancescosoffritti.androidyoutubeplayer:core`, which wraps YouTube's IFrame embed in a WebView and sets the origin YouTube requires (a bare embed WebView gets "Error 153"). It's a separate activity with `configChanges`, so it rotates without reloading while the main activity stays portrait; YouTube's full-screen button switches to landscape and hides the bars. Videos that can't be embedded open in the YouTube app or browser instead. On the API 37 emulator, playback needs `-feature GuestAngle` (see Build & run).

### Ad-blocking modules

- `:ad-filter` (`io.github.edsuns.adfilter`) — Kotlin API (`AdFilter.create/get`, `FilterViewModel`); downloads and installs filter lists with WorkManager (`DownloadWorker`, `InstallationWorker`) and injects element-hiding/scriptlet JS that `script/JsAssets.kt` reads from `src/main/assets/adfilter/` at runtime. `scriptlets.min.js` is generated from `scriptlets_src/` with `gulp`; `extended-css.min.js` is vendored from AdGuard ExtendedCss. The workers open the filter store with `BinaryDataStore.of(context)`; never create `AdFilter` from a worker: its init blocks on a WorkManager query queued behind that worker, deadlocks while holding the singleton lock, and the next `WebViewActivity` launch ANRs. Lists are tens of MB (processed up to ~27 MB), so they never go through the Java heap: downloads stream to the file, `Checksum.of(InputStream)` streams, and `AdBlockClient`'s `File` overloads read and write in native code (small heaps, e.g. 48 MB on old devices, ran out of memory).
- `:adblock-client` (`io.github.edsuns.adblockclient`) — JNI wrapper (`src/main/cpp/adblockclient-lib.cpp`) around Brave's C++ ad-block engine in `src/main/cpp/third-party/`. Native bugs here crash the app with SIGSEGV (e.g. strings passed to `parse()` must be NUL-terminated).

Both library modules share the app's catalog versions (compileSdk 37, minSdk 26, Java 17) and have their own namespaces (AGP 9 requires unique package names). The vendored brave/ad-block C++ engine is deprecated upstream (successor: adblock-rust).

### System bars

`zedMoviesActivity` calls `enableEdgeToEdge()`, so bars are always transparent. `ui/SystemBarsAppearance.kt` only picks light or dark bar icons from a color's luminance (`appState.setSystemBarsColor(color)`, e.g. transparent with white icons on the details backdrop). Screens must apply window insets themselves.
