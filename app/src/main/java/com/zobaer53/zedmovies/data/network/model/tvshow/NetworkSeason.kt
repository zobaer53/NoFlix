package com.zobaer53.zedmovies.data.network.model.tvshow

import com.zobaer53.zedmovies.data.network.serializer.LocalDateSerializer
import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSeason(
    @SerialName(Constants.Fields.ID)
    val id: Int,

    @SerialName(Constants.Fields.NAME)
    val name: String,

    @SerialName(Constants.Fields.OVERVIEW)
    val overview: String,

    @Serializable(LocalDateSerializer::class)
    @SerialName(Constants.Fields.AIR_DATE)
    val airDate: LocalDate?,

    @SerialName(Constants.Fields.EPISODE_COUNT)
    val episodeCount: Int,

    @SerialName(Constants.Fields.SEASON_NUMBER)
    val seasonNumber: Int,

    @SerialName(Constants.Fields.POSTER_PATH)
    val posterPath: String?
)
