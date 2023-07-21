

package com.zobaer53.zedmovies.domain.model

import kotlinx.datetime.LocalDate

data class TvShowDetailsModel(
    val id: Int,
    val name: String,
    val adult: Boolean,
    val backdropPath: String?,
    val episodeRunTime: List<Int>,
    val firstAirDate: LocalDate?,
    val genres: List<GenreModel>,
    val homepage: String,
    val inProduction: Boolean,
    val languages: List<String>,
    val lastAirDate: LocalDate?,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalName: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String?,
    val status: String,
    val tagline: String,
    val type: String,
    val voteAverage: Double,
    val voteCount: Int,
    val credits: CreditsModel,
    val isWishlisted: Boolean
)
