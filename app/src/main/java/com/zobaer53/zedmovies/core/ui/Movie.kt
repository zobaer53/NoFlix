
package com.zobaer53.zedmovies.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zobaer53.zedmovies.core.model.Movie
import com.zobaer53.zedmovies.core.model.MovieDetails
import com.zobaer53.zedmovies.core.ui.mapper.asNames

@Suppress("ReusedModifierInstance")
@Composable
fun HorizontalMovieItem(
    movie: Movie,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    with(movie) {
        HorizontalFeedItem(
            title = title,
            posterPath = posterPath,
            voteAverage = voteAverage,
            genres = genres.asNames(),
            onClick = { onClick(id) },
            modifier = modifier
        )
    }
}

@Composable
fun HorizontalMovieItemPlaceholder(
    modifier: Modifier = Modifier
) {
    HorizontalFeedItemPlaceholder(modifier = modifier)
}

@Suppress("ReusedModifierInstance")
@Composable
fun VerticalMovieItem(
    movie: Movie,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    with(movie) {
        VerticalFeedItem(
            title = title,
            overview = overview,
            posterPath = posterPath,
            voteAverage = voteAverage,
            releaseDate = releaseDate,
            genres = genres.asNames(),
            onClick = { onClick(id) },
            modifier = modifier
        )
    }
}

@Suppress("ReusedModifierInstance")
@Composable
fun VerticalMovieItem(
    movie: MovieDetails,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    with(movie) {
        VerticalFeedItem(
            title = title,
            overview = overview,
            posterPath = posterPath,
            voteAverage = voteAverage,
            releaseDate = releaseDate,
            genres = genres.asNames(),
            onClick = { onClick(id) },
            modifier = modifier
        )
    }
}

@Composable
fun VerticalMovieItemPlaceholder(
    modifier: Modifier = Modifier
) {
    VerticalFeedItemPlaceholder(modifier = modifier)
}
