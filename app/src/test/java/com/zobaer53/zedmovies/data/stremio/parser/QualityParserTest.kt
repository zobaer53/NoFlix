package com.zobaer53.zedmovies.data.stremio.parser

import com.zobaer53.zedmovies.data.stremio.model.StreamQuality
import org.junit.Assert.assertEquals
import org.junit.Test

class QualityParserTest {

    @Test
    fun parse_matches_4k_variations() {
        assertEquals(StreamQuality.Q_4K, QualityParser.parse("Movie.Name.2160p.BluRay"))
        assertEquals(StreamQuality.Q_4K, QualityParser.parse("Movie Name 4k HDR"))
        assertEquals(StreamQuality.Q_4K, QualityParser.parse("Movie Name UHD Remux"))
    }

    @Test
    fun parse_matches_1080p() {
        assertEquals(StreamQuality.Q_1080P, QualityParser.parse("Breaking.Bad.S01E01.1080p.WEB-DL"))
    }

    @Test
    fun parse_matches_720p() {
        assertEquals(StreamQuality.Q_720P, QualityParser.parse("Breaking.Bad.S01E01.720p.HDTV"))
    }

    @Test
    fun parse_matches_480p() {
        assertEquals(StreamQuality.Q_480P, QualityParser.parse("Old.Film.480p.DVD"))
    }

    @Test
    fun parse_returns_unknown_when_no_match() {
        assertEquals(StreamQuality.UNKNOWN, QualityParser.parse("Some.Title.Without.Quality.Label"))
        assertEquals(StreamQuality.UNKNOWN, QualityParser.parse(null))
        assertEquals(StreamQuality.UNKNOWN, QualityParser.parse(""))
    }
}

