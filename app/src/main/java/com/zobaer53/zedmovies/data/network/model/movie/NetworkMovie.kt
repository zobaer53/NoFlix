
package com.zobaer53.zedmovies.data.network.model.movie

import com.zobaer53.zedmovies.core.network.serializer.LocalDateSerializer
import com.zobaer53.zedmovies.core.network.util.Constants
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovie(
    @SerialName(Constants.Fields.ID)
    val id: Int,

    @SerialName(Constants.Fields.TITLE)
    val title: String,

    @SerialName(Constants.Fields.OVERVIEW)
    val overview: String,

    @SerialName(Constants.Fields.POPULARITY)
    val popularity: Double,

    @Serializable(LocalDateSerializer::class)
    @SerialName(Constants.Fields.RELEASE_DATE)
    val releaseDate: LocalDate?,

    @SerialName(Constants.Fields.ADULT)
    val adult: Boolean,

    @SerialName(Constants.Fields.GENRE_IDS)
    val genreIds: List<Int>,

    @SerialName(Constants.Fields.ORIGINAL_TITLE)
    val originalTitle: String,

    @SerialName(Constants.Fields.ORIGINAL_LANGUAGE)
    val originalLanguage: String,

    @SerialName(Constants.Fields.VOTE_AVERAGE)
    val voteAverage: Double,

    @SerialName(Constants.Fields.VOTE_COUNT)
    val voteCount: Int,

    @SerialName(Constants.Fields.POSTER_PATH)
    val posterPath: String?,

    @SerialName(Constants.Fields.BACKDROP_PATH)
    val backdropPath: String?,

    @SerialName(Constants.Fields.VIDEO)
    val video: Boolean
)
