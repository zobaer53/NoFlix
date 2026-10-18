package com.zobaer53.zedmovies.data.database.dao.watch

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.zobaer53.zedmovies.data.database.model.watch.LastWatchedEpisodeEntity
import com.zobaer53.zedmovies.data.database.util.Constants.Tables.LAST_WATCHED_EPISODES
import kotlinx.coroutines.flow.Flow

@Dao
interface LastWatchedEpisodeDao {
    @Query("SELECT * FROM $LAST_WATCHED_EPISODES WHERE tv_show_id = :tvShowId")
    fun observe(tvShowId: Int): Flow<LastWatchedEpisodeEntity?>

    @Query("SELECT * FROM $LAST_WATCHED_EPISODES WHERE tv_show_id = :tvShowId")
    suspend fun get(tvShowId: Int): LastWatchedEpisodeEntity?

    @Upsert
    suspend fun upsert(episode: LastWatchedEpisodeEntity)
}
