

package com.zobaer53.zedmovies.feature.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zobaer53.zedmovies.core.designsystem.component.ZedMoviesCardNetworkImage
import com.zobaer53.zedmovies.core.designsystem.component.zedMoviesImagePlaceholder
import com.zobaer53.zedmovies.core.designsystem.component.ZedMoviesNetworkImage
import com.zobaer53.zedmovies.core.designsystem.component.zedMoviesOverlay
import com.zobaer53.zedmovies.core.designsystem.component.zedMoviesPlaceholder
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.core.model.Credits
import com.zobaer53.zedmovies.core.model.ReleaseDate
import com.zobaer53.zedmovies.core.ui.RatingItem
import com.zobaer53.zedmovies.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Suppress("LongParameterList")
@Composable
internal fun DetailsItem(
    title: String,
    overview: String,
    posterPath: String?,
    releaseDate: ReleaseDate,
    runtime: String,
    genres: List<String>,
    voteAverage: Double,
    credits: Credits,
    isWishlisted: Boolean,
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal + WindowInsetsSides.Top
    ),
    isPlaceholder: Boolean = false
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(BackdropHeight)
        ) {
            if (isPlaceholder) {
                Box(
                    modifier = Modifier
                        .background(color = zedMoviesTheme.colors.primaryVariant)
                        .fillMaxSize()
                )
            } else {
                ZedMoviesNetworkImage(
                    modifier = Modifier.fillMaxSize(),
                    model = posterPath,
                    contentDescription = title
                )
            }
            zedMoviesOverlay(color = zedMoviesTheme.colors.primary, alpha = BackdropAlpha)
        }

        Scaffold(
            topBar = {
                if (isPlaceholder) {
                    TopAppBarPlaceholder(
                        isWishlisted = isWishlisted,
                        onBackButtonClick = onBackButtonClick,
                        onWishlistButtonClick = onWishlistButtonClick
                    )
                } else {
                    TopAppBar(
                        title = title,
                        isWishlisted = isWishlisted,
                        onBackButtonClick = onBackButtonClick,
                        onWishlistButtonClick = onWishlistButtonClick,
                        windowInsets = windowInsets
                    )
                }
            },
            containerColor = Color.Transparent,
            contentWindowInsets = windowInsets
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraMedium)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraMedium)
                    ) {
                        if (isPlaceholder) {
                            zedMoviesImagePlaceholder(
                                modifier = Modifier
                                    .size(width = PosterWidth, height = PosterHeight)
                                    .clip(zedMoviesTheme.shapes.smallMedium)
                            )
                        } else {
                            ZedMoviesCardNetworkImage(
                                modifier = Modifier.size(
                                    width = PosterWidth,
                                    height = PosterHeight
                                ),
                                model = posterPath,
                                contentDescription = title,
                                shape = zedMoviesTheme.shapes.smallMedium
                            )
                        }
                        Column(
                            modifier = Modifier.padding(horizontal = zedMoviesTheme.spacing.largest),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isPlaceholder) {
                                IconAndTextPlaceholder(iconResourceId = R.drawable.ic_calendar)
                                IconAndTextPlaceholder(iconResourceId = R.drawable.ic_clock)
                                IconAndTextPlaceholder(iconResourceId = R.drawable.ic_film)
                            } else {
                                IconAndText(
                                    iconResourceId = R.drawable.ic_calendar,
                                    text = releaseDate.year.ifEmpty { stringResource(id = R.string.no_release_date) }
                                )
                                IconAndText(iconResourceId = R.drawable.ic_clock, text = runtime)
                                IconAndText(
                                    iconResourceId = R.drawable.ic_film,
                                    text = genres.joinToString(separator = GenreSeparator)
                                        .ifEmpty { stringResource(id = R.string.no_genre) }
                                )
                            }
                        }
                        if (isPlaceholder) {
                            RatingItem(
                                title,
                                modifier = Modifier.zedMoviesPlaceholder(
                                    color = zedMoviesTheme.colors.secondaryVariant
                                ),
                                rating = PlaceholderRating
                            )
                        } else {
                            RatingItem(title,rating = voteAverage)
                        }
                    }
                }
                item {
                    if (isPlaceholder) OverviewPlaceholder() else Overview(overview = overview)
                }
                item {
                    if (isPlaceholder) CastAndCrewPlaceholder() else CastAndCrew(credits = credits)
                }
                item {
                    Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                }
            }
        }
    }
}

@Composable
internal fun DetailsItemPlaceholder(
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DetailsItem(
        modifier = modifier,
        title = PlaceholderText,
        overview = PlaceholderText,
        posterPath = null,
        releaseDate = ReleaseDate(),
        runtime = PlaceholderText,
        genres = emptyList(),
        voteAverage = 0.0,
        credits = Credits(cast = emptyList(), crew = emptyList()),
        isWishlisted = false,
        onBackButtonClick = onBackButtonClick,
        onWishlistButtonClick = onWishlistButtonClick,
        isPlaceholder = true
    )
}

private val BackdropHeight = 552.dp
private val PosterWidth = 205.dp
private val PosterHeight = 287.dp
private const val BackdropAlpha = 0.2f
private const val GenreSeparator = ", "
private const val PlaceholderText = ""
private const val PlaceholderRating = 0.0
