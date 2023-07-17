

package com.zobaer53.zedmovies.domain.model

data class CrewModel(
    val id: Int,
    val adult: Boolean,
    val creditId: String,
    val department: String,
    val gender: Int?,
    val job: String,
    val knownForDepartment: String,
    val name: String,
    val originalName: String,
    val popularity: Double,
    val profilePath: String?
)
