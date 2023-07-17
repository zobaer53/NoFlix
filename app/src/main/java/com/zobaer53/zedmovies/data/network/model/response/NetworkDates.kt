
package com.zobaer53.zedmovies.data.network.model.response

import com.zobaer53.zedmovies.core.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkDates(
    @SerialName(Constants.Fields.MAXIMUM)
    val maximum: String,

    @SerialName(Constants.Fields.MINIMUM)
    val minimum: String
)
