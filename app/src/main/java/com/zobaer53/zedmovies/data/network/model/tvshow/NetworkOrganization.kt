package com.zobaer53.zedmovies.data.network.model.tvshow

import com.zobaer53.zedmovies.data.network.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkOrganization(
    @SerialName(Constants.Fields.ID)
    val id: Int,

    @SerialName(Constants.Fields.NAME)
    val name: String,

    @SerialName(Constants.Fields.ORIGIN_COUNTRY)
    val originCountry: String,

    @SerialName(Constants.Fields.LOGO_PATH)
    val logoPath: String?
)
