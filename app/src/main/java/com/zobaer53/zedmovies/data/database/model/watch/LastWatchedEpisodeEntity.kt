package com.zobaer53.zedmovies.data.database.model.watch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zobaer53.zedmovies.data.database.util.Constants

/** The episode of a TV show last opened in the player: one row per show. */
@Entity(tableName = Constants.Tables.LAST_WATCHED_EPISODES)
data class LastWatchedEpisodeEntity(
    @PrimaryKey
    @ColumnInfo(name = Constants.Fields.TV_SHOW_ID)
    val tvShowId: Int,

    @ColumnInfo(name = Constants.Fields.SEASON)
    val season: Int,

    @ColumnInfo(name = Constants.Fields.EPISODE)
    val episode: Int,

    @ColumnInfo(name = Constants.Fields.POSITION_SECONDS)
    val positionSeconds: Double?,

    @ColumnInfo(name = Constants.Fields.DURATION_SECONDS)
    val durationSeconds: Double?,

    @ColumnInfo(name = Constants.Fields.COMPLETED)
    val completed: Boolean,

    @ColumnInfo(name = Constants.Fields.UPDATED_AT)
    val updatedAt: Long
)
