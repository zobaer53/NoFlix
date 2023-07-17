

package com.zobaer53.zedmovies.data.repository

import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import com.zobaer53.zedmovies.data.mapper.asTvShowDetailsEntity
import com.zobaer53.zedmovies.data.mapper.asTvShowDetailsModel
import com.zobaer53.zedmovies.data.mapper.listMap
import com.zobaer53.zedmovies.data.database.source.TvShowDetailsDatabaseDataSource
import com.zobaer53.zedmovies.data.database.source.WishlistDatabaseDataSource
import com.zobaer53.zedmovies.data.datastore.PreferencesDataStoreDataSource
import com.zobaer53.zedmovies.domain.model.TvShowDetailsModel
import com.zobaer53.zedmovies.domain.repository.TvShowDetailsRepository
import com.zobaer53.zedmovies.data.network.common.networkBoundResource
import com.zobaer53.zedmovies.data.network.model.tvshow.NetworkTvShowDetails
import com.zobaer53.zedmovies.data.network.source.TvShowDetailsNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TvShowDetailsRepositoryImpl @Inject constructor(
    private val databaseDataSource: TvShowDetailsDatabaseDataSource,
    private val networkDataSource: TvShowDetailsNetworkDataSource,
    private val wishlistDatabaseDataSource: WishlistDatabaseDataSource,
    private val preferencesDataStoreDataSource: PreferencesDataStoreDataSource
) : TvShowDetailsRepository {
    override fun getById(id: Int): Flow<zedMoviesResult<TvShowDetailsModel?>> = networkBoundResource(
        query = {
            databaseDataSource.getById(id).map {
                it?.asTvShowDetailsModel(
                    isWishlisted = wishlistDatabaseDataSource.isTvShowWishlisted(it.id)
                )
            }
        },
        fetch = {
            networkDataSource.getById(
                id = id,
                language = preferencesDataStoreDataSource.getContentLanguage().first()
            )
        },
        saveFetchResult = { response ->
            databaseDataSource.deleteAndInsert(response.asTvShowDetailsEntity())
        }
    )

    override fun getByIds(ids: List<Int>): Flow<zedMoviesResult<List<TvShowDetailsModel>>> =
        networkBoundResource(
            query = {
                databaseDataSource.getByIds(ids).listMap {
                    it.asTvShowDetailsModel(
                        isWishlisted = wishlistDatabaseDataSource.isTvShowWishlisted(it.id)
                    )
                }
            },
            fetch = {
                networkDataSource.getByIds(
                    ids = ids,
                    language = preferencesDataStoreDataSource.getContentLanguage().first()
                )
            },
            saveFetchResult = { response ->
                databaseDataSource.deleteAndInsertAll(
                    response.map(NetworkTvShowDetails::asTvShowDetailsEntity)
                )
            }
        )
}
