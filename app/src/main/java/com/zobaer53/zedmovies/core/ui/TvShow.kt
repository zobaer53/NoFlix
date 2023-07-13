
package com.zobaer53.zedmovies.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zobaer53.zedmovies.core.model.TvShow
import com.zobaer53.zedmovies.core.model.TvShowDetails
import com.zobaer53.zedmovies.core.ui.mapper.asNames

@Suppress("ReusedModifierInstance")
@Composable
fun HorizontalTvShowItem(
    tvShow: TvShow,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    with(tvShow) {
        HorizontalFeedItem(
            title = name,
            posterPath = posterPath,
            voteAverage = voteAverage,
            genres = genres.asNames(),
            onClick = { onClick(id) },
            modifier = modifier
        )
    }
}

@Composable
fun HorizontalTvShowItemPlaceholder(
    modifier: Modifier = Modifier
) {
    HorizontalFeedItemPlaceholder(modifier = modifier)
}

@Suppress("ReusedModifierInstance")
@Composable
fun VerticalTvShowItem(
    tvShow: TvShow,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    with(tvShow) {
        VerticalFeedItem(
            title = name,
            overview = overview,
            posterPath = posterPath,
            voteAverage = voteAverage,
            releaseDate = firstAirDate,
            genres = genres.asNames(),
            onClick = { onClick(id) },
            modifier = modifier
        )
    }
}

@Suppress("ReusedModifierInstance")
@Composable
fun VerticalTvShowItem(
    tvShow: TvShowDetails,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    with(tvShow) {
        VerticalFeedItem(
            title = name,
            overview = overview,
            posterPath = posterPath,
            voteAverage = voteAverage,
            releaseDate = firstAirDate,
            genres = genres.asNames(),
            onClick = { onClick(id) },
            modifier = modifier
        )
    }
}

@Composable
fun VerticalTvShowItemPlaceholder(
    modifier: Modifier = Modifier
) {
    VerticalFeedItemPlaceholder(modifier = modifier)
}
