

package com.zobaer53.zedmovies.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zobaer53.zedmovies.core.common.result.handle
import com.zobaer53.zedmovies.core.domain.usecase.AddMovieToWishlistUseCase
import com.zobaer53.zedmovies.core.domain.usecase.AddTvShowToWishlistUseCase
import com.zobaer53.zedmovies.core.domain.usecase.GetMovieDetailsUseCase
import com.zobaer53.zedmovies.core.domain.usecase.GetTvShowDetailsUseCase
import com.zobaer53.zedmovies.core.domain.usecase.RemoveMovieFromWishlistUseCase
import com.zobaer53.zedmovies.core.domain.usecase.RemoveTvShowFromWishlistUseCase
import com.zobaer53.zedmovies.core.model.MediaType
import com.zobaer53.zedmovies.core.model.UserMessage
import com.zobaer53.zedmovies.core.ui.common.EventHandler
import com.zobaer53.zedmovies.core.ui.mapper.asMovieDetails
import com.zobaer53.zedmovies.core.ui.mapper.asTvShowDetails
import com.zobaer53.zedmovies.feature.details.navigation.DetailsDestination
import com.zobaer53.zedmovies.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("LongParameterList", "TooManyFunctions")
@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getTvShowDetailsUseCase: GetTvShowDetailsUseCase,
    private val addMovieToWishlistUseCase: AddMovieToWishlistUseCase,
    private val addTvShowToWishlistUseCase: AddTvShowToWishlistUseCase,
    private val removeMovieFromWishlistUseCase: RemoveMovieFromWishlistUseCase,
    private val removeTvShowFromWishlistUseCase: RemoveTvShowFromWishlistUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel(), EventHandler<DetailsEvent> {
    private val _uiState = MutableStateFlow(getInitialUiState(savedStateHandle))
    val uiState = _uiState.asStateFlow()

    private var contentJob = loadContent()

    override fun onEvent(event: DetailsEvent) = when (event) {
        DetailsEvent.WishlistMovie -> onWishlistMovie()
        DetailsEvent.WishlistTvShow -> onWishlistTvShow()
        DetailsEvent.Refresh -> onRefresh()
        DetailsEvent.Retry -> onRetry()
        DetailsEvent.ClearError -> onClearError()
        DetailsEvent.ClearUserMessage -> onClearUserMessage()
    }

    private fun getInitialUiState(savedStateHandle: SavedStateHandle): DetailsUiState {
        val mediaType = DetailsDestination.fromSavedStateHandle(savedStateHandle)
        return DetailsUiState(mediaType = mediaType)
    }

    private fun loadContent() = when (val mediaType = uiState.value.mediaType) {
        is MediaType.Details.Movie -> loadMovie(mediaType.id)
        is MediaType.Details.TvShow -> loadTvShow(mediaType.id)
    }

    private fun onWishlistMovie() {
        _uiState.update {
            it.copy(movie = it.movie?.copy(isWishlisted = !it.movie.isWishlisted))
        }
        viewModelScope.launch {
            uiState.value.movie?.let { movie ->
                if (movie.isWishlisted) {
                    addMovieToWishlistUseCase(movie.id)
                    setUserMessage(UserMessage(messageResourceId = R.string.add_movie_wishlist))
                } else {
                    removeMovieFromWishlistUseCase(movie.id)
                    setUserMessage(UserMessage(messageResourceId = R.string.remove_movie_wishlist))
                }
            }
        }
    }

    private fun onWishlistTvShow() {
        _uiState.update {
            it.copy(tvShow = it.tvShow?.copy(isWishlisted = !it.tvShow.isWishlisted))
        }
        viewModelScope.launch {
            uiState.value.tvShow?.let { tvShow ->
                if (tvShow.isWishlisted) {
                    addTvShowToWishlistUseCase(tvShow.id)
                    setUserMessage(UserMessage(messageResourceId = R.string.add_tv_show_wishlist))
                } else {
                    removeTvShowFromWishlistUseCase(tvShow.id)
                    setUserMessage(UserMessage(messageResourceId = R.string.remove_tv_show_wishlist))
                }
            }
        }
    }

    private fun setUserMessage(userMessage: UserMessage) =
        _uiState.update { it.copy(userMessage = userMessage) }

    private fun onRefresh() {
        contentJob.cancel()
        contentJob = loadContent()
    }

    private fun onRetry() {
        onClearError()
        onRefresh()
    }

    private fun onClearError() = _uiState.update { it.copy(error = null) }
    private fun onClearUserMessage() = _uiState.update { it.copy(userMessage = null) }

    private fun loadMovie(id: Int) = viewModelScope.launch {
        getMovieDetailsUseCase(id).handle {
            onLoading { movie ->
                _uiState.update { it.copy(movie = movie?.asMovieDetails(), isLoading = true) }
            }
            onSuccess { movie ->
                _uiState.update { it.copy(movie = movie?.asMovieDetails(), isLoading = false) }
            }
            onFailure(::handleFailure)
        }
    }

    private fun loadTvShow(id: Int) = viewModelScope.launch {
        getTvShowDetailsUseCase(id).handle {
            onLoading { tvShow ->
                _uiState.update { it.copy(tvShow = tvShow?.asTvShowDetails(), isLoading = true) }
            }
            onSuccess { tvShow ->
                _uiState.update { it.copy(tvShow = tvShow?.asTvShowDetails(), isLoading = false) }
            }
            onFailure(::handleFailure)
        }
    }

    private fun handleFailure(error: Throwable) = _uiState.update {
        it.copy(
            error = error,
            isOfflineModeAvailable = it.movie != null || it.tvShow != null,
            isLoading = false
        )
    }
}
