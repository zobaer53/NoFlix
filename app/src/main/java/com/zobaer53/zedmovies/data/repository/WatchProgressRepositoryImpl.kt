package com.zobaer53.zedmovies.data.repository

import com.zobaer53.zedmovies.data.database.mapper.asLastWatchedEpisodeEntity
import com.zobaer53.zedmovies.data.database.mapper.asLastWatchedEpisodeModel
import com.zobaer53.zedmovies.data.database.source.WatchProgressDatabaseDataSource
import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel
import com.zobaer53.zedmovies.domain.repository.WatchProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WatchProgressRepositoryImpl @Inject constructor(
    private val databaseDataSource: WatchProgressDatabaseDataSource
) : WatchProgressRepository {
    override fun getLastWatchedEpisode(tvShowId: Int): Flow<LastWatchedEpisodeModel?> =
        databaseDataSource.getLastWatchedEpisode(tvShowId).map { it?.asLastWatchedEpisodeModel() }

    override suspend fun saveOpenedEpisode(tvShowId: Int, season: Int, episode: Int) {
        val now = System.currentTimeMillis()
        databaseDataSource.updateLastWatchedEpisodes(listOf(tvShowId)) { _, current ->
            current?.asLastWatchedEpisodeModel()
                .withOpenedEpisode(tvShowId, season, episode, now)
                .asLastWatchedEpisodeEntity()
        }
    }

    override suspend fun saveSiteProgress(progress: List<LastWatchedEpisodeModel>) {
        val byShow = progress.groupBy { it.tvShowId }
        databaseDataSource.updateLastWatchedEpisodes(byShow.keys) { tvShowId, current ->
            current?.asLastWatchedEpisodeModel()
                .withSiteProgress(byShow.getValue(tvShowId))
                ?.asLastWatchedEpisodeEntity()
        }
    }
}
