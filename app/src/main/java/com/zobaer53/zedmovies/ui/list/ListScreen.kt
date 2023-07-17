

package com.zobaer53.zedmovies.ui.list

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.zobaer53.zedmovies.ui.designsystem.component.zedMoviesTopAppBar
import com.zobaer53.zedmovies.data.model.MediaType.Common.Upcoming
import com.zobaer53.zedmovies.data.model.Movie
import com.zobaer53.zedmovies.data.model.TvShow
import com.zobaer53.zedmovies.ui.smallcomponent.zedMoviesBackButton
import com.zobaer53.zedmovies.ui.smallcomponent.MediaTabPager
import com.zobaer53.zedmovies.ui.smallcomponent.MoviesContainer
import com.zobaer53.zedmovies.ui.smallcomponent.TvShowsContainer
import com.zobaer53.zedmovies.ui.list.util.asTitleResourceId

@Composable
internal fun ListRoute(
    onBackButtonClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val movies = uiState.movies.collectAsLazyPagingItems()
    val tvShows = uiState.tvShows.collectAsLazyPagingItems()
    ListScreen(
        modifier = modifier,
        uiState = uiState,
        movies = movies,
        tvShows = tvShows,
        onBackButtonClick = onBackButtonClick,
        onMovieClick = onMovieClick,
        onTvShowClick = onTvShowClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ListScreen(
    uiState: ListUiState,
    movies: LazyPagingItems<Movie>,
    tvShows: LazyPagingItems<TvShow>,
    onBackButtonClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onTvShowClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            zedMoviesTopAppBar(
                titleResourceId = uiState.mediaType.asTitleResourceId(),
                navigationIcon = { zedMoviesBackButton(onClick = onBackButtonClick) }
            )
        },
      /*  contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top
        )*/
    ) { innerPadding ->
        when (uiState.mediaType) {
            Upcoming -> {
                MoviesContainer(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding),
                    movies = movies,
                    onClick = onMovieClick
                )
            }

            else -> {
                MediaTabPager(
                    modifier = Modifier
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding),
                    moviesTabContent = {
                        MoviesContainer(movies = movies, onClick = onMovieClick)
                    },
                    tvShowsTabContent = {
                        TvShowsContainer(tvShows = tvShows, onClick = onTvShowClick)
                    }
                )
            }
        }
    }
}
