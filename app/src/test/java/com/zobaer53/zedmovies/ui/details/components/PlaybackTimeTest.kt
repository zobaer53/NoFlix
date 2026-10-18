package com.zobaer53.zedmovies.ui.details.components

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackTimeTest {
    @Test
    fun `playback time is formatted like the site's player`() {
        assertEquals("0:00", formatPlaybackTime(0.0))
        assertEquals("0:29", formatPlaybackTime(29.9))
        assertEquals("12:34", formatPlaybackTime(754.2))
        assertEquals("1:02:03", formatPlaybackTime(3723.0))
        assertEquals("0:00", formatPlaybackTime(-5.0))
    }
}
