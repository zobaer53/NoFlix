

package com.zobaer53.zedmovies.data.model

data class Credits(
    val cast: List<Cast>,
    val crew: List<Crew>
)

data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
)

data class Crew(
    val id: Int,
    val name: String,
    val job: String,
    val profilePath: String?
)
