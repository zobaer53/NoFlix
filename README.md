<p align="center">
  <img src="showcase/noflix-ad-free-movies-app-icon.png" alt="NoFlix – ad-free movies and TV series app for Android" width="128"/>
</p>

<h1 align="center">NoFlix – Watch Movies &amp; TV Series Ad-Free on Android</h1>

<p align="center">
  <b>A free, open-source Android app to stream movies and TV shows with no ads, no pop-ups and no trackers.</b><br/>
  Built-in ad blocker · Full-screen player · No sign-up · Made with Kotlin &amp; Jetpack Compose
</p>

<p align="center">
  <a href="https://github.com/zobaer53/NoFlix/releases/latest/download/NoFlix.apk"><img alt="Download NoFlix APK" src="https://img.shields.io/badge/Download-APK-12CDD9?style=for-the-badge&logo=android&logoColor=white"/></a>
  <a href="https://zobaer53.github.io/NoFlix/"><img alt="NoFlix website" src="https://img.shields.io/badge/Website-NoFlix-6647F0?style=for-the-badge&logo=googlechrome&logoColor=white"/></a>
</p>

<p align="center">
  <a href="https://github.com/zobaer53/NoFlix/releases/latest"><img alt="Latest release" src="https://img.shields.io/github/v/release/zobaer53/NoFlix"/></a>
  <a href="https://github.com/zobaer53/NoFlix/releases"><img alt="Downloads" src="https://img.shields.io/github/downloads/zobaer53/NoFlix/total"/></a>
  <a href="https://android-arsenal.com/api?level=26"><img alt="API 26+" src="https://img.shields.io/badge/API-26%2B-brightgreen.svg"/></a>
  <a href="LICENSE.txt"><img alt="License: Apache 2.0" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://github.com/zobaer53/NoFlix/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/zobaer53/NoFlix?style=social"/></a>
</p>

![NoFlix ad-free movie streaming app for Android](showcase/banner.png)

## What's new: should I update?

> **Latest: [v3.0.5](https://github.com/zobaer53/NoFlix/releases/latest) (4 Nov 2026). Recommended for everyone.**
> If the player crashes, it reloads instead of closing the app, and the download is less than half the size.
> [Download the APK](https://github.com/zobaer53/NoFlix/releases/latest/download/NoFlix.apk) and install it over your current version. Your watchlist and history are kept.

| Version | Released | What changed | Should you update? |
| --- | --- | --- | --- |
| **3.0.5** | 4 Nov 2026 | **Fix:** if the player crashes, it reloads the video instead of closing the whole app. The APK is less than half the size (about 7 MB instead of 16 MB). | ✅ **Yes**, everyone |
| 3.0.4 | 2 Nov 2026 | "Upload .srt / .vtt" in the player's subtitle menu now opens your phone's files ([#3](https://github.com/zobaer53/NoFlix/issues/3), [#4](https://github.com/zobaer53/NoFlix/issues/4)). Ads can no longer redirect the player to other websites. | Update to 3.0.5 |
| 3.0.3 | 22 Oct 2026 | Smoother playback: the video fills the screen, and the watch position is saved more reliably | Update to 3.0.5 |
| 3.0.2 | 18 Oct 2026 | TV shows resume at the last episode you watched | Update to 3.0.5 |
| 3.0.1 | 14 Oct 2026 | Search filters (genre, year, rating and more) and a refreshed light theme | Update to 3.0.5 |
| 3.0.0 | 14 Oct 2026 | Android 17 support, YouTube trailers, crash and freeze fixes; needs Android 8.0+ | Update to 3.0.5 |
| 2.0.2 or older | 2023 | Signed with an old key | ⚠️ **Uninstall it first**, then install 3.0.5 (Android can't update across keys) |

Proof of the subtitle fix, recorded on a Pixel 6a: [video](showcase/fixes/subtitle-upload-fix.mp4). Every release is listed on the [Releases page](https://github.com/zobaer53/NoFlix/releases).

## What is NoFlix?

**NoFlix** is an **ad-free movie and TV series streaming app for Android**. Browse trending, popular, top-rated and upcoming movies and TV shows, read the details, save favourites to your watchlist, and press **Play** to watch in a clean, full-screen player. Ads, pop-ups, redirects and trackers are blocked with a built-in ad blocker.

If you've been looking for a way to **watch free movies without ads**, **stream TV series without pop-ups**, or find a **Netflix-style app without ads** that is free and open source, NoFlix is for you.

## Features

- 🚫 **Ad-free streaming.** A built-in ad blocker (AdGuard / EasyPrivacy filter lists and a native ad-block engine) removes ads, pop-ups, redirects and crypto-miners.
- 🎬 **Movies and TV series.** Trending, popular, top-rated, now playing, upcoming and discover lists, powered by [The Movie Database (TMDB)](https://www.themoviedb.org/).
- ▶️ **One-tap full-screen player.** Videos open in landscape full screen; the rest of the app stays portrait.
- 💬 **Subtitles.** Pick a built-in track or upload your own `.srt` / `.vtt` file.
- ⏯️ **Resume where you left off.** TV shows reopen at the last episode you watched.
- 🎞️ **Trailers.** Watch the YouTube trailer from the details screen.
- 🔍 **Search and filters.** Find any movie or TV show by title, or browse by genre, year and rating.
- ❤️ **Watchlist.** Save movies and series to watch later.
- 📶 **Offline browsing.** Lists and details are cached, so you can browse without a connection.
- 🔒 **Private.** No account, no sign-up, no analytics.
- 🎨 **Modern design.** Material 3, dark theme and smooth Jetpack Compose UI.

## Download

Get the latest signed APK from **[GitHub Releases](https://github.com/zobaer53/NoFlix/releases/latest)**. Every update on `master` is published there automatically.

1. Download `NoFlix.apk` on your Android phone (Android 8.0 or newer).
2. Open it and allow *Install unknown apps* if prompted.
3. Open NoFlix, pick a movie or series and tap **Play**.

## Demo

<p align="center">
  <a href="showcase/demo.mp4"><img src="showcase/demo.gif" alt="NoFlix demo: browse movies, open details, play ad-free, search with filters and the watchlist" width="300"/></a>
  <br/><sub>Tap the animation for the full-quality video.</sub>
</p>

## Screenshots

<table width="100%">
  <tbody>
    <tr>
      <td width="20%"><img src="showcase/screenshot-1-home.png" alt="NoFlix home screen with upcoming and top-rated movies"/></td>
      <td width="20%"><img src="showcase/screenshot-2-lists.png" alt="NoFlix popular movies and TV series lists"/></td>
      <td width="20%"><img src="showcase/screenshot-3-details.png" alt="NoFlix movie details screen with Play and Trailer buttons"/></td>
      <td width="20%"><img src="showcase/screenshot-4-search-filters.png" alt="NoFlix search with genre, year and rating filters"/></td>
      <td width="20%"><img src="showcase/screenshot-5-wishlist.png" alt="NoFlix watchlist of saved movies and TV shows"/></td>
    </tr>
  </tbody>
</table>

![NoFlix full-screen ad-free player with subtitles](showcase/screenshot-6-player.png)

## FAQ

**Is NoFlix free?**
Yes. NoFlix is completely free and open source under the Apache 2.0 license, and it has no ads or in-app purchases.

**How do I watch movies without ads on Android?**
Install NoFlix, search for a movie or TV show and tap Play. The built-in ad blocker filters every request the player makes, so ads and pop-ups never load.

**Do I need an account?**
No. NoFlix works without sign-up or login.

**Is NoFlix on the Google Play Store?**
No. Download the APK from [GitHub Releases](https://github.com/zobaer53/NoFlix/releases/latest).

**Can I use my own subtitles?**
Yes. In the player, open the subtitles menu, tap **Upload .srt / .vtt** and pick the file from your phone. Built-in subtitle tracks are listed in the same menu.

**Does NoFlix host any videos?**
No. See the [disclaimer](#disclaimer) below.

## Tech stack & architecture

NoFlix follows the [official Android architecture guidance](https://developer.android.com/topic/architecture) (data, domain and UI layers):

- **Kotlin**, **Coroutines & Flow**
- **Jetpack Compose**, Material 3, Navigation Compose
- **Hilt** for dependency injection
- **Retrofit** + **kotlinx.serialization** for the TMDB API
- **Room** + **Paging 3** (RemoteMediator) for offline caching
- **DataStore**, **Coil**, **Lottie**
- **WebView** with an AdGuard-compatible ad-blocking engine (C++ via JNI)

![NoFlix architecture diagram](showcase/architecture.png)

## Build from source

1. Clone the repo and open it in Android Studio.
2. Get a free API key from [The Movie DB](https://www.themoviedb.org/settings/api) and add it to `local.properties`:
   ```properties
   API_KEY="yourApiKey"
   ```
3. Install **CMake 3.22.1** and **NDK r28c** (`28.2.13676358`) from *SDK Manager → SDK Tools*. The native ad-block library needs them.
4. Run `./gradlew :app:installDebug` or press Run in Android Studio.

## Contributing

Issues and pull requests are welcome. If NoFlix helps you watch movies and TV series without ads, please ⭐ **star the repo**. It helps other people find it.

## Disclaimer

NoFlix does not host, upload or store any video files. Movie and TV metadata comes from the TMDB API, and playback loads publicly available third-party web pages. NoFlix is not endorsed or certified by TMDB. You are responsible for complying with the laws in your country.

## Credits

- UI design from [Figma Community](https://www.figma.com/community/file/1088719884686291024)
- Ad blocking based on [AdblockAndroid](https://github.com/Edsuns/AdblockAndroid), [AdGuard filters](https://github.com/AdguardTeam/AdguardFilters) and Brave's ad-block engine
- Movie and TV data from [TMDB](https://www.themoviedb.org/)

## Contact

📫 **hossain.zobaer11858@gmail.com**

<p align="left">
<a href="https://www.linkedin.com/in/zobaer53/" target="blank"><img align="center" src="https://raw.githubusercontent.com/rahuldkjain/github-profile-readme-generator/master/src/images/icons/Social/linked-in-alt.svg" alt="Zobaer Hossain on LinkedIn" height="30" width="40" /></a>
<a href="https://medium.com/@zobaer53" target="blank"><img align="center" src="https://upload.wikimedia.org/wikipedia/commons/thumb/e/ec/Medium_logo_Monogram.svg/1200px-Medium_logo_Monogram.svg.png" alt="Zobaer Hossain on Medium" height="30" width="40" /></a>
<a href="https://www.hackerrank.com/zobaer53" target="blank"><img align="center" src="https://upload.wikimedia.org/wikipedia/commons/4/40/HackerRank_Icon-1000px.png" alt="Zobaer Hossain on HackerRank" height="30" width="40" /></a>
<a href="https://www.sololearn.com/profile/15909016" target="blank"><img align="center" src="https://blob.sololearn.com/avatars/sololearn.png" alt="Zobaer Hossain on Sololearn" height="30" width="40" /></a>
<a href="https://stackoverflow.com/users/zobaer-hossain" target="blank"><img align="center" src="https://raw.githubusercontent.com/rahuldkjain/github-profile-readme-generator/master/src/images/icons/Social/stack-overflow.svg" alt="Zobaer Hossain on Stack Overflow" height="30" width="40" /></a>
</p>

## License

```
Copyright 2023 zobaer53 (Zobaer Hossain)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

<!-- Keywords: ad free movies app, ad-free TV series app, watch movies without ads, free movie streaming app Android, no ads movie app APK, adblock movie player, open source Netflix alternative, Kotlin Jetpack Compose movie app, TMDB movie app -->
