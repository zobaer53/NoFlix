package com.zobaer53.zedmovies.data.database.source

import androidx.paging.PagingSource
import com.zobaer53.zedmovies.core.database.dao.movie.MovieDao
import com.zobaer53.zedmovies.core.database.dao.movie.MovieRemoteKeyDao
import com.zobaer53.zedmovies.core.database.model.common.MediaType
import com.zobaer53.zedmovies.core.database.model.movie.MovieEntity
import com.zobaer53.zedmovies.core.database.model.movie.MovieRemoteKeyEntity
import com.zobaer53.zedmovies.core.database.util.zedMoviesDatabaseTransactionProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieDatabaseDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val movieRemoteKeyDao: MovieRemoteKeyDao,
    private val transactionProvider: zedMoviesDatabaseTransactionProvider
) {
    fun getByMediaType(mediaType: MediaType.Movie, pageSize: Int): Flow<List<MovieEntity>> =
        movieDao.getByMediaType(mediaType, pageSize)

    fun getPagingByMediaType(mediaType: MediaType.Movie): PagingSource<Int, MovieEntity> =
        movieDao.getPagingByMediaType(mediaType)

    suspend fun insertAll(movies: List<MovieEntity>) = movieDao.insertAll(movies)

    suspend fun deleteByMediaTypeAndInsertAll(
        mediaType: MediaType.Movie,
        movies: List<MovieEntity>
    ) = transactionProvider.runWithTransaction {
        movieDao.deleteByMediaType(mediaType)
        movieDao.insertAll(movies)
    }

    suspend fun getRemoteKeyByIdAndMediaType(id: Int, mediaType: MediaType.Movie) =
        movieRemoteKeyDao.getByIdAndMediaType(id, mediaType)

    suspend fun handlePaging(
        mediaType: MediaType.Movie,
        movies: List<MovieEntity>,
        remoteKeys: List<MovieRemoteKeyEntity>,
        shouldDeleteMoviesAndRemoteKeys: Boolean
    ) = transactionProvider.runWithTransaction {
        if (shouldDeleteMoviesAndRemoteKeys) {
            movieDao.deleteByMediaType(mediaType)
            movieRemoteKeyDao.deleteByMediaType(mediaType)
        }
        movieRemoteKeyDao.insertAll(remoteKeys)
        movieDao.insertAll(movies)
    }
}
