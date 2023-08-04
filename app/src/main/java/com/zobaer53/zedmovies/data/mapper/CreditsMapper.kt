package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.database.model.common.Cast
import com.zobaer53.zedmovies.data.database.model.common.Credits
import com.zobaer53.zedmovies.data.database.model.common.Crew
import com.zobaer53.zedmovies.data.network.model.common.NetworkCast
import com.zobaer53.zedmovies.data.network.model.common.NetworkCredits
import com.zobaer53.zedmovies.data.network.model.common.NetworkCrew
import com.zobaer53.zedmovies.domain.model.CastModel
import com.zobaer53.zedmovies.domain.model.CreditsModel
import com.zobaer53.zedmovies.domain.model.CrewModel

fun NetworkCredits.asCredits() = Credits(
    cast = cast.map(NetworkCast::asCast),
    crew = crew.map(NetworkCrew::asCrew)
)

fun NetworkCast.asCast() = Cast(
    id = id,
    name = name,
    adult = adult,
    castId = 0,
    character = character,
    creditId = creditId,
    gender = gender,
    knownForDepartment = knownForDepartment,
    order = order,
    originalName = originalName,
    popularity = popularity,
    profilePath = profilePath?.asImageUrl()
)

fun NetworkCrew.asCrew() = Crew(
    id = id,
    adult = adult,
    creditId = creditId,
    department = department,
    gender = gender,
    job = job,
    knownForDepartment = knownForDepartment,
    name = name,
    originalName = originalName,
    popularity = popularity,
    profilePath = profilePath?.asImageUrl()
)

fun Credits.asCreditsModel() = CreditsModel(
    cast = cast.map(Cast::asCastModel),
    crew = crew.map(Crew::asCrewModel)
)

fun Cast.asCastModel() = CastModel(
    id = id,
    name = name,
    adult = adult,
    castId = 0,
    character = character,
    creditId = creditId,
    gender = gender,
    knownForDepartment = knownForDepartment,
    order = order,
    originalName = originalName,
    popularity = popularity,
    profilePath = profilePath
)

fun Crew.asCrewModel() = CrewModel(
    id = id,
    adult = adult,
    creditId = creditId,
    department = department,
    gender = gender,
    job = job,
    knownForDepartment = knownForDepartment,
    name = name,
    originalName = originalName,
    popularity = popularity,
    profilePath = profilePath
)
