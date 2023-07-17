
package com.zobaer53.zedmovies.data.network.model.response

import com.zobaer53.zedmovies.core.network.model.movie.NetworkMovie
import com.zobaer53.zedmovies.core.network.model.tvshow.NetworkTvShow
import com.zobaer53.zedmovies.core.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseDto(
    @SerialName(Constants.Fields.PAGE)
    val page: Int,

    @SerialName(Constants.Fields.RESULTS)
    val results: List<NetworkMovie>,

    @SerialName(Constants.Fields.TOTAL_PAGES)
    val totalPages: Int,

    @SerialName(Constants.Fields.TOTAL_RESULTS)
    val totalResults: Int,

    @SerialName(Constants.Fields.DATES)
    val dates: NetworkDates? = null
)

@Serializable
data class TvShowResponseDto(
    @SerialName(Constants.Fields.PAGE)
    val page: Int,

    @SerialName(Constants.Fields.RESULTS)
    val results: List<NetworkTvShow>,

    @SerialName(Constants.Fields.TOTAL_PAGES)
    val totalPages: Int,

    @SerialName(Constants.Fields.TOTAL_RESULTS)
    val totalResults: Int,

    @SerialName(Constants.Fields.DATES)
    val dates: NetworkDates? = null
)
