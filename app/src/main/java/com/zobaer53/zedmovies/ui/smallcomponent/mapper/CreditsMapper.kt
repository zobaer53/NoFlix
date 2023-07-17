package com.zobaer53.zedmovies.ui.smallcomponent.mapper

import com.zobaer53.zedmovies.domain.model.CastModel
import com.zobaer53.zedmovies.domain.model.CreditsModel
import com.zobaer53.zedmovies.domain.model.CrewModel
import com.zobaer53.zedmovies.data.model.Cast
import com.zobaer53.zedmovies.data.model.Credits
import com.zobaer53.zedmovies.data.model.Crew

internal fun com.zobaer53.zedmovies.domain.model.CreditsModel.asCredits() = Credits(
    cast = cast.map(CastModel::asCast),
    crew = crew.map(CrewModel::asCrew)
)

private fun com.zobaer53.zedmovies.domain.model.CastModel.asCast() = Cast(
    id = id,
    name = name,
    character = character,
    profilePath = profilePath
)

private fun com.zobaer53.zedmovies.domain.model.CrewModel.asCrew() = Crew(
    id = id,
    name = name,
    job = job,
    profilePath = profilePath
)
