

package com.zobaer53.zedmovies.ui.search

import androidx.paging.PagingData
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.data.model.Movie
import com.zobaer53.zedmovies.data.model.TvShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val searchMovies: Flow<PagingData<Movie>> = emptyFlow(),
    val searchTvShows: Flow<PagingData<TvShow>> = emptyFlow(),
    val movies: Map<MediaType.Movie, List<Movie>> = emptyMap(),
    val tvShows: Map<MediaType.TvShow, List<TvShow>> = emptyMap(),
    val loadStates: Map<MediaType, Boolean> = emptyMap(),
    val error: Throwable? = null,
    val isOfflineModeAvailable: Boolean = false
)

internal val SearchUiState.isLoading: Boolean get() = loadStates.values.any { it }
