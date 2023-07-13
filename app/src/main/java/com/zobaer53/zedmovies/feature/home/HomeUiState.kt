

package com.zobaer53.zedmovies.feature.home

import com.zobaer53.zedmovies.core.model.MediaType
import com.zobaer53.zedmovies.core.model.Movie
import com.zobaer53.zedmovies.core.model.TvShow

data class HomeUiState(
    val movies: Map<MediaType.Movie, List<Movie>> = emptyMap(),
    val tvShows: Map<MediaType.TvShow, List<TvShow>> = emptyMap(),
    val loadStates: Map<MediaType, Boolean> = emptyMap(),
    val error: Throwable? = null,
    val isOfflineModeAvailable: Boolean = false
)

internal val HomeUiState.isLoading: Boolean get() = loadStates.values.any { it }
