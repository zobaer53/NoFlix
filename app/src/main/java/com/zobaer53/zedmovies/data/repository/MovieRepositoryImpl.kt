
package com.zobaer53.zedmovies.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.common.result.map
import com.zobaer53.zedmovies.data.mapper.asMediaType
import com.zobaer53.zedmovies.data.mapper.asMovieEntity
import com.zobaer53.zedmovies.data.mapper.asMovieModel
import com.zobaer53.zedmovies.data.mapper.asNetworkMediaType
import com.zobaer53.zedmovies.data.mapper.listMap
import com.zobaer53.zedmovies.data.mapper.asDiscoverMovieQuery
import com.zobaer53.zedmovies.data.mapper.pagingMap
import com.zobaer53.zedmovies.data.paging.MovieRemoteMediator
import com.zobaer53.zedmovies.data.mapper.filtersSearchResults
import com.zobaer53.zedmovies.data.mapper.matches
import com.zobaer53.zedmovies.data.mapper.searchYear
import com.zobaer53.zedmovies.data.paging.FILTERED_REQUESTS_PER_LOAD
import com.zobaer53.zedmovies.data.paging.ResultsPage
import com.zobaer53.zedmovies.data.paging.TmdbPagingSource
import com.zobaer53.zedmovies.data.util.defaultPagingConfig
import com.zobaer53.zedmovies.data.database.model.movie.MovieEntity
import com.zobaer53.zedmovies.data.database.source.MovieDatabaseDataSource
import com.zobaer53.zedmovies.data.datastore.PreferencesDataStoreDataSource
import com.zobaer53.zedmovies.domain.model.MediaTypeModel
import com.zobaer53.zedmovies.domain.model.MovieModel
import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.repository.MovieRepository
import com.zobaer53.zedmovies.data.network.common.networkBoundResource
import com.zobaer53.zedmovies.data.network.source.MovieNetworkDataSource
import com.zobaer53.zedmovies.data.network.model.movie.NetworkMovie
import com.zobaer53.zedmovies.data.network.model.response.MovieResponseDto
import com.zobaer53.zedmovies.data.network.util.Constants.Discover
import com.zobaer53.zedmovies.data.network.util.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val databaseDataSource: MovieDatabaseDataSource,
    private val networkDataSource: MovieNetworkDataSource,
    private val preferencesDataStoreDataSource: PreferencesDataStoreDataSource
) : MovieRepository {
    override fun getByMediaType(
        mediaTypeModel: MediaTypeModel.Movie
    ): Flow<ZedMoviesResult<List<MovieModel>>> {
        val mediaType = mediaTypeModel.asMediaType()
        return networkBoundResource(
            query = {
                databaseDataSource.getByMediaType(
                    mediaType = mediaType,
                    pageSize = PAGE_SIZE
                ).listMap(MovieEntity::asMovieModel)
            },
            fetch = {
                networkDataSource.getByMediaType(
                    mediaType = mediaType.asNetworkMediaType(),
                    language = preferencesDataStoreDataSource.getContentLanguage().first()
                )
            },
            saveFetchResult = { response ->
                databaseDataSource.deleteByMediaTypeAndInsertAll(
                    mediaType = mediaType,
                    movies = response.results.map {
                        it.asMovieEntity(mediaType) }
                )
            }
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagingByMediaType(
        mediaTypeModel: MediaTypeModel.Movie
    ): Flow<PagingData<MovieModel>> {
        val mediaType = mediaTypeModel.asMediaType()
        return Pager(
            config = defaultPagingConfig,
            remoteMediator = MovieRemoteMediator(
                databaseDataSource = databaseDataSource,
                networkDataSource = networkDataSource,
                preferencesDataStoreDataSource = preferencesDataStoreDataSource,
                mediaType = mediaType
            ),
            pagingSourceFactory = { databaseDataSource.getPagingByMediaType(mediaType) }
        ).flow.pagingMap(MovieEntity::asMovieModel)
    }

    override fun search(
        query: String,
        filters: SearchFiltersModel
    ): Flow<PagingData<MovieModel>> = Pager(
        config = defaultPagingConfig,
        pagingSourceFactory = {
            val requestsPerLoad = if (filters.filtersSearchResults) FILTERED_REQUESTS_PER_LOAD else 1
            TmdbPagingSource(maxRequestsPerLoad = requestsPerLoad) { page ->
                networkDataSource.search(
                    query = query,
                    language = preferencesDataStoreDataSource.getContentLanguage().first(),
                    page = page,
                    year = filters.searchYear
                ).map { response -> response.asResultsPage { filters.matches(it) } }
            }
        }
    ).flow

    override fun discover(filters: SearchFiltersModel): Flow<PagingData<MovieModel>> = Pager(
        config = defaultPagingConfig,
        pagingSourceFactory = {
            val discoverQuery = filters.asDiscoverMovieQuery()
            TmdbPagingSource(maxPage = Discover.MAX_PAGE) { page ->
                networkDataSource.discover(
                    filters = discoverQuery,
                    language = preferencesDataStoreDataSource.getContentLanguage().first(),
                    page = page
                ).map(MovieResponseDto::asResultsPage)
            }
        }
    ).flow
}

private fun MovieResponseDto.asResultsPage(keep: (NetworkMovie) -> Boolean = { true }) = ResultsPage(
    results = results.filter(keep).map(NetworkMovie::asMovieModel),
    totalPages = totalPages
)
