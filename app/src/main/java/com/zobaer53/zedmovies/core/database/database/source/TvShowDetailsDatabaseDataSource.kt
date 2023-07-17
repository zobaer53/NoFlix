package com.zobaer53.zedmovies.core.database.source

import com.zobaer53.zedmovies.core.database.dao.tvshow.TvShowDetailsDao
import com.zobaer53.zedmovies.core.database.model.tvshow.TvShowDetailsEntity
import com.zobaer53.zedmovies.core.database.util.zedMoviesDatabaseTransactionProvider
import javax.inject.Inject

class TvShowDetailsDatabaseDataSource @Inject constructor(
    private val tvShowDetailsDao: TvShowDetailsDao,
    private val transactionProvider: zedMoviesDatabaseTransactionProvider
) {
    fun getById(id: Int) = tvShowDetailsDao.getById(id)
    fun getByIds(ids: List<Int>) = tvShowDetailsDao.getByIds(ids)

    suspend fun deleteAndInsert(tvShowDetails: TvShowDetailsEntity) =
        transactionProvider.runWithTransaction {
            tvShowDetailsDao.deleteById(id = tvShowDetails.id)
            tvShowDetailsDao.insert(tvShowDetails)
        }

    suspend fun deleteAndInsertAll(tvShowDetailsList: List<TvShowDetailsEntity>) =
        transactionProvider.runWithTransaction {
            tvShowDetailsList.forEach { tvShowDetails ->
                tvShowDetailsDao.deleteById(id = tvShowDetails.id)
                tvShowDetailsDao.insert(tvShowDetails)
            }
        }
}
