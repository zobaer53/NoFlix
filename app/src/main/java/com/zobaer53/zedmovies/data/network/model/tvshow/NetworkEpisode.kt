
package com.zobaer53.zedmovies.data.network.model.tvshow

import com.zobaer53.zedmovies.core.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkEpisode(
    @SerialName(Constants.Fields.ID)
    val id: Int,

    @SerialName(Constants.Fields.NAME)
    val name: String,

    @SerialName(Constants.Fields.AIR_DATE)
    val airDate: String,

    @SerialName(Constants.Fields.EPISODE_NUMBER)
    val episodeNumber: Int,

    @SerialName(Constants.Fields.OVERVIEW)
    val overview: String,

    @SerialName(Constants.Fields.PRODUCTION_CODE)
    val productionCode: String,

    @SerialName(Constants.Fields.RUNTIME)
    val runtime: Int?,

    @SerialName(Constants.Fields.SEASON_NUMBER)
    val seasonNumber: Int,

    @SerialName(Constants.Fields.SHOW_ID)
    val showId: Int,

    @SerialName(Constants.Fields.STILL_PATH)
    val stillPath: String?,

    @SerialName(Constants.Fields.VOTE_AVERAGE)
    val voteAverage: Double,

    @SerialName(Constants.Fields.VOTE_COUNT)
    val voteCount: Int
)
