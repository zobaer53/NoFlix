

package com.zobaer53.zedmovies.data.network.model.common

import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSpokenLanguage(
    @SerialName(Constants.Fields.NAME)
    val name: String,

    @SerialName(Constants.Fields.ENGLISH_NAME)
    val englishName: String,

    @SerialName(Constants.Fields.ISO_639_1)
    val iso: String
)
