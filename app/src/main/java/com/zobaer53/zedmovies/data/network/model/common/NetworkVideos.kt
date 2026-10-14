package com.zobaer53.zedmovies.data.network.model.common

import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkVideos(
    @SerialName(Constants.Fields.RESULTS)
    val results: List<NetworkVideo>
)

@Serializable
data class NetworkVideo(
    @SerialName(Constants.Fields.ISO_639_1)
    val language: String? = null,

    @SerialName(Constants.Fields.KEY)
    val key: String,

    @SerialName(Constants.Fields.SITE)
    val site: String,

    @SerialName(Constants.Fields.TYPE)
    val type: String,

    @SerialName(Constants.Fields.OFFICIAL)
    val official: Boolean = false
)
