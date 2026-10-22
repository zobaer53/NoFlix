package com.zobaer53.zedmovies.data.stremio.parser

import java.util.Locale
import kotlin.math.roundToLong

object SizeParser {
    private val SIZE_REGEX = Regex("([\\d.]+)\\s*(GB|MB|GiB|MiB)\\b", RegexOption.IGNORE_CASE)

    fun parse(text: String?, videoSizeHint: Long? = null): Long? {
        if (videoSizeHint != null && videoSizeHint > 0) return videoSizeHint
        if (text.isNullOrBlank()) return null

        val match = SIZE_REGEX.find(text) ?: return null
        val numberPart = match.groupValues[1].toDoubleOrNull() ?: return null
        val unitPart = match.groupValues[2].uppercase(Locale.ROOT)

        val multiplier = if (unitPart.startsWith("G")) {
            1024L * 1024L * 1024L
        } else {
            1024L * 1024L
        }

        return (numberPart * multiplier).roundToLong()
    }

    fun format(bytes: Long?): String {
        if (bytes == null || bytes <= 0) return ""
        val gb = 1024.0 * 1024.0 * 1024.0
        val mb = 1024.0 * 1024.0
        val kb = 1024.0

        return when {
            bytes >= gb -> String.format(Locale.US, "%.2f GB", bytes / gb)
            bytes >= mb -> String.format(Locale.US, "%.1f MB", bytes / mb)
            bytes >= kb -> String.format(Locale.US, "%.0f KB", bytes / kb)
            else -> "$bytes B"
        }
    }

    fun formatSpeed(bytesPerSec: Long): String {
        if (bytesPerSec <= 0) return "0 KB/s"
        return "${format(bytesPerSec)}/s"
    }
}

