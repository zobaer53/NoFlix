package com.zobaer53.zedmovies.data.stremio.parser

import com.zobaer53.zedmovies.data.stremio.model.HlsVariant
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object HlsParser {
    private val BANDWIDTH_REGEX = Regex("BANDWIDTH=(\\d+)")
    private val RESOLUTION_REGEX = Regex("RESOLUTION=(\\d+x\\d+)")

    fun parseVariants(m3u8Content: String, baseUrl: String): List<HlsVariant> {
        val lines = m3u8Content.lines().map { it.trim() }
        val variants = mutableListOf<HlsVariant>()

        for (i in lines.indices) {
            val line = lines[i]
            if (line.startsWith("#EXT-X-STREAM-INF:")) {
                val attrs = line.substringAfter("#EXT-X-STREAM-INF:")
                val bandwidth = BANDWIDTH_REGEX.find(attrs)?.groupValues?.get(1)?.toLongOrNull()
                val resolution = RESOLUTION_REGEX.find(attrs)?.groupValues?.get(1)

                // The next non-empty, non-comment line is the URI for this variant
                val uri = lines.drop(i + 1).firstOrNull { it.isNotEmpty() && !it.startsWith("#") }
                if (!uri.isNullOrBlank()) {
                    val resolvedUrl = resolveUrl(baseUrl, uri)
                    val quality = when {
                        resolution != null && resolution.contains("x") -> {
                            val height = resolution.substringAfter("x")
                            "${height}p"
                        }
                        bandwidth != null -> {
                            val kbps = bandwidth / 1000
                            "${kbps} kbps"
                        }
                        else -> "Unknown"
                    }

                    variants.add(
                        HlsVariant(
                            url = resolvedUrl,
                            bandwidth = bandwidth,
                            resolution = resolution,
                            quality = quality
                        )
                    )
                }
            }
        }

        // Sort descending by bandwidth
        variants.sortByDescending { it.bandwidth ?: 0L }

        return if (variants.isNotEmpty()) {
            variants
        } else {
            listOf(
                HlsVariant(
                    url = baseUrl,
                    bandwidth = null,
                    resolution = null,
                    quality = "Original"
                )
            )
        }
    }

    private fun resolveUrl(baseUrl: String, relativeOrAbsolute: String): String {
        val baseHttpUrl = baseUrl.toHttpUrlOrNull() ?: return relativeOrAbsolute
        return baseHttpUrl.resolve(relativeOrAbsolute)?.toString() ?: relativeOrAbsolute
    }
}

