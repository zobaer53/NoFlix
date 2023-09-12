![zeDMoviesApp](https://github.com/zobaer53/zeDMoviesApp/blob/master/Product%20Card%20(1).png)
<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=24"><img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat"/></a>
</p>

![GitHub stars](https://img.shields.io/github/stars/zobaer53/zeDMoviesApp.svg?style=social)
![GitHub forks](https://img.shields.io/github/forks/zobaer53/zeDMoviesApp.svg?style=social)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/zobaer53/zeDMoviesApp)
![GitHub issues](https://img.shields.io/github/issues-raw/zobaer53/zeDMoviesApp)
![GitHub closed issues](https://img.shields.io/github/issues-closed-raw/zobaer53/zeDMoviesApp)
![GitHub repo size](https://img.shields.io/github/repo-size/zobaer53/zeDMoviesApp)





# zeDMoviesApp

zeDMoviesApp is an application for Android to watch ad-free Movies & TV Shows using Webview.

# Preview

https://github.com/zobaer53/zeDMoviesApp/assets/61627836/3743cf43-8afc-4984-b392-b425784ef823



<img src="https://github.com/zobaer53/zeDMoviesApp/blob/master/screenshot-1-home.png" width="50%"><img src="https://github.com/zobaer53/zeDMoviesApp/blob/master/screenshot-2-home.png" width="50%">
<img src="https://github.com/zobaer53/zeDMoviesApp/blob/master/screenshot-4-details.png" width="50%"><img src="https://github.com/zobaer53/zeDMoviesApp/blob/master/screenshot-7-wishlist.png" width="50%">

# Architecture

The **zeDMoviesApp** app follows the
[official architecture guidance](https://developer.android.com/topic/architecture)

![Architecture diagram](https://github.com/zobaer53/zeDMoviesApp/blob/master/architecture-1-overall.png)

# API Keys
zeDMoviesApp uses [The Movie DB](https://www.themoviedb.org/) API in order to fetch all the Movies and TV Shows Data.
<br>
To run this application on your machine you have to issue an API KEY from The Movie DB and place it in package com.zobaer53.zedmovies.di
<br>
```
const val zedMovies_API_KEY = "yourApiKey"
```

# Credits

- Design on [Figma](https://www.figma.com/community/file/1088719884686291024).

# License
```xml
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
