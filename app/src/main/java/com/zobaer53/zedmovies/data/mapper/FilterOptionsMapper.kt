package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.network.model.common.NetworkCertification
import com.zobaer53.zedmovies.data.network.model.common.NetworkCertificationsResponse
import com.zobaer53.zedmovies.data.network.model.common.NetworkWatchProvider
import com.zobaer53.zedmovies.data.network.util.Constants
import com.zobaer53.zedmovies.domain.model.CertificationModel
import com.zobaer53.zedmovies.domain.model.CertificationsModel
import com.zobaer53.zedmovies.domain.model.WatchProviderModel
import com.zobaer53.zedmovies.domain.model.WatchProvidersModel

/** The [region]'s ratings, mildest first, or [fallbackRegion]'s when TMDB has none for it. */
internal fun NetworkCertificationsResponse.asCertificationsModel(
    region: String,
    fallbackRegion: String
): CertificationsModel {
    val country = region.takeIf { certifications[it].orEmpty().isNotEmpty() } ?: fallbackRegion
    return CertificationsModel(
        country = country,
        certifications = certifications[country].orEmpty()
            .sortedBy(NetworkCertification::order)
            .map { CertificationModel(code = it.certification, meaning = it.meaning) }
    )
}

/** Duplicates (a service listed for movies and TV) are merged, most popular first. */
internal fun List<NetworkWatchProvider>.asWatchProvidersModel(region: String) = WatchProvidersModel(
    region = region,
    providers = groupBy(NetworkWatchProvider::id)
        .map { (_, same) -> same.minBy(NetworkWatchProvider::displayPriority) }
        .sortedBy(NetworkWatchProvider::displayPriority)
        .map {
            WatchProviderModel(
                id = it.id,
                name = it.name,
                logoPath = it.logoPath?.let { path -> Constants.LOGO_URL + path }
            )
        }
)
