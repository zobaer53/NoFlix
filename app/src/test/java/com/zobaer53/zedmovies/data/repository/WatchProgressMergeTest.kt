package com.zobaer53.zedmovies.data.repository

import com.zobaer53.zedmovies.domain.model.LastWatchedEpisodeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WatchProgressMergeTest {
    private fun episode(
        season: Int,
        episode: Int,
        updatedAt: Long,
        position: Double? = null,
        completed: Boolean = false
    ) = LastWatchedEpisodeModel(
        tvShowId = 1396,
        season = season,
        episode = episode,
        positionSeconds = position,
        durationSeconds = position?.let { 2800.0 },
        completed = completed,
        updatedAt = updatedAt
    )

    @Test
    fun `opening an episode makes it the last watched one`() {
        assertEquals(episode(2, 3, 10), null.withOpenedEpisode(1396, 2, 3, now = 10))
        assertEquals(
            episode(2, 4, 20),
            episode(2, 3, 10, position = 500.0).withOpenedEpisode(1396, 2, 4, now = 20)
        )
    }

    @Test
    fun `reopening the same episode keeps its position`() {
        assertEquals(
            episode(2, 3, 20, position = 500.0),
            episode(2, 3, 10, position = 500.0).withOpenedEpisode(1396, 2, 3, now = 20)
        )
    }

    @Test
    fun `site progress for the stored episode updates its position`() {
        assertEquals(
            episode(2, 3, 20, position = 900.0),
            episode(2, 3, 20).withSiteProgress(episode(2, 3, 15, position = 900.0))
        )
        assertEquals(
            episode(2, 3, 30, position = 900.0),
            episode(2, 3, 20).withSiteProgress(episode(2, 3, 30, position = 900.0))
        )
    }

    @Test
    fun `newer site progress for another episode replaces it`() {
        assertEquals(
            episode(3, 1, 30, position = 60.0),
            episode(2, 3, 20).withSiteProgress(episode(3, 1, 30, position = 60.0))
        )
        assertEquals(
            episode(2, 3, 20),
            episode(2, 3, 20).withSiteProgress(episode(1, 1, 10, position = 60.0))
        )
    }

    @Test
    fun `a finished episode saved after auto-advance does not move the user back`() {
        // The player auto-advanced to S2E4 (recorded from the URL), then the site saved S2E3's end.
        val advanced = episode(2, 4, 20)
        assertEquals(
            advanced,
            advanced.withSiteProgress(episode(2, 3, 21, position = 2790.0, completed = true))
        )
    }

    @Test
    fun `a newer finished episode replaces a finished one`() {
        assertEquals(
            episode(1, 2, 30, position = 2790.0, completed = true),
            episode(1, 1, 20, position = 2790.0, completed = true)
                .withSiteProgress(episode(1, 2, 30, position = 2790.0, completed = true))
        )
    }

    @Test
    fun `site history seeds a show with no record, newest entry winning`() {
        // The site keeps its list newest first.
        val site = listOf(
            episode(1, 3, 30, position = 100.0),
            episode(1, 2, 20, position = 2790.0, completed = true),
            episode(1, 1, 10, position = 2790.0, completed = true)
        )
        assertEquals(episode(1, 3, 30, position = 100.0), null.withSiteProgress(site))
        assertEquals(
            episode(1, 2, 20, position = 2790.0, completed = true),
            null.withSiteProgress(site.drop(1))
        )
        assertNull(null.withSiteProgress(emptyList()))
    }

    @Test
    fun `site history applies position updates after an auto-advance`() {
        val site = listOf(
            episode(2, 4, 25, position = 30.0),
            episode(2, 3, 22, position = 2790.0, completed = true)
        )
        assertEquals(episode(2, 4, 25, position = 30.0), episode(2, 4, 20).withSiteProgress(site))
    }
}
