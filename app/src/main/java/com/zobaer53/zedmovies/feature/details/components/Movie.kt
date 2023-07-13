

package com.zobaer53.zedmovies.feature.details.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.core.ui.mapper.asNames
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.core.model.MovieDetails
import com.zobaer53.zedmovies.core.ui.mapper.NoMovieRuntimeValue

@Suppress("ReusedModifierInstance")
@Composable
internal fun MovieDetailsItem(
    movieDetails: MovieDetails,
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    with(movieDetails) {
        DetailsItem(
            modifier = modifier,
            title = title,
            overview = overview,
            posterPath = posterPath,
            releaseDate = releaseDate,
            runtime = if (runtime == NoMovieRuntimeValue) {
                stringResource(id = R.string.no_runtime)
            } else {
                stringResource(id = R.string.details_runtime_text, runtime.toString())
            },
            genres = genres.asNames(),
            voteAverage = 0.0,
            credits = credits,
            isWishlisted = isWishlisted,
            onBackButtonClick = onBackButtonClick,
            onWishlistButtonClick = onWishlistButtonClick
        )
    }
}

@Composable
internal fun MovieDetailsItemPlaceholder(
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DetailsItemPlaceholder(
        modifier = modifier,
        onBackButtonClick = onBackButtonClick,
        onWishlistButtonClick = onWishlistButtonClick
    )
}
