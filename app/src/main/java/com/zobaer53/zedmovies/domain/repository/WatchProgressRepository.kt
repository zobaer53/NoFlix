package com.zobaer53.zedmovies.domain.repository

import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel
import kotlinx.coroutines.flow.Flow

interface WatchProgressRepository {
    fun getLastWatchedEpisode(tvShowId: Int): Flow<LastWatchedEpisodeModel?>

    /** The player opened this episode, so it is now the show's last watched one. */
    suspend fun saveOpenedEpisode(tvShowId: Int, season: Int, episode: Int)

    /** Merges the progress the stream site keeps for its own resume feature. */
    suspend fun saveSiteProgress(progress: List<LastWatchedEpisodeModel>)
}
