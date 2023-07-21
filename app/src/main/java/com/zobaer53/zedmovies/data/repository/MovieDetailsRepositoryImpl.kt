
package com.zobaer53.zedmovies.data.repository

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.mapper.asMovieDetailsEntity
import com.zobaer53.zedmovies.data.mapper.asMovieDetailsModel
import com.zobaer53.zedmovies.data.mapper.listMap
import com.zobaer53.zedmovies.data.database.source.MovieDetailsDatabaseDataSource
import com.zobaer53.zedmovies.data.database.source.WishlistDatabaseDataSource
import com.zobaer53.zedmovies.data.datastore.PreferencesDataStoreDataSource
import com.zobaer53.zedmovies.domain.model.MovieDetailsModel
import com.zobaer53.zedmovies.domain.repository.MovieDetailsRepository
import com.zobaer53.zedmovies.data.network.common.networkBoundResource
import com.zobaer53.zedmovies.data.network.model.movie.NetworkMovieDetails
import com.zobaer53.zedmovies.data.network.source.MovieDetailsNetworkDataSource
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
    override fun getById(id: Int): Flow<ZedMoviesResult<MovieDetailsModel?>> = networkBoundResource(
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

    override fun getByIds(ids: List<Int>): Flow<ZedMoviesResult<List<MovieDetailsModel>>> =
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
