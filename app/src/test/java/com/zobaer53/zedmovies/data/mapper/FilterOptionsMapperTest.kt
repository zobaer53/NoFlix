package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.network.model.common.NetworkCertification
import com.zobaer53.zedmovies.data.network.model.common.NetworkCertificationsResponse
import com.zobaer53.zedmovies.data.network.model.common.NetworkWatchProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FilterOptionsMapperTest {
    private val certifications = NetworkCertificationsResponse(
        mapOf(
            "US" to listOf(
                NetworkCertification("R", "", order = 4),
                NetworkCertification("G", "", order = 1),
                NetworkCertification("PG-13", "", order = 3)
            ),
            "GB" to listOf(NetworkCertification("15", "", order = 4)),
            "XX" to emptyList()
        )
    )

    @Test
    fun `certifications of the region, mildest first`() {
        val model = certifications.asCertificationsModel(region = "US", fallbackRegion = "US")
        assertEquals("US", model.country)
        assertEquals(listOf("G", "PG-13", "R"), model.certifications.map { it.code })
        assertEquals("GB", certifications.asCertificationsModel("GB", "US").country)
    }

    @Test
    fun `regions without certifications fall back`() {
        assertEquals("US", certifications.asCertificationsModel("BD", "US").country)
        assertEquals("US", certifications.asCertificationsModel("XX", "US").country)
    }

    @Test
    fun `providers are merged by id and sorted by priority`() {
        val providers = listOf(
            NetworkWatchProvider(9, "Prime Video", "/prime.png", displayPriority = 5),
            NetworkWatchProvider(8, "Netflix", "/netflix.png", displayPriority = 3),
            NetworkWatchProvider(8, "Netflix", "/netflix.png", displayPriority = 1),
            NetworkWatchProvider(337, "Disney Plus", null, displayPriority = 2)
        ).asWatchProvidersModel("US")
        assertEquals("US", providers.region)
        assertEquals(listOf(8, 337, 9), providers.providers.map { it.id })
        assertEquals("https://image.tmdb.org/t/p/w92/netflix.png", providers.providers.first().logoPath)
        assertNull(providers.providers[1].logoPath)
    }
}
