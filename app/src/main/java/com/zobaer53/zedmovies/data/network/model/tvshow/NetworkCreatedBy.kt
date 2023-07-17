
package com.zobaer53.zedmovies.data.network.model.tvshow

import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCreatedBy(
    @SerialName(Constants.Fields.ID)
    val id: Int,

    @SerialName(Constants.Fields.CREDIT_ID)
    val creditId: String,

    @SerialName(Constants.Fields.NAME)
    val name: String,

    @SerialName(Constants.Fields.GENDER)
    val gender: Int,

    @SerialName(Constants.Fields.PROFILE_PATH)
    val profilePath: String?
)
