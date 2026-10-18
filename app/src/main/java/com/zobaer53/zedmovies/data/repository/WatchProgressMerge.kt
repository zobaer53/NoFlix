package com.zobaer53.zedmovies.data.repository

import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel

/** Opening an episode makes it the last watched one; reopening the same one keeps its position. */
internal fun LastWatchedEpisodeModel?.withOpenedEpisode(
    tvShowId: Int,
    season: Int,
    episode: Int,
    now: Long
): LastWatchedEpisodeModel =
    if (this != null && this.season == season && this.episode == episode) {
        copy(updatedAt = now)
    } else {
        LastWatchedEpisodeModel(tvShowId, season, episode, updatedAt = now)
    }

/**
 * Applies one entry of the site's progress. An entry for the stored episode only updates its
 * position. An entry for another episode replaces it when it is newer, unless it is a finished
 * episode replacing an unfinished one: the site can save the end of an episode just after its
 * player has auto-advanced to the next, which must not move the user back.
 */
internal fun LastWatchedEpisodeModel?.withSiteProgress(
    site: LastWatchedEpisodeModel
): LastWatchedEpisodeModel = when {
    this == null -> site
    season == site.season && episode == site.episode -> copy(
        positionSeconds = site.positionSeconds,
        durationSeconds = site.durationSeconds,
        completed = site.completed,
        updatedAt = maxOf(updatedAt, site.updatedAt)
    )
    site.updatedAt > updatedAt && (!site.completed || completed) -> site
    else -> this
}

/** Applies a show's site entries oldest first, so the newest one decides. */
internal fun LastWatchedEpisodeModel?.withSiteProgress(
    site: List<LastWatchedEpisodeModel>
): LastWatchedEpisodeModel? =
    site.sortedBy { it.updatedAt }.fold(this) { current, entry -> current.withSiteProgress(entry) }
