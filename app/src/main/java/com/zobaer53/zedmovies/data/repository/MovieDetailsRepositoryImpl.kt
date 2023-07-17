
package com.zobaer53.zedmovies.data.repository

import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import com.zobaer53.zedmovies.core.data.mapper.asMovieDetailsEntity
import com.zobaer53.zedmovies.core.data.mapper.asMovieDetailsModel
import com.zobaer53.zedmovies.core.data.mapper.listMap
import com.zobaer53.zedmovies.core.database.source.MovieDetailsDatabaseDataSource
import com.zobaer53.zedmovies.core.database.source.WishlistDatabaseDataSource
import com.zobaer53.zedmovies.data.datastore.PreferencesDataStoreDataSource
import com.zobaer53.zedmovies.core.domain.model.MovieDetailsModel
import com.zobaer53.zedmovies.core.domain.repository.MovieDetailsRepository
import com.zobaer53.zedmovies.core.network.common.networkBoundResource
import com.zobaer53.zedmovies.core.network.model.movie.NetworkMovieDetails
import com.zobaer53.zedmovies.core.network.source.MovieDetailsNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieDetailsRepositoryImpl @Inject constructor(
    private val databaseDataSource: MovieDetailsDatabaseDataSource,
    private val networkDataSource: MovieDetailsNetworkDataSource,
    private val wishlistDatabaseDataSource: WishlistDatabaseDataSource,
    private val preferencesDataStoreDataSource: PreferencesDataStoreDataSource
) : MovieDetailsRepository {
    override fun getById(id: Int): Flow<zedMoviesResult<MovieDetailsModel?>> = networkBoundResource(
        query = {
            databaseDataSource.getById(id).map {
                it?.asMovieDetailsModel(
                    isWishlisted = wishlistDatabaseDataSource.isMovieWishlisted(it.id)
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
            databaseDataSource.deleteAndInsert(response.asMovieDetailsEntity())
        }
    )

    override fun getByIds(ids: List<Int>): Flow<zedMoviesResult<List<MovieDetailsModel>>> =
        networkBoundResource(
            query = {
                databaseDataSource.getByIds(ids).listMap {
                    it.asMovieDetailsModel(
                        isWishlisted = wishlistDatabaseDataSource.isMovieWishlisted(it.id)
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
                    response.map(NetworkMovieDetails::asMovieDetailsEntity)
                )
            }
        )
}
