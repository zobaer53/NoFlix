
package com.zobaer53.zedmovies.data.network.model.common

import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkProductionCountry(
    @SerialName(Constants.Fields.NAME)
    val name: String,

    @SerialName(Constants.Fields.ISO_3166_1)
    val iso: String
)
