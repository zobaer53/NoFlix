package com.zobaer53.zedmovies.ui.webview

import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamWatchProgressTest {
    @Test
    fun `tv entries are read as the site saves them`() {
        val json = """
            [{"mediaType":"tv","id":"1396","season":2,"episode":3,"timestamp":21.14,
              "duration":2830.95,"title":"Breaking Bad","poster":"https://image.tmdb.org/x.jpg",
              "completed":false,"updatedAt":1792295851396},
             {"mediaType":"movie","id":"550","timestamp":26,"duration":6000,"updatedAt":1792295851000},
             {"mediaType":"anime","id":"20","episode":4,"timestamp":1,"duration":2,"updatedAt":1}]
        """
        assertEquals(
            listOf(
                LastWatchedEpisodeModel(
                    tvShowId = 1396, season = 2, episode = 3,
                    positionSeconds = 21.14, durationSeconds = 2830.95,
                    completed = false, updatedAt = 1792295851396
                )
            ),
            parseStreamWatchProgress(json)
        )
    }

    @Test
    fun `numeric ids and missing progress are accepted`() {
        val json = """[{"mediaType":"tv","id":1396,"season":1,"episode":7,"completed":true,"updatedAt":5}]"""
        assertEquals(
            listOf(LastWatchedEpisodeModel(1396, 1, 7, completed = true, updatedAt = 5)),
            parseStreamWatchProgress(json)
        )
    }

    @Test
    fun `broken entries are skipped`() {
        val json = """[
            null, 1, "x", [],
            {"mediaType":"tv","id":"abc","season":1,"episode":1,"updatedAt":1},
            {"mediaType":"tv","id":"1","season":1.5,"episode":1,"updatedAt":1},
            {"mediaType":"tv","id":"1","season":1,"episode":0,"updatedAt":1},
            {"mediaType":"tv","id":"1","season":1,"episode":1},
            {"mediaType":"tv","id":"1","season":"1","episode":"2","updatedAt":"3"},
            {"mediaType":"tv","id":"2","season":1,"episode":1,"timestamp":"NaN","duration":-1,"updatedAt":9}
        ]"""
        assertEquals(
            listOf(
                LastWatchedEpisodeModel(1, 1, 2, updatedAt = 3),
                LastWatchedEpisodeModel(2, 1, 1, updatedAt = 9)
            ),
            parseStreamWatchProgress(json)
        )
    }

    @Test
    fun `anything that is not an array is empty`() {
        listOf(null, "", "null", "{}", "{\"a\":1}", "not json", "[").forEach {
            assertTrue(it.toString(), parseStreamWatchProgress(it).isEmpty())
        }
    }

    @Test
    fun `playback active signal is parsed correctly`() {
        val json = """{"type":"playback_active","videoWidth":1920,"videoHeight":1080,"renderedWidth":853,"renderedHeight":384,"currentTime":5.2}"""
        val signal = parsePlaybackActiveSignal(json)
        assertEquals(
            PlaybackActiveSignal(
                videoWidth = 1920,
                videoHeight = 1080,
                renderedWidth = 853,
                renderedHeight = 384,
                currentTime = 5.2
            ),
            signal
        )
    }

    @Test
    fun `invalid playback active signal returns null`() {
        listOf(
            null,
            "",
            "[]",
            """{"type":"other"}""",
            """{"type":"playback_active"}"""
        ).forEach {
            if (it == null || !it.contains("playback_active")) {
                assertEquals(null, parsePlaybackActiveSignal(it))
            }
        }
    }
}
