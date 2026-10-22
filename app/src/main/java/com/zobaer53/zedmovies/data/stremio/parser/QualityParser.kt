package com.zobaer53.zedmovies.data.stremio.parser

import com.zobaer53.zedmovies.data.stremio.model.StreamQuality

object QualityParser {
    private val QUALITY_PATTERNS = listOf(
        StreamQuality.Q_4K to Regex("\\b(2160p|4k|uhd)\\b", RegexOption.IGNORE_CASE),
        StreamQuality.Q_1440P to Regex("\\b1440p\\b", RegexOption.IGNORE_CASE),
        StreamQuality.Q_1080P to Regex("\\b1080p\\b", RegexOption.IGNORE_CASE),
        StreamQuality.Q_720P to Regex("\\b720p\\b", RegexOption.IGNORE_CASE),
        StreamQuality.Q_576P to Regex("\\b576p\\b", RegexOption.IGNORE_CASE),
        StreamQuality.Q_480P to Regex("\\b480p\\b", RegexOption.IGNORE_CASE),
        StreamQuality.Q_360P to Regex("\\b360p\\b", RegexOption.IGNORE_CASE)
    )

    fun parse(text: String?): StreamQuality {
        if (text.isNullOrBlank()) return StreamQuality.UNKNOWN
        for ((quality, pattern) in QUALITY_PATTERNS) {
            if (pattern.containsMatchIn(text)) {
                return quality
            }
        }
        return StreamQuality.UNKNOWN
    }
}

