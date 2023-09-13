
package com.zobaer53.zedmovies.ui.details.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.data.model.TvShowDetails
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.NoTvShowRuntimeValue
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asNames
import com.zobaer53.zedmovies.R

@Suppress("ReusedModifierInstance")
@Composable
internal fun TvShowDetailsItem(
    tvShowDetails: TvShowDetails,
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    with(tvShowDetails) {
        DetailsItem(
            modifier = modifier,
            type = "tv",
            title = name,
            overview = overview,
            posterPath = posterPath,
            releaseDate = firstAirDate,
            runtime = if (episodeRunTime == NoTvShowRuntimeValue) {
                stringResource(id = R.string.no_runtime)
            } else {
                stringResource(id = R.string.details_runtime_text, episodeRunTime.toString())
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
internal fun TvShowDetailsItemPlaceholder(
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
