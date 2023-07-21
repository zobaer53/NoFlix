

package com.zobaer53.zedmovies.ui.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesMessage
import com.zobaer53.zedmovies.ui.designsystem.component.ZedMoviesSwipeRefresh
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.data.model.MovieDetails
import com.zobaer53.zedmovies.data.model.TvShowDetails
import com.zobaer53.zedmovies.ui.smallcomponent.zedMoviesCenteredError
import com.zobaer53.zedmovies.ui.smallcomponent.MediaTabPager
import com.zobaer53.zedmovies.ui.smallcomponent.VerticalMovieItem
import com.zobaer53.zedmovies.ui.smallcomponent.VerticalMovieItemPlaceholder
import com.zobaer53.zedmovies.ui.smallcomponent.VerticalTvShowItem
import com.zobaer53.zedmovies.ui.smallcomponent.VerticalTvShowItemPlaceholder
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asUserMessage

@Composable
internal fun WishlistRoute(
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WishlistScreen(
        uiState = uiState,
        onRefreshMovies = { viewModel.onEvent(WishlistEvent.RefreshMovies) },
        onRefreshTvShows = { viewModel.onEvent(WishlistEvent.RefreshTvShows) },
        onMovieClick = onMovieClick,
        onTvShowClick = onTvShowClick,
        onRetry = { viewModel.onEvent(WishlistEvent.Retry) },
        onOfflineModeClick = { viewModel.onEvent(WishlistEvent.ClearError) },
        modifier = modifier
    )
}

@Composable
private fun WishlistScreen(
    uiState: WishlistUiState,
    onRefreshMovies: () -> Unit,
    onRefreshTvShows: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onOfflineModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
        if (uiState.error != null) {
            zedMoviesCenteredError(
                errorMessage = uiState.error.asUserMessage(),
                onRetry = onRetry,
                shouldShowOfflineMode = uiState.isOfflineModeAvailable,
                onOfflineModeClick = onOfflineModeClick
            )
        } else {
            MediaTabPager(
                moviesTabContent = {
                    MoviesContainer(
                        movies = uiState.movies,
                        isLoading = uiState.isMoviesLoading,
                        onRefresh = onRefreshMovies,
                        onClick = onMovieClick
                    )
                },
                tvShowsTabContent = {
                    TvShowsContainer(
                        tvShows = uiState.tvShows,
                        isLoading = uiState.isTvShowsLoading,
                        onRefresh = onRefreshTvShows,
                        onClick = onTvShowClick
                    )
                }
            )
        }
    }
}

@Composable
private fun MoviesContainer(
    movies: List<MovieDetails>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    emptyContent: @Composable LazyItemScope.() -> Unit = {
        zedMoviesMessage(
            modifier = Modifier.fillParentMaxSize(),
            messageResourceId = R.string.no_movie_wishlist,
            imageResourceId = R.drawable.no_wishlist_results
        )
    }
) {
    ZedMoviesSwipeRefresh(
        modifier = modifier,
        isRefreshing = isLoading,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.medium),
            contentPadding = PaddingValues(zedMoviesTheme.spacing.extraMedium)
        ) {
            when {
                movies.isNotEmpty() -> {
                    items(movies) { movie ->
                        VerticalMovieItem(movie = movie, onClick = onClick)
                    }
                }
                isLoading -> {
                    items(PlaceholderCount) { VerticalMovieItemPlaceholder() }
                }
                else -> item(content = emptyContent)
            }
        }
    }
}

@Composable
private fun TvShowsContainer(
    tvShows: List<TvShowDetails>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    emptyContent: @Composable LazyItemScope.() -> Unit = {
        zedMoviesMessage(
            modifier = Modifier.fillParentMaxSize(),
            messageResourceId = R.string.no_tv_show_wishlist,
            imageResourceId = R.drawable.no_wishlist_results
        )
    }
) {
    ZedMoviesSwipeRefresh(
        modifier = modifier,
        isRefreshing = isLoading,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.medium),
            contentPadding = PaddingValues(zedMoviesTheme.spacing.extraMedium)
        ) {
            when {
                tvShows.isNotEmpty() -> {
                    items(tvShows) { tvShow ->
                        VerticalTvShowItem(tvShow = tvShow, onClick = onClick)
                    }
                }
                isLoading -> {
                    items(PlaceholderCount) { VerticalTvShowItemPlaceholder() }
                }
                else -> item(content = emptyContent)
            }
        }
    }
}

private const val PlaceholderCount = 20
