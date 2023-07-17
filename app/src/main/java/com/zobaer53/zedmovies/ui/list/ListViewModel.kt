

package com.zobaer53.zedmovies.ui.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zobaer53.zedmovies.core.domain.model.MovieModel
import com.zobaer53.zedmovies.core.domain.model.TvShowModel
import com.zobaer53.zedmovies.core.domain.usecase.GetMoviesPagingUseCase
import com.zobaer53.zedmovies.core.domain.usecase.GetTvShowsPagingUseCase
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asMediaTypeModel
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asMovie
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asMovieMediaType
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asTvShow
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asTvShowMediaType
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.pagingMap
import com.zobaer53.zedmovies.ui.list.navigation.ListDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getMoviesPagingUseCase: GetMoviesPagingUseCase,
    private val getTvShowsPagingUseCase: GetTvShowsPagingUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(getInitialUiState(savedStateHandle))
    val uiState = _uiState.asStateFlow()

    private fun getInitialUiState(savedStateHandle: SavedStateHandle): ListUiState {
        val mediaType = ListDestination.fromSavedStateHandle(savedStateHandle)

        val movies = getMoviesPagingUseCase(mediaType.asMovieMediaType().asMediaTypeModel())
            .pagingMap(MovieModel::asMovie)
            .cachedIn(viewModelScope)

        val tvShows = mediaType.asTvShowMediaType()?.let { tvShowMediaType ->
            getTvShowsPagingUseCase(tvShowMediaType.asMediaTypeModel())
                .pagingMap(TvShowModel::asTvShow)
                .cachedIn(viewModelScope)
        } ?: emptyFlow()

        return ListUiState(
            mediaType = mediaType,
            movies = movies,
            tvShows = tvShows
        )
    }
}
