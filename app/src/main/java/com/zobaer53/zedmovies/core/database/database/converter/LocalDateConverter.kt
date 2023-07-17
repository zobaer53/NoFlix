package com.zobaer53.zedmovies.core.database.converter

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate

internal class LocalDateConverter {
    @TypeConverter
    internal fun localDateToString(localDate: LocalDate): String = localDate.toString()

    @TypeConverter
    internal fun stringToLocalDate(string: String): LocalDate = string.toLocalDate()
}
