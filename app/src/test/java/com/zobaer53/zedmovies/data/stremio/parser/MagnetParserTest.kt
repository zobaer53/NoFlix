package com.zobaer53.zedmovies.data.stremio.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MagnetParserTest {

    @Test
    fun extractInfoHash_from_magnet_link() {
        val magnet = "magnet:?xt=urn:btih:c3da9a3dc2ce14d0d4fc0e87d1b2023502f8dcd6&dn=Movie"
        val hash = MagnetParser.extractInfoHash(magnet)
        assertEquals("c3da9a3dc2ce14d0d4fc0e87d1b2023502f8dcd6", hash)
    }

    @Test
    fun extractInfoHash_from_raw_40_char_hex() {
        val raw = "C3DA9A3DC2CE14D0D4FC0E87D1B2023502F8DCD6"
        val hash = MagnetParser.extractInfoHash(raw)
        assertEquals("c3da9a3dc2ce14d0d4fc0e87d1b2023502f8dcd6", hash)
    }

    @Test
    fun extractTrackers_extracts_and_decodes_trackers() {
        val magnet = "magnet:?xt=urn:btih:c3da9a3dc2ce14d0d4fc0e87d1b2023502f8dcd6&tr=http%3A%2F%2Ftracker.example.com%2Fannounce"
        val trackers = MagnetParser.extractTrackers(magnet)
        assertEquals(1, trackers.size)
        assertEquals("tracker:http://tracker.example.com/announce", trackers[0])
    }

    @Test
    fun buildMagnetUri_constructs_valid_magnet_string() {
        val uri = MagnetParser.buildMagnetUri(
            infoHash = "c3da9a3dc2ce14d0d4fc0e87d1b2023502f8dcd6",
            name = "TestMovie",
            trackers = listOf("tracker:http://tracker.example.com/announce")
        )
        assertTrue(uri.startsWith("magnet:?xt=urn:btih:c3da9a3dc2ce14d0d4fc0e87d1b2023502f8dcd6"))
        assertTrue(uri.contains("&dn=TestMovie"))
        assertTrue(uri.contains("&tr=http://tracker.example.com/announce"))
    }
}

