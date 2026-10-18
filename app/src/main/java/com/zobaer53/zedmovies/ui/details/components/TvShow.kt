
package com.zobaer53.zedmovies.ui.details.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.data.model.TvShowDetails
import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.NoTvShowRuntimeValue
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asNames
import com.zobaer53.zedmovies.R
import kotlin.math.ceil

@Suppress("ReusedModifierInstance")
@Composable
internal fun TvShowDetailsItem(
    tvShowDetails: TvShowDetails,
    lastWatchedEpisode: LastWatchedEpisodeModel?,
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    with(tvShowDetails) {
        DetailsItem(
            modifier = modifier,
            id = id,
            type = "tv",
            title = name,
            overview = overview,
            posterPath = posterPath,
            releaseDate = firstAirDate,
            runtime = if (episodeRunTime == NoTvShowRuntimeValue) {
                stringResource(id = R.string.no_runtime)
            } else {
                stringResource(id = R.string.details_runtime_text, episodeRunTime.toString())
            },
            genres = genres.asNames(),
            voteAverage = 0.0,
            credits = credits,
            trailerKey = trailerKey,
            isWishlisted = isWishlisted,
            onBackButtonClick = onBackButtonClick,
            onWishlistButtonClick = onWishlistButtonClick,
            playLabel = lastWatchedEpisode?.let {
                stringResource(id = R.string.continue_episode, it.season, it.episode)
            },
            playCaption = lastWatchedEpisode?.resumeProgress()
        )
    }
}

/** "Resume at 12:04 · 35 min left", like the site's own Continue button; null if not started or finished. */
@Composable
private fun LastWatchedEpisodeModel.resumeProgress(): String? {
    val position = positionSeconds ?: return null
    val duration = durationSeconds ?: return null
    if (completed || position < MinResumePositionSeconds || position >= duration) return null
    val minutesLeft = ceil((duration - position) / SecondsPerMinute).toInt()
    val timeLeft = if (minutesLeft <= 1) {
        stringResource(id = R.string.less_than_a_minute_left)
    } else {
        stringResource(id = R.string.minutes_left, minutesLeft)
    }
    return stringResource(id = R.string.resume_episode_progress, formatPlaybackTime(position), timeLeft)
}

/** 754.2 -> "12:34", 3723 -> "1:02:03". */
internal fun formatPlaybackTime(seconds: Double): String {
    val total = seconds.toLong().coerceAtLeast(0)
    val hours = total / 3600
    val minutes = total % 3600 / 60
    val secs = total % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, secs)
    } else {
        "%d:%02d".format(minutes, secs)
    }
}

// The site only offers to continue after 15 seconds.
private const val MinResumePositionSeconds = 15.0
private const val SecondsPerMinute = 60.0

@Composable
internal fun TvShowDetailsItemPlaceholder(
    onBackButtonClick: () -> Unit,
    onWishlistButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DetailsItemPlaceholder(
        modifier = modifier,
        onBackButtonClick = onBackButtonClick,
        onWishlistButtonClick = onWishlistButtonClick
    )
}
