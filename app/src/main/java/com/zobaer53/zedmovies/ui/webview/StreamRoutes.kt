package com.zobaer53.zedmovies.ui.webview

const val STREAM_BASE_URL = "https://framextv.xyz"

/** A TV episode on the stream site, at `/watch/<tmdbId>/<season>/<episode>`. */
data class StreamEpisode(val tvShowId: Int, val season: Int, val episode: Int)

// The streaming site is a client-rendered SPA, so its pages can't be scraped with Jsoup.
// Its routes are keyed by TMDB id, so the watch URL is built directly instead. A TV show opens
// at [resume] when there is one, otherwise on its show page. `/watch/<id>` alone is always a
// movie: the site reads a TV id there as a movie id.
fun buildStreamUrl(tmdbId: Int, type: String, resume: StreamEpisode? = null): String = when {
    type.trim().lowercase() != "tv" -> "$STREAM_BASE_URL/watch/$tmdbId"
    resume != null -> buildStreamEpisodeUrl(resume)
    else -> buildStreamShowUrl(tmdbId)
}

fun buildStreamEpisodeUrl(episode: StreamEpisode): String =
    "$STREAM_BASE_URL/watch/${episode.tvShowId}/${episode.season}/${episode.episode}"

/** The show page, with [season]'s episode list selected when given. */
fun buildStreamShowUrl(tvShowId: Int, season: Int? = null): String =
    "$STREAM_BASE_URL/tv/$tvShowId" + if (season != null) "?season=$season" else ""

/** The episode a stream-site URL plays, or null for any other page. */
fun parseStreamEpisode(url: String?): StreamEpisode? {
    val path = streamSitePath(url) ?: return null
    val (id, season, episode) = EpisodePath.matchEntire(path)?.destructured ?: return null
    return StreamEpisode(
        tvShowId = id.toIntOrNull()?.takeIf { it > 0 } ?: return null,
        season = season.toIntOrNull() ?: return null,
        episode = episode.toIntOrNull()?.takeIf { it > 0 } ?: return null
    )
}

/** Whether [url] is the show page of [tvShowId]. */
fun isStreamShowUrl(url: String?, tvShowId: Int): Boolean =
    streamSitePath(url)?.trimEnd('/') == "/tv/$tvShowId"

// Plain string parsing rather than android.net.Uri, so this runs in JVM unit tests.
private fun streamSitePath(url: String?): String? {
    val (host, path) = url?.let(UrlParts::matchEntire)?.destructured ?: return null
    val streamHost = STREAM_BASE_URL.substringAfter("://")
    val isStreamHost = host.lowercase().let { it == streamHost || it.endsWith(".$streamHost") }
    return if (isStreamHost) path.ifEmpty { "/" } else null
}

private val UrlParts = Regex("""https?://([^/?#:]+)(?::\d+)?([^?#]*)(?:[?#].*)?""")
private val EpisodePath = Regex("""/watch/(\d+)/(\d+)/(\d+)/?""")
