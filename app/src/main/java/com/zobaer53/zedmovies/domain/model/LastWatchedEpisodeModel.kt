package com.zobaer53.zedmovies.domain.model

/**
 * The episode of a TV show last opened in the player. The position comes from the stream site's
 * own resume data and is null until the site has reported it.
 */
data class LastWatchedEpisodeModel(
    val tvShowId: Int,
    val season: Int,
    val episode: Int,
    val positionSeconds: Double? = null,
    val durationSeconds: Double? = null,
    val completed: Boolean = false,
    val updatedAt: Long
)
