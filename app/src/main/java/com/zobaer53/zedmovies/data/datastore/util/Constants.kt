

package com.zobaer53.zedmovies.data.datastore.util

import androidx.datastore.preferences.core.stringPreferencesKey

internal object Constants {
    private const val CONTENT_LANGUAGE_NAME = "content_language"
    internal val CONTENT_LANGUAGE_KEY = stringPreferencesKey(CONTENT_LANGUAGE_NAME)
}
