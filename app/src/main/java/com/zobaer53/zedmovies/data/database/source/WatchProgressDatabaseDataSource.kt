package com.zobaer53.zedmovies.data.database.source

import com.zobaer53.zedmovies.data.database.dao.watch.LastWatchedEpisodeDao
import com.zobaer53.zedmovies.data.database.model.watch.LastWatchedEpisodeEntity
import com.zobaer53.zedmovies.data.database.util.zedMoviesDatabaseTransactionProvider
import javax.inject.Inject

class WatchProgressDatabaseDataSource @Inject constructor(
    private val lastWatchedEpisodeDao: LastWatchedEpisodeDao,
    private val transactionProvider: zedMoviesDatabaseTransactionProvider
) {
    fun getLastWatchedEpisode(tvShowId: Int) = lastWatchedEpisodeDao.observe(tvShowId)

    /** Reads, transforms and writes back each show's row in one transaction. */
    suspend fun updateLastWatchedEpisodes(
        tvShowIds: Collection<Int>,
        transform: (tvShowId: Int, current: LastWatchedEpisodeEntity?) -> LastWatchedEpisodeEntity?
    ) = transactionProvider.runWithTransaction {
        for (tvShowId in tvShowIds) {
            val current = lastWatchedEpisodeDao.get(tvShowId)
            val updated = transform(tvShowId, current)
            if (updated != null && updated != current) lastWatchedEpisodeDao.upsert(updated)
        }
    }
}
