
package com.zobaer53.zedmovies.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.zobaer53.zedmovies.data.datastore.util.Constants.CONTENT_LANGUAGE_KEY
import com.zobaer53.zedmovies.data.datastore.util.getDefaultLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesDataStoreDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    fun getContentLanguage(): Flow<String> = dataStore.data.map { preferences ->
        preferences[CONTENT_LANGUAGE_KEY] ?: run {
            val defaultLanguage = context.getDefaultLanguage()
            setContentLanguage(defaultLanguage)
            defaultLanguage
        }
    }

    suspend fun setContentLanguage(contentLanguage: String) = dataStore.edit { preferences ->
        preferences[CONTENT_LANGUAGE_KEY] = contentLanguage
    }
}
