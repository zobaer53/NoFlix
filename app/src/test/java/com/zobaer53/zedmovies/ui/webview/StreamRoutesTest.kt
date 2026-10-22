package com.zobaer53.zedmovies.ui.webview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamRoutesTest {
    @Test
    fun `episode pages are parsed`() {
        assertEquals(StreamEpisode(1396, 2, 3), parseStreamEpisode("$STREAM_BASE_URL/watch/1396/2/3"))
        assertEquals(StreamEpisode(1396, 2, 3), parseStreamEpisode("$STREAM_BASE_URL/watch/1396/2/3/"))
        assertEquals(StreamEpisode(1396, 2, 3), parseStreamEpisode("$STREAM_BASE_URL/watch/1396/2/3?t=1#x"))
        assertEquals(StreamEpisode(1396, 0, 1), parseStreamEpisode("$STREAM_BASE_URL/watch/1396/0/1"))
        assertEquals(StreamEpisode(1396, 2, 3), parseStreamEpisode("$STREAM_BASE_URL/embed/1396/2/3"))
        assertEquals(StreamEpisode(1396, 2, 3), parseStreamEpisode("$STREAM_BASE_URL/embed/1396/2/3/"))
        assertEquals(StreamEpisode(1396, 2, 3), parseStreamEpisode("$STREAM_BASE_URL/embed/tv/1396/2/3"))
        assertEquals(StreamEpisode(1396, 2, 3), parseStreamEpisode("$STREAM_BASE_URL/embed/1396/2/3?t=120#x"))
        assertEquals(StreamEpisode(1, 1, 1), parseStreamEpisode("https://www.framextv.xyz/watch/1/1/1"))
        assertEquals(StreamEpisode(1, 1, 1), parseStreamEpisode("https://framextv.xyz/watch/1/1/1"))
        assertEquals(StreamEpisode(1, 1, 1), parseStreamEpisode("https://framextv.cc/watch/1/1/1"))
        assertEquals(StreamEpisode(1, 1, 1), parseStreamEpisode("https://www.framextv.cc/watch/1/1/1"))
        assertEquals(StreamEpisode(1, 1, 1), parseStreamEpisode("https://framextv.com/watch/1/1/1"))
        assertEquals(StreamEpisode(1, 1, 1), parseStreamEpisode("https://www.framextv.com/watch/1/1/1"))
        assertEquals(StreamEpisode(1, 1, 1), parseStreamEpisode("https://framextv.cc/embed/1/1/1"))
        assertEquals(StreamEpisode(1, 1, 1), parseStreamEpisode("https://framextv.xyz/embed/1/1/1"))
    }

    @Test
    fun `other pages are not episodes`() {
        assertNull(parseStreamEpisode(null))
        assertNull(parseStreamEpisode(""))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/watch/1396"))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/embed/1396"))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/embed/movie/1396"))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/tv/1396"))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/watch/1396/2/3/4"))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/embed/1396/2/3/4"))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/watch/1396/2/0"))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/watch/0/2/3"))
        assertNull(parseStreamEpisode("$STREAM_BASE_URL/watch/99999999999/2/3"))
        assertNull(parseStreamEpisode("https://example.com/watch/1396/2/3"))
        assertNull(parseStreamEpisode("https://example.com/embed/1396/2/3"))
        assertNull(parseStreamEpisode("https://notframextv.xyz/watch/1396/2/3"))
        assertNull(parseStreamEpisode("https://notframextv.cc/watch/1396/2/3"))
        assertNull(parseStreamEpisode("https://fake-framextv.cc/watch/1396/2/3"))
        assertNull(parseStreamEpisode("about:blank"))
    }

    @Test
    fun `movies always open the movie player`() {
        assertEquals("$STREAM_BASE_URL/embed/550", buildStreamUrl(550, "movie"))
        assertEquals("$STREAM_BASE_URL/embed/550", buildStreamUrl(550, "", StreamEpisode(550, 1, 1)))
    }

    @Test
    fun `tv shows open the show page or the resumed episode`() {
        assertEquals("$STREAM_BASE_URL/tv/1396", buildStreamUrl(1396, "tv"))
        assertEquals("$STREAM_BASE_URL/tv/1396", buildStreamUrl(1396, " TV "))
        assertEquals(
            "$STREAM_BASE_URL/embed/1396/2/3",
            buildStreamUrl(1396, "tv", StreamEpisode(1396, 2, 3))
        )
        assertEquals(
            "$STREAM_BASE_URL/embed/1396/2/3?t=450",
            buildStreamUrl(1396, "tv", StreamEpisode(1396, 2, 3, positionSeconds = 450))
        )
        assertEquals(
            "$STREAM_BASE_URL/embed/1396/2/3",
            buildStreamUrl(1396, "tv", StreamEpisode(1396, 2, 3, positionSeconds = 15))
        )
        assertEquals("$STREAM_BASE_URL/tv/1396?season=2", buildStreamShowUrl(1396, 2))
    }

    @Test
    fun `show page check ignores the selected season`() {
        assertTrue(isStreamShowUrl("$STREAM_BASE_URL/tv/1396", 1396))
        assertTrue(isStreamShowUrl("$STREAM_BASE_URL/tv/1396/?season=2", 1396))
        assertFalse(isStreamShowUrl("$STREAM_BASE_URL/tv/13960", 1396))
        assertFalse(isStreamShowUrl("$STREAM_BASE_URL/watch/1396/2/3", 1396))
        assertFalse(isStreamShowUrl("$STREAM_BASE_URL/embed/1396/2/3", 1396))
        assertFalse(isStreamShowUrl(null, 1396))
    }

    @Test
    fun `watch url check identifies player routes`() {
        assertTrue(isStreamWatchUrl("$STREAM_BASE_URL/embed/550"))
        assertTrue(isStreamWatchUrl("$STREAM_BASE_URL/embed/1396/2/3"))
        assertTrue(isStreamWatchUrl("$STREAM_BASE_URL/embed/tv/1396/2/3"))
        assertTrue(isStreamWatchUrl("$STREAM_BASE_URL/watch/550"))
        assertTrue(isStreamWatchUrl("$STREAM_BASE_URL/watch/1396/2/3"))
        assertFalse(isStreamWatchUrl("$STREAM_BASE_URL/tv/1396"))
        assertFalse(isStreamWatchUrl("$STREAM_BASE_URL/tv/1396?season=2"))
        assertFalse(isStreamWatchUrl("https://example.com/embed/550"))
        assertFalse(isStreamWatchUrl(null))
        assertFalse(isStreamWatchUrl(""))
    }
}
