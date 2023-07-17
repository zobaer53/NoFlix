package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.core.database.model.common.Cast
import com.zobaer53.zedmovies.core.database.model.common.Credits
import com.zobaer53.zedmovies.core.database.model.common.Crew
import com.zobaer53.zedmovies.domain.model.CastModel
import com.zobaer53.zedmovies.domain.model.CreditsModel
import com.zobaer53.zedmovies.domain.model.CrewModel
import com.zobaer53.zedmovies.core.network.model.common.NetworkCast
import com.zobaer53.zedmovies.core.network.model.common.NetworkCredits
import com.zobaer53.zedmovies.core.network.model.common.NetworkCrew

fun NetworkCredits.asCredits() = Credits(
    cast = cast.map(NetworkCast::asCast),
    crew = crew.map(NetworkCrew::asCrew)
)

fun NetworkCast.asCast() = Cast(
    id = id,
    name = name,
    adult = adult,
    castId = castId,
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

fun Credits.asCreditsModel() = com.zobaer53.zedmovies.domain.model.CreditsModel(
    cast = cast.map(Cast::asCastModel),
    crew = crew.map(Crew::asCrewModel)
)

fun Cast.asCastModel() = com.zobaer53.zedmovies.domain.model.CastModel(
    id = id,
    name = name,
    adult = adult,
    castId = castId,
    character = character,
    creditId = creditId,
    gender = gender,
    knownForDepartment = knownForDepartment,
    order = order,
    originalName = originalName,
    popularity = popularity,
    profilePath = profilePath
)

fun Crew.asCrewModel() = com.zobaer53.zedmovies.domain.model.CrewModel(
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
