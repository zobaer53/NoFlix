package com.zobaer53.zedmovies.data.stremio.parser

import java.net.URLDecoder
import java.util.Locale

object MagnetParser {
    private val BTIH_REGEX = Regex("btih:([a-f0-9]{40})", RegexOption.IGNORE_CASE)
    private val RAW_HASH_REGEX = Regex("^[a-f0-9]{40}$", RegexOption.IGNORE_CASE)
    private val TRACKER_REGEX = Regex("[?&]tr=([^&]+)")

    fun extractInfoHash(link: String): String? {
        val trimmed = link.trim()
        val btihMatch = BTIH_REGEX.find(trimmed)
        if (btihMatch != null) {
            return btihMatch.groupValues[1].lowercase(Locale.ROOT)
        }
        if (RAW_HASH_REGEX.matches(trimmed)) {
            return trimmed.lowercase(Locale.ROOT)
        }
        return null
    }

    fun extractTrackers(link: String): List<String> {
        val trackers = mutableListOf<String>()
        val matches = TRACKER_REGEX.findAll(link)
        for (m in matches) {
            val encoded = m.groupValues[1]
            try {
                val decoded = URLDecoder.decode(encoded, "UTF-8")
                trackers.add("tracker:$decoded")
            } catch (_: Exception) {
                trackers.add("tracker:$encoded")
            }
        }
        return trackers
    }

    fun buildMagnetUri(infoHash: String, name: String? = null, trackers: List<String> = emptyList()): String {
        val sb = StringBuilder("magnet:?xt=urn:btih:").append(infoHash)
        if (!name.isNullOrBlank()) {
            sb.append("&dn=").append(name)
        }
        for (tr in trackers) {
            val rawUrl = tr.removePrefix("tracker:")
            sb.append("&tr=").append(rawUrl)
        }
        return sb.toString()
    }
}

