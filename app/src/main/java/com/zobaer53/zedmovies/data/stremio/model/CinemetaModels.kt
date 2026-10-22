package com.zobaer53.zedmovies.data.stremio.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CinemetaCatalogResponse(
    val metas: List<CinemetaMetaSummary> = emptyList()
)

@Serializable
data class CinemetaMetaSummary(
    val id: String,
    val type: String,
    val name: String,
    val poster: String? = null,
    @SerialName("releaseInfo")
    val year: String? = null
)

@Serializable
data class CinemetaDetailsResponse(
    val meta: CinemetaMetaDetails
)

@Serializable
data class CinemetaMetaDetails(
    val id: String,
    val type: String,
    val name: String,
    val poster: String? = null,
    @SerialName("releaseInfo")
    val year: String? = null,
    val description: String? = null,
    val videos: List<CinemetaVideo> = emptyList()
)

@Serializable
data class CinemetaVideo(
    val id: String,
    val season: Int = 0,
    val episode: Int = 0,
    val name: String? = null,
    val title: String? = null
)

