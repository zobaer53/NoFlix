package com.zobaer53.zedmovies.domain.model

/** Filter choices that come from TMDB rather than from the app. */
data class FilterOptionsModel(
    val certifications: CertificationsModel,
    val watchProviders: WatchProvidersModel
)

/** Age ratings of [country], from mildest to strictest. */
data class CertificationsModel(
    val country: String,
    val certifications: List<CertificationModel>
)

data class CertificationModel(
    val code: String,
    val meaning: String
)

/** Streaming services in [region], most popular first. */
data class WatchProvidersModel(
    val region: String,
    val providers: List<WatchProviderModel>
)

data class WatchProviderModel(
    val id: Int,
    val name: String,
    val logoPath: String?
)
