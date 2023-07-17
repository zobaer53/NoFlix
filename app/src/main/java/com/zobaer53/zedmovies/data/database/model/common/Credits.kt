

package com.zobaer53.zedmovies.data.database.model.common

import com.zobaer53.zedmovies.core.database.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Credits(
    @SerialName(Constants.Fields.CAST)
    val cast: List<Cast>,

    @SerialName(Constants.Fields.CREW)
    val crew: List<Crew>
)
