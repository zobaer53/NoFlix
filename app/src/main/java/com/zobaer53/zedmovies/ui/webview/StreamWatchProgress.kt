package com.zobaer53.zedmovies.ui.webview

import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull

/** The localStorage key the stream site keeps its resume data under. */
const val STREAM_WATCH_PROGRESS_KEY = "framextv_watch_progress_v1"

/** Signal posted by the stream page when a video is actively decoding and rendering frames. */
data class PlaybackActiveSignal(
    val videoWidth: Int,
    val videoHeight: Int,
    val renderedWidth: Int,
    val renderedHeight: Int,
    val currentTime: Double
)

fun parsePlaybackActiveSignal(json: String?): PlaybackActiveSignal? {
    if (json == null || !json.contains("\"playback_active\"")) return null
    val obj = runCatching { Json.parseToJsonElement(json) as? JsonObject }.getOrNull() ?: return null
    if (obj.string("type") != "playback_active") return null
    val vw = obj.int("videoWidth") ?: 0
    val vh = obj.int("videoHeight") ?: 0
    val rw = obj.int("renderedWidth") ?: 0
    val rh = obj.int("renderedHeight") ?: 0
    val time = obj.number("currentTime") ?: 0.0
    return PlaybackActiveSignal(vw, vh, rw, rh, time)
}

/**
 * Reads the TV entries of the site's resume data: a JSON array of
 * `{mediaType, id, season, episode, timestamp, duration, completed, updatedAt}`, newest first.
 * The format is the site's own and can change at any time, so anything unexpected is skipped.
 */
fun parseStreamWatchProgress(json: String?): List<LastWatchedEpisodeModel> {
    val entries = json?.let { runCatching { Json.parseToJsonElement(it) }.getOrNull() }
    return (entries as? JsonArray).orEmpty().mapNotNull { (it as? JsonObject)?.asLastWatchedEpisode() }
}

private fun JsonObject.asLastWatchedEpisode(): LastWatchedEpisodeModel? {
    if (string("mediaType") != "tv") return null
    return LastWatchedEpisodeModel(
        tvShowId = string("id")?.trim()?.toIntOrNull()?.takeIf { it > 0 } ?: return null,
        season = int("season")?.takeIf { it >= 0 } ?: return null,
        episode = int("episode")?.takeIf { it > 0 } ?: return null,
        positionSeconds = number("timestamp")?.takeIf { it >= 0 },
        durationSeconds = number("duration")?.takeIf { it > 0 },
        completed = (get("completed") as? JsonPrimitive)?.booleanOrNull ?: false,
        updatedAt = number("updatedAt")?.toLong() ?: return null
    )
}

private fun JsonObject.string(key: String) = (get(key) as? JsonPrimitive)?.contentOrNull

private fun JsonObject.number(key: String) =
    (get(key) as? JsonPrimitive)?.doubleOrNull?.takeIf { it.isFinite() }

private fun JsonObject.int(key: String) =
    number(key)?.takeIf { it == Math.floor(it) && it <= Int.MAX_VALUE }?.toInt()
