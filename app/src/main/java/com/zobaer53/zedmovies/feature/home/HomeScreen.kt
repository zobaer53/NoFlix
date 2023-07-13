
package com.zobaer53.zedmovies.feature.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zobaer53.zedmovies.R
import com.zobaer53.zedmovies.core.designsystem.component.zedMoviesSwipeRefresh
import com.zobaer53.zedmovies.core.designsystem.theme.zedMoviesTheme
import com.zobaer53.zedmovies.core.model.MediaType
import com.zobaer53.zedmovies.core.model.Movie
import com.zobaer53.zedmovies.core.model.TvShow
import com.zobaer53.zedmovies.core.ui.MoviesAndTvShowsContainer
import com.zobaer53.zedmovies.core.ui.mapper.asUserMessage
import com.zobaer53.zedmovies.feature.home.component.UpcomingMoviesContainer

@Composable
internal fun HomeRoute(
    onSeeAllClick: (MediaType.Common) -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onSeeAllClick = onSeeAllClick,
        onMovieClick = onMovieClick,
        onTvShowClick = onTvShowClick,
        onRefresh = { viewModel.onEvent(HomeEvent.Refresh) },
        onRetry = { viewModel.onEvent(HomeEvent.Retry) },
        onOfflineModeClick = { viewModel.onEvent(HomeEvent.ClearError) },
        modifier = modifier
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onSeeAllClick: (MediaType.Common) -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onOfflineModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    zedMoviesSwipeRefresh(
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh
    ) {
        if (uiState.error != null) {
          /*  zedMoviesCenteredError(
                errorMessage = uiState.error.asUserMessage(),
                onRetry = onRetry,
                shouldShowOfflineMode = uiState.isOfflineModeAvailable,
                onOfflineModeClick = onOfflineModeClick
            )*/
            if(uiState.movies.containsKey(MediaType.Movie.Discover)){
                Log.i("error1","ui state error msg ${uiState.error?.message} " +
                        " asUserMessage ${uiState.error?.asUserMessage()} " +
                        " localizedMessage ${uiState.error?.cause}" +
                        "--- Dis")
            }else if(uiState.movies.containsKey(MediaType.Movie.TopRated)){
                Log.i("error1","ui state error msg ${uiState.error?.message} " +
                        " asUserMessage ${uiState.error?.asUserMessage()} " +
                        " localizedMessage ${uiState.error?.cause}" +
                        "--- top")
            }
            else if(uiState.movies.containsKey(MediaType.Movie.NowPlaying)){
                Log.i("error1","ui state error msg ${uiState.error?.message} " +
                        " asUserMessage ${uiState.error?.asUserMessage()} " +
                        " localizedMessage ${uiState.error?.cause}" +
                        "---- now playing")

            }else if(uiState.movies.containsKey(MediaType.Movie.Upcoming)){
                Log.i("error1","ui state error msg ${uiState.error?.message} " +
                        " asUserMessage ${uiState.error?.asUserMessage()} " +
                        " localizedMessage ${uiState.error?.cause}" +
                        "--- upcomig")

            }else if(uiState.movies.containsKey(MediaType.Movie.Popular)){
                Log.i("error1","ui state error msg ${uiState.error?.message} " +
                        " asUserMessage ${uiState.error?.asUserMessage()} " +
                        " localizedMessage ${uiState.error?.cause}" +
                        "--- popular")

            }else if(uiState.movies.containsKey(MediaType.Movie.Trending)){
                Log.i("error1","ui state error msg ${uiState.error?.message} " +
                        " asUserMessage ${uiState.error?.asUserMessage()} " +
                        " localizedMessage ${uiState.error?.cause}" +
                        "---- trending")
            }
        }

       // } else {
            HomeContent(
                movies = uiState.movies,
                tvShows = uiState.tvShows,
                onSeeAllClick = onSeeAllClick,
                onMovieClick = onMovieClick,
                onTvShowClick = onTvShowClick
            )
      //  }
    }
}

@Composable
private fun HomeContent(
    movies: Map<MediaType.Movie, List<Movie>>,
    tvShows: Map<MediaType.TvShow, List<TvShow>>,
    onSeeAllClick: (MediaType.Common) -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(zedMoviesTheme.spacing.extraMedium),
        contentPadding = PaddingValues(vertical = zedMoviesTheme.spacing.extraMedium)
    ) {
        item {
            UpcomingMoviesContainer(
                movies = movies[MediaType.Movie.Upcoming].orEmpty(),
                onSeeAllClick = { onSeeAllClick(MediaType.Common.Upcoming) },
                onMovieClick = onMovieClick
            )
        }
        item {
            MoviesAndTvShowsContainer(
                titleResourceId = R.string.top_rated,
                onSeeAllClick = { onSeeAllClick(MediaType.Common.TopRated) },
                movies = movies[MediaType.Movie.TopRated].orEmpty(),
                tvShows = tvShows[MediaType.TvShow.TopRated].orEmpty(),
                onMovieClick = onMovieClick,
                onTvShowClick = onTvShowClick
            )
        }
        item {
            MoviesAndTvShowsContainer(
                titleResourceId = R.string.most_popular,
                onSeeAllClick = { onSeeAllClick(MediaType.Common.Popular) },
                movies = movies[MediaType.Movie.Popular].orEmpty(),
                tvShows = tvShows[MediaType.TvShow.Popular].orEmpty(),
                onMovieClick = onMovieClick,
                onTvShowClick = onTvShowClick
            )
        }
        item {
            MoviesAndTvShowsContainer(
                titleResourceId = R.string.now_playing,
                onSeeAllClick = { onSeeAllClick(MediaType.Common.NowPlaying) },
                movies = movies[MediaType.Movie.NowPlaying].orEmpty(),
                tvShows = tvShows[MediaType.TvShow.NowPlaying].orEmpty(),
                onMovieClick = onMovieClick,
                onTvShowClick = onTvShowClick
            )
        }
    }
}
