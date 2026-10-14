

package com.zobaer53.zedmovies.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.common.result.map
import com.zobaer53.zedmovies.data.mapper.asMediaType
import com.zobaer53.zedmovies.data.mapper.asNetworkMediaType
import com.zobaer53.zedmovies.data.mapper.asTvShowEntity
import com.zobaer53.zedmovies.data.mapper.asTvShowModel
import com.zobaer53.zedmovies.data.mapper.listMap
import com.zobaer53.zedmovies.data.mapper.asDiscoverTvQuery
import com.zobaer53.zedmovies.data.mapper.pagingMap
import com.zobaer53.zedmovies.data.mapper.filtersSearchResults
import com.zobaer53.zedmovies.data.mapper.matches
import com.zobaer53.zedmovies.data.mapper.searchYear
import com.zobaer53.zedmovies.data.paging.FILTERED_REQUESTS_PER_LOAD
import com.zobaer53.zedmovies.data.paging.ResultsPage
import com.zobaer53.zedmovies.data.paging.TmdbPagingSource
import com.zobaer53.zedmovies.data.paging.TvShowRemoteMediator
import com.zobaer53.zedmovies.data.util.defaultPagingConfig
import com.zobaer53.zedmovies.data.database.model.tvshow.TvShowEntity
import com.zobaer53.zedmovies.data.database.source.TvShowDatabaseDataSource
import com.zobaer53.zedmovies.data.datastore.PreferencesDataStoreDataSource
import com.zobaer53.zedmovies.domain.model.MediaTypeModel
import com.zobaer53.zedmovies.domain.model.TvShowModel
import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.repository.TvShowRepository
import com.zobaer53.zedmovies.data.network.common.networkBoundResource
import com.zobaer53.zedmovies.data.network.source.TvShowNetworkDataSource
import com.zobaer53.zedmovies.data.network.model.tvshow.NetworkTvShow
import com.zobaer53.zedmovies.data.network.model.response.TvShowResponseDto
import com.zobaer53.zedmovies.data.network.util.Constants.Discover
import com.zobaer53.zedmovies.data.network.util.PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TvShowRepositoryImpl @Inject constructor(
    private val databaseDataSource: TvShowDatabaseDataSource,
    private val networkDataSource: TvShowNetworkDataSource,
    private val preferencesDataStoreDataSource: PreferencesDataStoreDataSource
) : TvShowRepository {
    override fun getByMediaType(
        mediaTypeModel: MediaTypeModel.TvShow
    ): Flow<ZedMoviesResult<List<TvShowModel>>> {
        val mediaType = mediaTypeModel.asMediaType()
        return networkBoundResource(
            query = {
                databaseDataSource.getByMediaType(
                    mediaType = mediaType,
                    pageSize = PAGE_SIZE
                ).listMap(TvShowEntity::asTvShowModel)
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
                    tvShows = response.results.map {
                        it.asTvShowEntity(mediaType) }
                )
            }
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagingByMediaType(
        mediaTypeModel: MediaTypeModel.TvShow
    ): Flow<PagingData<TvShowModel>> {
        val mediaType = mediaTypeModel.asMediaType()
        return Pager(
            config = defaultPagingConfig,
            remoteMediator = TvShowRemoteMediator(
                databaseDataSource = databaseDataSource,
                networkDataSource = networkDataSource,
                preferencesDataStoreDataSource = preferencesDataStoreDataSource,
                mediaType = mediaType
            ),
            pagingSourceFactory = { databaseDataSource.getPagingByMediaType(mediaType) }
        ).flow.pagingMap(TvShowEntity::asTvShowModel)
    }

    override fun search(
        query: String,
        filters: SearchFiltersModel
    ): Flow<PagingData<TvShowModel>> = Pager(
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

    override fun discover(filters: SearchFiltersModel): Flow<PagingData<TvShowModel>> = Pager(
        config = defaultPagingConfig,
        pagingSourceFactory = {
            val discoverQuery = filters.asDiscoverTvQuery()
            TmdbPagingSource(maxPage = Discover.MAX_PAGE) { page ->
                networkDataSource.discover(
                    filters = discoverQuery,
                    language = preferencesDataStoreDataSource.getContentLanguage().first(),
                    page = page
                ).map(TvShowResponseDto::asResultsPage)
            }
        }
    ).flow
}

private fun TvShowResponseDto.asResultsPage(keep: (NetworkTvShow) -> Boolean = { true }) = ResultsPage(
    results = results.filter(keep).map(NetworkTvShow::asTvShowModel),
    totalPages = totalPages
)
