

package com.zobaer53.zedmovies.ui.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zobaer53.zedmovies.data.common.result.handle
import com.zobaer53.zedmovies.domain.model.MovieDetailsModel
import com.zobaer53.zedmovies.domain.model.TvShowDetailsModel
import com.zobaer53.zedmovies.domain.usecase.GetWishlistMoviesUseCase
import com.zobaer53.zedmovies.domain.usecase.GetWishlistTvShowsUseCase
import com.zobaer53.zedmovies.ui.smallcomponent.common.EventHandler
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asMovieDetails
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asTvShowDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistMoviesUseCase: GetWishlistMoviesUseCase,
    private val getWishlistTvShowsUseCase: GetWishlistTvShowsUseCase
) : ViewModel(), EventHandler<WishlistEvent> {
    private val _uiState = MutableStateFlow(WishlistUiState())
    val uiState = _uiState.asStateFlow()

    private var moviesJob = getMoviesJob()
    private var tvShowsJob = getTvShowsJob()

    override fun onEvent(event: WishlistEvent) = when (event) {
        WishlistEvent.RefreshMovies -> onRefreshMovies()
        WishlistEvent.RefreshTvShows -> onRefreshTvShows()
        WishlistEvent.Retry -> onRetry()
        WishlistEvent.ClearError -> onClearError()
    }

    private fun onRefreshMovies() {
        moviesJob.cancel()
        moviesJob = getMoviesJob()
    }

    private fun onRefreshTvShows() {
        tvShowsJob.cancel()
        tvShowsJob = getTvShowsJob()
    }

    private fun onRetry() {
        onClearError()
        onRefreshMovies()
        onRefreshTvShows()
    }

    private fun onClearError() = _uiState.update { it.copy(error = null) }

    private fun getMoviesJob() = viewModelScope.launch {
        getWishlistMoviesUseCase().handle {
            onLoading { movies ->
                _uiState.update {
                    it.copy(
                        movies = movies?.map(MovieDetailsModel::asMovieDetails).orEmpty(),
                        isMoviesLoading = true
                    )
                }
            }
            onSuccess { movies ->
                _uiState.update {
                    it.copy(
                        movies = movies.map(MovieDetailsModel::asMovieDetails),
                        isMoviesLoading = false
                    )
                }
            }
            onFailure(::handleFailure)
        }
    }

    private fun getTvShowsJob() = viewModelScope.launch {
        getWishlistTvShowsUseCase().handle {
            onLoading { tvShows ->
                _uiState.update {
                    it.copy(
                        tvShows = tvShows?.map(TvShowDetailsModel::asTvShowDetails).orEmpty(),
                        isTvShowsLoading = true
                    )
                }
            }
            onSuccess { tvShows ->
                _uiState.update {
                    it.copy(
                        tvShows = tvShows.map(TvShowDetailsModel::asTvShowDetails),
                        isTvShowsLoading = false
                    )
                }
            }
            onFailure(::handleFailure)
        }
    }

    private fun handleFailure(error: Throwable) = _uiState.update {
        it.copy(
            error = error,
            isOfflineModeAvailable = it.movies.isNotEmpty() || it.tvShows.isNotEmpty(),
            isMoviesLoading = false,
            isTvShowsLoading = false
        )
    }
}
