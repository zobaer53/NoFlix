package com.zobaer53.zedmovies.core.ui.mapper

import com.zobaer53.zedmovies.core.domain.model.CastModel
import com.zobaer53.zedmovies.core.domain.model.CreditsModel
import com.zobaer53.zedmovies.core.domain.model.CrewModel
import com.zobaer53.zedmovies.core.model.Cast
import com.zobaer53.zedmovies.core.model.Credits
import com.zobaer53.zedmovies.core.model.Crew

internal fun CreditsModel.asCredits() = Credits(
    cast = cast.map(CastModel::asCast),
    crew = crew.map(CrewModel::asCrew)
)

private fun CastModel.asCast() = Cast(
    id = id,
    name = name,
    character = character,
    profilePath = profilePath
)

private fun CrewModel.asCrew() = Crew(
    id = id,
    name = name,
    job = job,
    profilePath = profilePath
)
