package com.zobaer53.zedmovies.data.database.mapper

import com.zobaer53.zedmovies.data.database.model.watch.LastWatchedEpisodeEntity
import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel

fun LastWatchedEpisodeEntity.asLastWatchedEpisodeModel() = LastWatchedEpisodeModel(
    tvShowId = tvShowId,
    season = season,
    episode = episode,
    positionSeconds = positionSeconds,
    durationSeconds = durationSeconds,
    completed = completed,
    updatedAt = updatedAt
)

fun LastWatchedEpisodeModel.asLastWatchedEpisodeEntity() = LastWatchedEpisodeEntity(
    tvShowId = tvShowId,
    season = season,
    episode = episode,
    positionSeconds = positionSeconds,
    durationSeconds = durationSeconds,
    completed = completed,
    updatedAt = updatedAt
)
