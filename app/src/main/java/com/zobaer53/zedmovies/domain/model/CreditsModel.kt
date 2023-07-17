
package com.zobaer53.zedmovies.domain.model

data class CreditsModel(
    val cast: List<com.zobaer53.zedmovies.domain.model.CastModel>,
    val crew: List<com.zobaer53.zedmovies.domain.model.CrewModel>
)
