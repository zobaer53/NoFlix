

package com.zobaer53.zedmovies.domain.model

import kotlinx.datetime.LocalDate

data class MovieDetailsModel(
    val id: Int,
    val adult: Boolean,
    val backdropPath: String?,
    val budget: Int,
    val genres: List<com.zobaer53.zedmovies.domain.model.GenreModel>,
    val homepage: String?,
    val imdbId: String?,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String?,
    val releaseDate: LocalDate?,
    val revenue: Long,
    val runtime: Int?,
    val status: String,
    val tagline: String?,
    val title: String,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int,
    val credits: com.zobaer53.zedmovies.domain.model.CreditsModel,
    val isWishlisted: Boolean
)
