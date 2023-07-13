package com.zobaer53.zedmovies.core.ui.mapper

import com.zobaer53.zedmovies.core.model.ReleaseDate
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

internal fun LocalDate.asReleaseDate() = ReleaseDate(
    fullDate = format(FullDatePattern),
    year = year.toString()
)

private fun LocalDate.format(pattern: String): String =
    toJavaLocalDate().format(DateTimeFormatter.ofPattern(pattern))

private const val FullDatePattern = "d MMMM, yyyy"
