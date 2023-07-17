

package com.zobaer53.zedmovies.data.network.model.tvshow

import com.zobaer53.zedmovies.data.network.serializer.LocalDateSerializer
import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTvShow(
    @SerialName(Constants.Fields.ID)
    val id: Int,

    @SerialName(Constants.Fields.NAME)
    val name: String,

    @SerialName(Constants.Fields.OVERVIEW)
    val overview: String,

    @SerialName(Constants.Fields.POPULARITY)
    val popularity: Double,

    @Serializable(LocalDateSerializer::class)
    @SerialName(Constants.Fields.FIRST_AIR_DATE)
    val firstAirDate: LocalDate?,

    @SerialName(Constants.Fields.GENRE_IDS)
    val genreIds: List<Int>,

    @SerialName(Constants.Fields.ORIGINAL_NAME)
    val originalName: String,

    @SerialName(Constants.Fields.ORIGINAL_LANGUAGE)
    val originalLanguage: String,

    @SerialName(Constants.Fields.ORIGIN_COUNTRY)
    val originCountry: List<String>,

    @SerialName(Constants.Fields.VOTE_AVERAGE)
    val voteAverage: Double,

    @SerialName(Constants.Fields.VOTE_COUNT)
    val voteCount: Int,

    @SerialName(Constants.Fields.POSTER_PATH)
    val posterPath: String?,

    @SerialName(Constants.Fields.BACKDROP_PATH)
    val backdropPath: String?
)
