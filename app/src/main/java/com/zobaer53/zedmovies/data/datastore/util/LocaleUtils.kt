package com.zobaer53.zedmovies.data.datastore.util

import android.content.Context
import androidx.core.os.ConfigurationCompat

internal fun Context.getDefaultLanguage(): String {
    val locale = checkNotNull(ConfigurationCompat.getLocales(resources.configuration)[0])
    return locale.language
}
