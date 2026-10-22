package com.zobaer53.zedmovies.data.stremio.parser

import org.junit.Assert.assertEquals
import org.junit.Test

class HlsParserTest {

    @Test
    fun parseVariants_extracts_bandwidth_and_resolution() {
        val m3u8 = """
            #EXTM3U
            #EXT-X-STREAM-INF:BANDWIDTH=5000000,RESOLUTION=1920x1080
            1080p/index.m3u8
            #EXT-X-STREAM-INF:BANDWIDTH=2500000,RESOLUTION=1280x720
            720p/index.m3u8
            #EXT-X-STREAM-INF:BANDWIDTH=800000,RESOLUTION=854x480
            480p/index.m3u8
        """.trimIndent()

        val baseUrl = "https://example.com/hls/master.m3u8"
        val variants = HlsParser.parseVariants(m3u8, baseUrl)

        assertEquals(3, variants.size)
        assertEquals("1080p", variants[0].quality)
        assertEquals(5000000L, variants[0].bandwidth)
        assertEquals("https://example.com/hls/1080p/index.m3u8", variants[0].url)

        assertEquals("720p", variants[1].quality)
        assertEquals(2500000L, variants[1].bandwidth)
        assertEquals("https://example.com/hls/720p/index.m3u8", variants[1].url)

        assertEquals("480p", variants[2].quality)
        assertEquals(800000L, variants[2].bandwidth)
        assertEquals("https://example.com/hls/480p/index.m3u8", variants[2].url)
    }

    @Test
    fun parseVariants_fallback_when_media_playlist() {
        val m3u8 = """
            #EXTM3U
            #EXT-X-TARGETDURATION:10
            #EXTINF:10,
            seg1.ts
        """.trimIndent()

        val baseUrl = "https://example.com/stream.m3u8"
        val variants = HlsParser.parseVariants(m3u8, baseUrl)

        assertEquals(1, variants.size)
        assertEquals("Original", variants[0].quality)
        assertEquals(baseUrl, variants[0].url)
    }
}

