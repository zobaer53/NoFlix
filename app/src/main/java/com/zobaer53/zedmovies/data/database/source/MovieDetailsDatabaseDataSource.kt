

package com.zobaer53.zedmovies.data.database.source

import com.zobaer53.zedmovies.core.database.dao.movie.MovieDetailsDao
import com.zobaer53.zedmovies.core.database.model.movie.MovieDetailsEntity
import com.zobaer53.zedmovies.core.database.util.zedMoviesDatabaseTransactionProvider
import javax.inject.Inject

class MovieDetailsDatabaseDataSource @Inject constructor(
    private val movieDetailsDao: MovieDetailsDao,
    private val transactionProvider: zedMoviesDatabaseTransactionProvider
) {
    fun getById(id: Int) = movieDetailsDao.getById(id)
    fun getByIds(ids: List<Int>) = movieDetailsDao.getByIds(ids)

    suspend fun deleteAndInsert(movieDetails: MovieDetailsEntity) =
        transactionProvider.runWithTransaction {
            movieDetailsDao.deleteById(id = movieDetails.id)
            movieDetailsDao.insert(movieDetails)
        }

    suspend fun deleteAndInsertAll(movieDetailsList: List<MovieDetailsEntity>) =
        transactionProvider.runWithTransaction {
            movieDetailsList.forEach { movieDetails ->
                movieDetailsDao.deleteById(id = movieDetails.id)
                movieDetailsDao.insert(movieDetails)
            }
        }
}
