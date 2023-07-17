

package com.zobaer53.zedmovies.data.util

internal fun String.titlecase() = lowercase().replaceFirstChar { it.uppercase() }
