
package com.zobaer53.zedmovies.domain.model

import kotlinx.datetime.LocalDate

data class TvShowModel(
    val id: Int,
    val name: String,
    val overview: String,
    val popularity: Double,
    val firstAirDate: LocalDate?,
    val genres: List<com.zobaer53.zedmovies.domain.model.GenreModel>,
    val originalName: String,
    val originalLanguage: String,
    val originCountry: List<String>,
    val voteAverage: Double,
    val voteCount: Int,
    val posterPath: String?,
    val backdropPath: String?
)
