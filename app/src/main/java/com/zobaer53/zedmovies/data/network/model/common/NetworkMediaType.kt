

package com.zobaer53.zedmovies.data.network.model.common

sealed interface NetworkMediaType {
    enum class Movie(val mediaType: String) : NetworkMediaType {
        UPCOMING(UPCOMING_MEDIA_TYPE),
        TOP_RATED(TOP_RATED_MEDIA_TYPE),
        POPULAR(POPULAR_MEDIA_TYPE),
        NOW_PLAYING(NOW_PLAYING_MEDIA_TYPE),
        DISCOVER(DISCOVER_MEDIA_TYPE),
        TRENDING(TRENDING_MEDIA_TYPE);

        companion object {
            private val mediaTypes = values().associateBy(Movie::mediaType)
            operator fun get(mediaType: String) = checkNotNull(mediaTypes[mediaType]) {
                "$INVALID_MEDIA_TYPE_MESSAGE $mediaType"
            }
        }
    }

    enum class TvShow(val mediaType: String) : NetworkMediaType {
        TOP_RATED(TOP_RATED_MEDIA_TYPE),
        POPULAR(POPULAR_MEDIA_TYPE),
        NOW_PLAYING(NOW_PLAYING_MEDIA_TYPE),
        DISCOVER(DISCOVER_MEDIA_TYPE),
        TRENDING(TRENDING_MEDIA_TYPE);

        companion object {
            private val mediaTypes = values().associateBy(TvShow::mediaType)
            operator fun get(mediaType: String) = checkNotNull(mediaTypes[mediaType]) {
                "$INVALID_MEDIA_TYPE_MESSAGE $mediaType"
            }
        }
    }
}

private const val UPCOMING_MEDIA_TYPE = "upcoming"
private const val TOP_RATED_MEDIA_TYPE = "top_rated"
private const val POPULAR_MEDIA_TYPE = "popular"
private const val NOW_PLAYING_MEDIA_TYPE = "now_playing"
private const val DISCOVER_MEDIA_TYPE = "discover"
private const val TRENDING_MEDIA_TYPE = "trending"

private const val INVALID_MEDIA_TYPE_MESSAGE = "Invalid media type."
