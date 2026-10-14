package com.zobaer53.zedmovies.data.network.model.common

import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCertificationsResponse(
    /** Keyed by ISO 3166-1 country. */
    @SerialName(Constants.Fields.CERTIFICATIONS)
    val certifications: Map<String, List<NetworkCertification>>
)

@Serializable
data class NetworkCertification(
    @SerialName(Constants.Fields.CERTIFICATION)
    val certification: String,

    @SerialName(Constants.Fields.MEANING)
    val meaning: String,

    @SerialName(Constants.Fields.ORDER)
    val order: Int
)

@Serializable
data class NetworkWatchProvidersResponse(
    @SerialName(Constants.Fields.RESULTS)
    val results: List<NetworkWatchProvider>
)

@Serializable
data class NetworkWatchProvider(
    @SerialName(Constants.Fields.PROVIDER_ID)
    val id: Int,

    @SerialName(Constants.Fields.PROVIDER_NAME)
    val name: String,

    @SerialName(Constants.Fields.LOGO_PATH)
    val logoPath: String? = null,

    @SerialName(Constants.Fields.DISPLAY_PRIORITY)
    val displayPriority: Int = Int.MAX_VALUE
)
