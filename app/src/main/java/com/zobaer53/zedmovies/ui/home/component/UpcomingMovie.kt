
package com.zobaer53.zedmovies.ui.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesImagePlaceholder
import com.zobaer53.zedmovies.ui.designsystem.component.ZedMoviesNetworkImage
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesPlaceholder
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.data.model.Movie
import com.zobaer53.zedmovies.data.model.ReleaseDate
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.ui.smallcomponent.MoviesContainer
import kotlin.math.absoluteValue


@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun UpcomingMoviesContainer(
    movies: List<Movie>,
    onSeeAllClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState()
    val shouldShowPlaceholder = movies.isEmpty()
    val count = 10

    MoviesContainer(
        titleResourceId = R.string.upcoming_movies,
        onSeeAllClick = onSeeAllClick,
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState,
            count = count,
            contentPadding = PaddingValues(horizontal = zedMoviesTheme.spacing.extraLarge)
        ) { page ->
            val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
            if (shouldShowPlaceholder) {
                UpcomingMovieItemPlaceholder(
                    modifier = Modifier.pagerTransition(pagerScope = this, page = page),
                    pageOffset
                )
            } else {
                with(movies[page]) {
                    UpcomingMovieItem(
                        pageOffset,
                        modifier = Modifier.pagerTransition(
                            pagerScope = this@HorizontalPager,
                            page = page
                        ),
                        title = title,
                        backdropPath = backdropPath,
                        releaseDate = releaseDate,
                        onClick = { onMovieClick(id) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(zedMoviesTheme.spacing.smallMedium))
        DefaultHorizontalPagerIndicator(
            modifier = Modifier
                .padding(horizontal = zedMoviesTheme.spacing.extraMedium)
                .align(Alignment.CenterHorizontally),
            pagerState = pagerState
        )
    }
}

@Composable
private fun UpcomingMovieItem(
    pageOffset: Float,
    title: String,
    backdropPath: String?,
    releaseDate: ReleaseDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = zedMoviesTheme.shapes.medium,

    isPlaceholder: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(UpcomingMovieItemHeight)
            .clip(shape)
            .graphicsLayer {
                animationMovieItem(pageOffset)
            }
            .then(if (isPlaceholder) Modifier else Modifier.clickable(onClick = onClick)),
        shape = shape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isPlaceholder) {
                zedMoviesImagePlaceholder()
            } else {
                ZedMoviesNetworkImage(
                    modifier = Modifier.fillMaxSize(),
                    model = backdropPath,
                    contentDescription = title
                )
            }

            Column(
                modifier = Modifier
                    .padding(
                        start = zedMoviesTheme.spacing.medium,
                        bottom = zedMoviesTheme.spacing.medium,
                        end = zedMoviesTheme.spacing.largest
                    )
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    modifier = if (isPlaceholder) {
                        Modifier
                            .fillMaxWidth()
                            .zedMoviesPlaceholder(color = zedMoviesTheme.colors.textOnMedia)
                    } else {
                        Modifier
                    },
                    text = title,
                    style = zedMoviesTheme.typography.semiBold.h4,
                    color = zedMoviesTheme.colors.textOnMedia
                )
                Spacer(modifier = Modifier.height(zedMoviesTheme.spacing.extraSmall))
                Text(
                    modifier = if (isPlaceholder) {
                        Modifier
                            .fillMaxWidth(
                                UpcomingMovieItemPlaceholderSecondTextMaxWidthFraction
                            )
                            .zedMoviesPlaceholder(color = zedMoviesTheme.colors.textOnMediaVariant)
                    } else {
                        Modifier
                    },
                    text = releaseDate.fullDate.ifEmpty { stringResource(id = R.string.no_release_date) },
                    style = zedMoviesTheme.typography.medium.h6,
                    color = zedMoviesTheme.colors.textOnMediaVariant
                )
            }
        }
    }
}

@Composable
private fun UpcomingMovieItemPlaceholder(modifier: Modifier = Modifier,pageOffset: Float) {
    UpcomingMovieItem(
        pageOffset,
        modifier = modifier,
        title = UpcomingMovieItemPlaceholderText,
        backdropPath = UpcomingMovieItemPlaceholderText,
        releaseDate = ReleaseDate(),
        onClick = {},
        isPlaceholder = true
    )
}
private fun GraphicsLayerScope.animationMovieItem(pageOffset: Float) {
    lerp(
        start = 0.85f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    ).also { scale ->
        scaleX = scale
        scaleY = scale
    }

    alpha = lerp(
        start = 0.5f,
        stop = 1f,
        fraction = 1f - pageOffset.coerceIn(0f, 1f)
    )
}

private val UpcomingMovieItemHeight = 300.dp
private const val UpcomingMovieItemPlaceholderSecondTextMaxWidthFraction = 0.5f
private const val UpcomingMovieItemPlaceholderText = ""

private const val PlaceholderCount = 10
