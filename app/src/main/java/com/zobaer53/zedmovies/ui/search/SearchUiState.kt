

package com.zobaer53.zedmovies.ui.search

import androidx.paging.PagingData
import com.zobaer53.zedmovies.data.model.FilterGenre
import com.zobaer53.zedmovies.data.model.FilterOptions
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.data.model.Movie
import com.zobaer53.zedmovies.data.model.SearchFilters
import com.zobaer53.zedmovies.data.model.TvShow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SearchUiState(
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
    /** True while the query or the filters are set: results replace the suggestions. */
    val isSearching: Boolean = false,
    /** Selected genres the tab can't match (see `SearchFiltersModel.unmatchableGenres`). */
    val unmatchableMovieGenres: List<FilterGenre> = emptyList(),
    val unmatchableTvShowGenres: List<FilterGenre> = emptyList(),
    /** Age ratings and streaming services for the sheet; loaded when it first opens. */
    val filterOptions: FilterOptions? = null,
    val isFilterOptionsLoading: Boolean = false,
    val filterOptionsError: Throwable? = null,
    val searchMovies: Flow<PagingData<Movie>> = emptyFlow(),
    val searchTvShows: Flow<PagingData<TvShow>> = emptyFlow(),
    val movies: Map<MediaType.Movie, List<Movie>> = emptyMap(),
    val tvShows: Map<MediaType.TvShow, List<TvShow>> = emptyMap(),
    val loadStates: Map<MediaType, Boolean> = emptyMap(),
    val error: Throwable? = null,
    val isOfflineModeAvailable: Boolean = false
)

internal val SearchUiState.isLoading: Boolean get() = loadStates.values.any { it }
