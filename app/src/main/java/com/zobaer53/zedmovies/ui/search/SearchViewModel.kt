

package com.zobaer53.zedmovies.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zobaer53.zedmovies.data.common.result.handle
import com.zobaer53.zedmovies.data.common.result.isFailure
import com.zobaer53.zedmovies.data.common.result.isSuccess
import com.zobaer53.zedmovies.data.model.SearchFilters
import com.zobaer53.zedmovies.domain.model.FilterMediaTypeModel
import com.zobaer53.zedmovies.domain.model.MovieModel
import com.zobaer53.zedmovies.domain.usecase.DiscoverMoviesUseCase
import com.zobaer53.zedmovies.domain.usecase.DiscoverTvShowsUseCase
import com.zobaer53.zedmovies.domain.usecase.GetFilterOptionsUseCase
import com.zobaer53.zedmovies.domain.model.TvShowModel
import com.zobaer53.zedmovies.domain.usecase.GetMoviesUseCase
import com.zobaer53.zedmovies.domain.usecase.SearchMoviesUseCase
import com.zobaer53.zedmovies.domain.usecase.SearchTvShowsUseCase
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.data.model.Movie
import com.zobaer53.zedmovies.data.model.TvShow
import com.zobaer53.zedmovies.domain.usecase.GetTvShowsUseCase
import com.zobaer53.zedmovies.ui.smallcomponent.common.EventHandler
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asFilterGenre
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asFilterOptions
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asMediaTypeModel
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asMovie
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asSearchFiltersModel
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asTvShow
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.pagingMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Suppress("TooManyFunctions", "LongParameterList")
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getTvShowsUseCase: GetTvShowsUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val searchTvShowsUseCase: SearchTvShowsUseCase,
    private val discoverMoviesUseCase: DiscoverMoviesUseCase,
    private val discoverTvShowsUseCase: DiscoverTvShowsUseCase,
    private val getFilterOptionsUseCase: GetFilterOptionsUseCase
) : ViewModel(), EventHandler<SearchEvent> {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    /** Owns the paged results' caches, so replaced results stop collecting. */
    private var resultsScope: CoroutineScope? = null

    init {
        val filters = restoreFilters()
        if (filters.isActive) {
            _uiState.update { it.copy(filters = filters, isSearching = true) }
            showResults()
        }
        loadContent()
    }

    override fun onEvent(event: SearchEvent) = when (event) {
        is SearchEvent.ChangeQuery -> onQueryChange(event.value)
        is SearchEvent.ApplyFilters -> onFiltersChange(event.filters)
        is SearchEvent.RemoveFilter -> onFiltersChange(_uiState.value.filters.without(event.filter))
        SearchEvent.ResetFilters -> onFiltersChange(SearchFilters())
        SearchEvent.LoadFilterOptions -> loadFilterOptions()
        SearchEvent.Refresh -> onRefresh()
        SearchEvent.Retry -> onRetry()
        SearchEvent.ClearError -> onClearError()
    }

    private fun onQueryChange(query: String) {
        _uiState.update {
            it.copy(query = query, isSearching = query.isNotBlank() || it.filters.isActive)
        }
        searchDebounced()
    }

    private fun onFiltersChange(filters: SearchFilters) {
        if (filters == _uiState.value.filters) return
        savedStateHandle[FILTERS_KEY] = Json.encodeToString(SearchFilters.serializer(), filters)
        _uiState.update {
            it.copy(filters = filters, isSearching = it.query.isNotBlank() || filters.isActive)
        }
        searchJob?.cancel()
        showResults()
    }

    private fun loadFilterOptions() {
        val state = _uiState.value
        if (state.filterOptions != null || state.isFilterOptionsLoading) return
        _uiState.update { it.copy(isFilterOptionsLoading = true, filterOptionsError = null) }
        viewModelScope.launch {
            val result = getFilterOptionsUseCase()
            _uiState.update {
                when {
                    result.isSuccess() -> it.copy(
                        filterOptions = result.value.asFilterOptions(),
                        isFilterOptionsLoading = false
                    )
                    result.isFailure() -> it.copy(
                        filterOptionsError = result.error,
                        isFilterOptionsLoading = false
                    )
                    else -> it
                }
            }
        }
    }

    private fun restoreFilters(): SearchFilters = savedStateHandle.get<String>(FILTERS_KEY)
        ?.let { runCatching { Json.decodeFromString(SearchFilters.serializer(), it) }.getOrNull() }
        ?: SearchFilters()

    private fun searchDebounced() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DURATION)
            showResults()
        }
    }

    override fun onCleared() {
        resultsScope?.cancel()
    }

    /** Blank query: discover with the filters. Otherwise: search, filtered in the app. */
    private fun showResults() {
        resultsScope?.cancel()
        val state = _uiState.value
        if (!state.isSearching) {
            resultsScope = null
            _uiState.update {
                it.copy(
                    searchMovies = emptyFlow(),
                    searchTvShows = emptyFlow(),
                    unmatchableMovieGenres = emptyList(),
                    unmatchableTvShowGenres = emptyList()
                )
            }
            return
        }
        // Not a child of viewModelScope: onRefresh cancels its children, and results must survive it.
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        resultsScope = scope

        val query = state.query.trim()
        val filters = state.filters.asSearchFiltersModel()
        val unmatchableMovieGenres = filters.unmatchableGenres(FilterMediaTypeModel.MOVIE)
        val unmatchableTvShowGenres = filters.unmatchableGenres(FilterMediaTypeModel.TV_SHOW)

        val searchMovies = when {
            unmatchableMovieGenres.isNotEmpty() -> emptyFlow()
            query.isEmpty() -> discoverMoviesUseCase(filters)
            else -> searchMoviesUseCase(query, filters)
        }.pagingMap(MovieModel::asMovie).cachedIn(scope)

        val searchTvShows = when {
            unmatchableTvShowGenres.isNotEmpty() -> emptyFlow()
            query.isEmpty() -> discoverTvShowsUseCase(filters)
            else -> searchTvShowsUseCase(query, filters)
        }.pagingMap(TvShowModel::asTvShow).cachedIn(scope)

        _uiState.update {
            it.copy(
                searchMovies = searchMovies,
                searchTvShows = searchTvShows,
                unmatchableMovieGenres = unmatchableMovieGenres.map { genre -> genre.asFilterGenre() },
                unmatchableTvShowGenres = unmatchableTvShowGenres.map { genre -> genre.asFilterGenre() }
            )
        }
    }

    private fun loadContent() {
        val movieMediaTypes = listOf(
            MediaType.Movie.Discover,
            MediaType.Movie.Trending
        )
        movieMediaTypes.forEach(::loadMovies)

        val tvShowMediaTypes = listOf(
            MediaType.TvShow.Discover,
            MediaType.TvShow.Trending
        )
        tvShowMediaTypes.forEach(::loadTvShows)
    }

    private fun onRefresh() {
        viewModelScope.coroutineContext.cancelChildren()
        loadContent()
    }

    private fun onRetry() {
        onClearError()
        onRefresh()
    }

    private fun onClearError() = _uiState.update { it.copy(error = null) }

    private fun loadMovies(mediaType: MediaType.Movie) = viewModelScope.launch {
        getMoviesUseCase(mediaType.asMediaTypeModel()).handle {
            onLoading { movies ->
                _uiState.update {
                    it.copy(
                        movies = it.movies + (
                            mediaType to movies?.map(MovieModel::asMovie).orEmpty()
                            ),
                        loadStates = it.loadStates + (mediaType to true)
                    )
                }
            }
            onSuccess { movies ->
                _uiState.update {
                    it.copy(
                        movies = it.movies + (mediaType to movies.map(MovieModel::asMovie)),
                        loadStates = it.loadStates + (mediaType to false)
                    )
                }

                if (movies.isEmpty()) {
                    onRefresh()
                }
            }
            onFailure { error -> handleFailure(error = error, mediaType = mediaType) }
        }
    }

    private fun loadTvShows(mediaType: MediaType.TvShow) = viewModelScope.launch {
        getTvShowsUseCase(mediaType.asMediaTypeModel()).handle {
            onLoading { tvShows ->
                _uiState.update {
                    it.copy(
                        tvShows = it.tvShows + (
                            mediaType to tvShows?.map(TvShowModel::asTvShow).orEmpty()
                            ),
                        loadStates = it.loadStates + (mediaType to true)
                    )
                }
            }
            onSuccess { tvShows ->
                _uiState.update {
                    it.copy(
                        tvShows = it.tvShows + (mediaType to tvShows.map(TvShowModel::asTvShow)),
                        loadStates = it.loadStates + (mediaType to false)
                    )
                }
            }
            onFailure { error -> handleFailure(error = error, mediaType = mediaType) }
        }
    }

    private fun handleFailure(error: Throwable, mediaType: MediaType) =
        _uiState.update {
            it.copy(
                error = error,
                isOfflineModeAvailable = it.movies.values.all(List<Movie>::isNotEmpty) ||
                    it.tvShows.values.all(List<TvShow>::isNotEmpty),
                loadStates = it.loadStates + (mediaType to false)
            )
        }
}

private const val SEARCH_DEBOUNCE_DURATION = 500L
private const val FILTERS_KEY = "search_filters"
