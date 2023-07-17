
package com.zobaer53.zedmovies.data.network.model.common

import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCredits(
    @SerialName(Constants.Fields.CAST)
    val cast: List<NetworkCast>,

    @SerialName(Constants.Fields.CREW)
    val crew: List<NetworkCrew>
)
