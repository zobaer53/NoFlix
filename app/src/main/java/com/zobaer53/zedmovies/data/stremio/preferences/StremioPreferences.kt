package com.zobaer53.zedmovies.data.stremio.preferences

import android.content.Context
import android.os.Environment
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zobaer53.zedmovies.data.stremio.api.StremioAddonService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StremioPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ADDONS_KEY = stringPreferencesKey("stremio_addons_list")
        private val DOWNLOAD_DIR_KEY = stringPreferencesKey("stremio_download_directory")
    }

    val defaultDownloadDir: String
        get() {
            val externalPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val appDir = File(externalPublicDir, "NoFlix")
            if (!appDir.exists()) appDir.mkdirs()
            return appDir.absolutePath
        }

    val addonsFlow: Flow<List<String>> = dataStore.data.map { preferences ->
        val saved = preferences[ADDONS_KEY]
        if (saved.isNullOrBlank()) {
            listOf(StremioAddonService.DEFAULT_TORRENTIO_MANIFEST)
        } else {
            saved.split("\n")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }
    }

    val downloadDirFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[DOWNLOAD_DIR_KEY]?.ifBlank { null } ?: defaultDownloadDir
    }

    suspend fun saveAddons(addons: List<String>) {
        dataStore.edit { preferences ->
            preferences[ADDONS_KEY] = addons
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .distinct()
                .joinToString("\n")
        }
    }

    suspend fun addAddon(manifestUrl: String) {
        val trimmed = manifestUrl.trim()
        if (trimmed.isBlank()) return
        dataStore.edit { preferences ->
            val current = preferences[ADDONS_KEY]?.split("\n")?.map { it.trim() }?.filter { it.isNotBlank() } ?: listOf(StremioAddonService.DEFAULT_TORRENTIO_MANIFEST)
            if (!current.contains(trimmed)) {
                preferences[ADDONS_KEY] = (current + trimmed).joinToString("\n")
            }
        }
    }

    suspend fun removeAddon(manifestUrl: String) {
        dataStore.edit { preferences ->
            val current = preferences[ADDONS_KEY]?.split("\n")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
            preferences[ADDONS_KEY] = current.filter { it != manifestUrl.trim() }.joinToString("\n")
        }
    }

    suspend fun saveDownloadDir(dirPath: String) {
        dataStore.edit { preferences ->
            preferences[DOWNLOAD_DIR_KEY] = dirPath.trim()
        }
    }
}

