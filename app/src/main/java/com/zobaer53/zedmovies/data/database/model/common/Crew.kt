

package com.zobaer53.zedmovies.data.database.model.common

import com.zobaer53.zedmovies.data.database.util.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Crew(
    @SerialName(Constants.Fields.ID)
    val id: Int,

    @SerialName(Constants.Fields.ADULT)
    val adult: Boolean,

    @SerialName(Constants.Fields.CREDIT_ID)
    val creditId: String,

    @SerialName(Constants.Fields.DEPARTMENT)
    val department: String,

    @SerialName(Constants.Fields.GENDER)
    val gender: Int?,

    @SerialName(Constants.Fields.JOB)
    val job: String,

    @SerialName(Constants.Fields.KNOWN_FOR_DEPARTMENT)
    val knownForDepartment: String,

    @SerialName(Constants.Fields.NAME)
    val name: String,

    @SerialName(Constants.Fields.ORIGINAL_NAME)
    val originalName: String,

    @SerialName(Constants.Fields.POPULARITY)
    val popularity: Double,

    @SerialName(Constants.Fields.PROFILE_PATH)
    val profilePath: String?
)
