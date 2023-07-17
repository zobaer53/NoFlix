

package com.zobaer53.zedmovies.data.network.model.movie

import com.zobaer53.zedmovies.data.network.model.common.NetworkCredits
import com.zobaer53.zedmovies.data.network.model.common.NetworkGenre
import com.zobaer53.zedmovies.data.network.model.common.NetworkProductionCompany
import com.zobaer53.zedmovies.data.network.model.common.NetworkProductionCountry
import com.zobaer53.zedmovies.data.network.model.common.NetworkSpokenLanguage
import com.zobaer53.zedmovies.data.network.serializer.LocalDateSerializer
import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovieDetails(
    @SerialName(Constants.Fields.ID)
    val id: Int,

    @SerialName(Constants.Fields.ADULT)
    val adult: Boolean,

    @SerialName(Constants.Fields.BACKDROP_PATH)
    val backdropPath: String?,

    @SerialName(Constants.Fields.BELONGS_TO_COLLECTION)
    val belongsToCollection: NetworkBelongsToCollection?,

    @SerialName(Constants.Fields.BUDGET)
    val budget: Int,

    @SerialName(Constants.Fields.GENRES)
    val genres: List<NetworkGenre>,

    @SerialName(Constants.Fields.HOMEPAGE)
    val homepage: String?,

    @SerialName(Constants.Fields.IMDB_ID)
    val imdbId: String?,

    @SerialName(Constants.Fields.ORIGINAL_LANGUAGE)
    val originalLanguage: String,

    @SerialName(Constants.Fields.ORIGINAL_TITLE)
    val originalTitle: String,

    @SerialName(Constants.Fields.OVERVIEW)
    val overview: String,

    @SerialName(Constants.Fields.POPULARITY)
    val popularity: Double,

    @SerialName(Constants.Fields.POSTER_PATH)
    val posterPath: String?,

    @SerialName(Constants.Fields.PRODUCTION_COMPANIES)
    val productionCompanies: List<NetworkProductionCompany>,

    @SerialName(Constants.Fields.PRODUCTION_COUNTRIES)
    val productionCountries: List<NetworkProductionCountry>,

    @Serializable(LocalDateSerializer::class)
    @SerialName(Constants.Fields.RELEASE_DATE)
    val releaseDate: LocalDate?,

    @SerialName(Constants.Fields.REVENUE)
    val revenue: Long,

    @SerialName(Constants.Fields.RUNTIME)
    val runtime: Int?,

    @SerialName(Constants.Fields.SPOKEN_LANGUAGES)
    val spokenLanguages: List<NetworkSpokenLanguage>,

    @SerialName(Constants.Fields.STATUS)
    val status: String,

    @SerialName(Constants.Fields.TAGLINE)
    val tagline: String?,

    @SerialName(Constants.Fields.TITLE)
    val title: String,

    @SerialName(Constants.Fields.VIDEO)
    val video: Boolean,

    @SerialName(Constants.Fields.VOTE_AVERAGE)
    val voteAverage: Double,

    @SerialName(Constants.Fields.VOTE_COUNT)
    val voteCount: Int,

    @SerialName(Constants.Fields.CREDITS)
    val credits: NetworkCredits
)
