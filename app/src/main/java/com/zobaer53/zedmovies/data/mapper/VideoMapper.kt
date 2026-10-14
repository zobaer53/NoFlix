package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.network.model.common.NetworkVideo
import com.zobaer53.zedmovies.data.network.model.common.NetworkVideos

/**
 * Picks the YouTube video to show as the trailer: trailers before teasers, then videos in the
 * content language before English ones, then official uploads before the rest.
 */
fun NetworkVideos?.asTrailerKey(contentLanguage: String): String? = this?.results.orEmpty()
    .filter { it.site == YOUTUBE_SITE && it.type in TRAILER_TYPES && it.key.isNotBlank() }
    .minWithOrNull(
        compareBy<NetworkVideo>(
            { TRAILER_TYPES.indexOf(it.type) },
            { it.language != contentLanguage },
            { !it.official }
        )
    )?.key

private const val YOUTUBE_SITE = "YouTube"
private val TRAILER_TYPES = listOf("Trailer", "Teaser")
