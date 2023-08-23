# zeDMoviesApp

![zeDMoviesApp](https://github.com/zobaer53/zeDMoviesApp/blob/master/app/src/main/res/drawable/zed_movies.png)


zeDMoviesApp is an application for Android for watching Movies & TV Shows using webview.

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
