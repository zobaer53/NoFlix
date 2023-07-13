package com.zobaer53.zedmovies.core.ui.util

import kotlin.math.roundToInt

internal fun Double.roundToOneDecimal() =
    (this * ONE_DECIMAL_ARGUMENT).roundToInt() / ONE_DECIMAL_ARGUMENT

private const val ONE_DECIMAL_ARGUMENT = 10.0
