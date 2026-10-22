package com.zobaer53.zedmovies.data.stremio.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SizeParserTest {

    @Test
    fun parse_extracts_gigabytes() {
        val bytes = SizeParser.parse("👤 150 💾 2.5 GB ⚙️ RARBG")
        val expected = (2.5 * 1024 * 1024 * 1024).toLong()
        assertEquals(expected, bytes)
    }

    @Test
    fun parse_extracts_megabytes() {
        val bytes = SizeParser.parse("Video 450 MB HDTV")
        val expected = (450 * 1024 * 1024).toLong()
        assertEquals(expected, bytes)
    }

    @Test
    fun parse_prefers_videoSize_hint() {
        val bytes = SizeParser.parse("Text says 1 GB", videoSizeHint = 5000000L)
        assertEquals(5000000L, bytes)
    }

    @Test
    fun parse_returns_null_when_no_size() {
        assertNull(SizeParser.parse("Just a movie title"))
    }

    @Test
    fun format_formats_correctly() {
        assertEquals("2.00 GB", SizeParser.format(2L * 1024 * 1024 * 1024))
        assertEquals("500.0 MB", SizeParser.format(500L * 1024 * 1024))
        assertEquals("0 KB/s", SizeParser.formatSpeed(0))
        assertEquals("2.00 GB/s", SizeParser.formatSpeed(2L * 1024 * 1024 * 1024))
    }
}

