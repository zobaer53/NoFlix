package com.zobaer53.zedmovies.data.database.converter

import androidx.room.TypeConverter
import com.zobaer53.zedmovies.core.database.model.common.Genre
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class ListConverter {
    @TypeConverter
    internal fun intListToString(list: List<Int>): String = Json.encodeToString(list)

    @TypeConverter
    internal fun stringToIntList(string: String): List<Int> = Json.decodeFromString(string)

    @TypeConverter
    internal fun stringListToString(list: List<String>): String = Json.encodeToString(list)

    @TypeConverter
    internal fun stringToStringList(string: String): List<String> = Json.decodeFromString(string)

    @TypeConverter
    internal fun genreListToString(list: List<Genre>): String = Json.encodeToString(list)

    @TypeConverter
    internal fun stringToGenreList(string: String): List<Genre> = Json.decodeFromString(string)
}
