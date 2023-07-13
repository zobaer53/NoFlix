
package com.zobaer53.zedmovies.feature.details.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.core.model.TvShowDetails
import com.zobaer53.zedmovies.core.ui.mapper.NoTvShowRuntimeValue
import com.zobaer53.zedmovies.core.ui.mapper.asNames
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.feature.details.components.DetailsItem
import com.zobaer53.zedmovies.feature.details.components.DetailsItemPlaceholder

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
