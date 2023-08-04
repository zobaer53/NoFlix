
package com.zobaer53.zedmovies.data.database.converter

import androidx.room.TypeConverter
import com.zobaer53.zedmovies.data.database.model.common.Credits
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class CreditsConverter {
    @TypeConverter
    internal fun creditsToString(credits: Credits): String = Json.encodeToString(credits)

    @TypeConverter
    internal fun stringToCredits(string: String): Credits = Json.decodeFromString(string)
}
