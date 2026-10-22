# Torrent Download Feature & Branch Code Review

**Branch:** `download`  
**Target Goal:** In-app download feature capable of downloading media from torrents  
**Date:** October 2026  
**Status:** Incomplete for Torrent Downloading (Delegates to external clients); HTTP/Stremio foundation established.

---

## 1. Executive Summary & Goal Gap Analysis

The objective for this branch is to implement a **download feature that can download from torrents**.

### Current Implementation State
The branch introduces an integration with the **Stremio Add-on ecosystem** (Cinemeta for catalog metadata and Torrentio for torrent stream scraping). It provides a full UI for searching titles, browsing seasons/episodes, inspecting magnet links, and managing downloads.

However, **there is currently no native torrent downloading engine in the application**:
* When a user taps a torrent stream (or inspects a magnet/info hash), `StremioViewModel` calls `StremioDownloadManager.openMagnetLink()`.
* `openMagnetLink()` constructs a `magnet:?xt=urn:btih:...` URI and fires an Android external intent (`Intent(Intent.ACTION_VIEW)`).
* If the user has a third-party torrent app installed (e.g. LibreTorrent, Flud, BitTorrent), the link is handed off to that external app.
* If no external client is installed, the app catches the `ActivityNotFoundException` and shows a Toast: *"No torrent app found. Install LibreTorrent or Flud to stream torrents."*
* The internal `StremioDownloadManager` engine is strictly an OkHttp-based **HTTP direct file downloader** (`startHttpDownload`), which only works for direct HTTP/HTTPS URLs.

---

## 2. Architecture Overview of the Current Branch

```mermaid
flowchart TD
    UI[StremioScreen / TopLevelDestination] --> VM[StremioViewModel]
    VM --> Repo[StremioRepositoryImpl]
    Repo --> Meta[CinemetaService]
    Repo --> Addon[StremioAddonService (Torrentio)]
    Repo --> Pref[StremioPreferences]
    Repo --> DL[StremioDownloadManager]
    
    subgraph Stream Action
        DL -->|HTTP Link| HttpEngine[runHttpDownloadLoop (OkHttp)]
        DL -->|Torrent Hash| ExtIntent[Intent.ACTION_VIEW (External Torrent Client)]
    end
```

### Component Breakdown
1. **Catalog & Stream Resolution**:
   - `CinemetaService`: Queries Cinemeta catalog API for movie/series metadata, poster URLs, and season/episode lists.
   - `StremioAddonService`: Fetches manifests and scrapes streams from Stremio add-ons (defaults to Torrentio). Supports concurrent scraping across multiple add-ons via coroutines (`async`/`awaitAll`).
   - Parsers: `QualityParser` (extracts 4K, 1080p, 720p, etc.), `SizeParser` (parses file sizes from stream strings), `MagnetParser` (extracts BTIH and trackers), and `HlsParser` (extracts variants from `.m3u8` playlists).
2. **Download Handling**:
   - `StremioDownloadManager`: Coroutine-based HTTP Range-request downloader with speed calculation, byte progress, and retry backoff.
   - `DownloadJob`: Data class holding job progress, speed, status, and local file path.
3. **UI / Presentation**:
   - `StremioScreen`: 4-tab interface (`Search`, `Paste link`, `Downloads`, `Settings`).
   - `TopLevelDestination`: Integrated as the 4th bottom navigation tab with `R.drawable.ic_download`.

---

## 3. Critical Issues & Runtime Failure Risks

### Issue 1: Missing Torrent Engine (Core Goal Gap)
* **Root Cause**: OkHttp cannot connect to BitTorrent peers, communicate with DHT nodes, or exchange piece bitfields.
* **Impact**: The app cannot download torrents itself. Users expecting an integrated experience are kicked out to external apps.

### Issue 2: Scoped Storage Crash on Android 10+ (`targetSdk 37`)
* **Location**: `StremioPreferences.kt:27-33`
  ```kotlin
  val externalPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
  val appDir = File(externalPublicDir, "NoFlix")
  if (!appDir.exists()) appDir.mkdirs()
  ```
* **Root Cause**: On Android 10+ (API 29+) with `targetSdk 37`, raw file system access to public shared storage (`Environment.DIRECTORY_DOWNLOADS`) throws `IOException: Permission denied` (EACCES) unless utilizing the `MediaStore.Downloads` API.
* **Fix**: Use app-specific external storage which does not require legacy storage permissions:
  ```kotlin
  context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath ?: context.filesDir.absolutePath
  ```
  Or write to `MediaStore.Downloads` using `ContentResolver`.

### Issue 3: Missing `FileProvider` Declaration in `AndroidManifest.xml`
* **Location**: `StremioDownloadManager.kt:270-275`
* **Root Cause**: `openDownloadedFile` requests a URI from `FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)`. However, no `<provider>` is declared in `AndroidManifest.xml` and no `xml/file_paths.xml` exists.
* **Impact**:
  - `getUriForFile` throws an `IllegalArgumentException`.
  - The fallback `Uri.fromFile(file)` throws `FileUriExposedException` on API 24+, crashing the app or preventing downloaded files from being opened.
* **Fix**: Add the `FileProvider` component to `AndroidManifest.xml` and define the file paths resource XML.

### Issue 4: Background Execution Termination (No Foreground Service)
* **Location**: `StremioDownloadManager.kt:40`
* **Root Cause**: Downloads are executed in `CoroutineScope(Dispatchers.IO + SupervisorJob())` held inside a singleton class with no Android Service lifecycle tie-in.
* **Impact**: When the user switches apps or locks their screen, the Android OS puts the app in a cached state and terminates network execution. Long movie downloads will be killed prematurely.
* **Fix**: Run downloads under an Android **Foreground Service** with `POST_NOTIFICATIONS` permission and a progress notification, or delegate tasks to `WorkManager`.

### Issue 5: HLS Playlist Misinterpretation
* **Location**: `StremioViewModel.kt:274-279`
* **Root Cause**: When a user selects an HLS quality variant from a `.m3u8` playlist, `startHttpDownload` is called with the playlist URL. The HTTP downloader fetches the plain-text `.m3u8` file and saves it as `video.mp4` instead of downloading and muxing the `.ts` / `.m4s` chunks.
* **Fix**: Implement an HLS segment fetcher or prevent HLS streams from being treated as direct HTTP binary downloads.

### Issue 6: In-Memory State Loss on App Kill
* **Location**: `StremioDownloadManager._jobsFlow`
* **Root Cause**: Download jobs are stored in an in-memory `MutableStateFlow`. If the OS kills the process or the app restarts, download history, partial progress, and file tracking are lost.
* **Fix**: Persist `DownloadJob` entities into the existing Room database.

---

## 4. Roadmap to Complete the Torrent Download Feature

To achieve genuine in-app torrent downloading, choose one of the following architectural paths:

### Path A: Native BitTorrent Engine (Recommended for Offline P2P)
Integrate an embedded BitTorrent client library:
1. **Library**: Add `org.libtorrent4j:libtorrent4j-android` (modern wrapper for C++ libtorrent rasterbar).
2. **Session Manager**: Create a `TorrentDownloadManager` that initializes a `SessionManager`, configures DHT, listen ports, and download rate limits.
3. **Torrent Task Flow**:
   - Ingest magnet link or `.torrent` file.
   - Fetch metadata (`add_torrent_params`).
   - Allow user to pick the video file inside multi-file torrents (using `TorrentFileInfo`).
   - Stream pieces sequentially if streaming preview is desired, or prioritize all pieces to disk.
   - Observe `TorrentHandle` alerts (download rate, progress percentage, seeders count).
4. **Foreground Service**: Bind the active `SessionManager` to a sticky Foreground Service to keep sockets alive while downloading in the background.

### Path B: Debrid Cloud Resolving (High Speed, Battery Efficient)
If direct P2P connections are undesirable on mobile (due to battery, carrier carrier-grade NAT, or ISP throttling):
1. Integrate a Debrid provider (Real-Debrid, AllDebrid, or Torbox).
2. The user supplies an API key in Settings.
3. The app submits the magnet link to the debrid service, which caches/downloads it on cloud servers and returns a high-speed direct HTTP/HTTPS download link.
4. The existing `startHttpDownload()` engine handles the direct HTTP stream at full line speed.

---

## 5. Review Checklist for Next Commit

- [ ] Choose and implement the BitTorrent client engine (`libtorrent4j` or Debrid bridge).
- [ ] Fix scoped storage target directory in `StremioPreferences`.
- [ ] Declare `androidx.core.content.FileProvider` in `AndroidManifest.xml` with `res/xml/file_paths.xml`.
- [ ] Implement a Foreground Service (`DownloadService`) with Android notifications.
- [ ] Connect download jobs to Room database for persistent state.
