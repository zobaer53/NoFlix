

package com.zobaer53.zedmovies.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context) =
        context.preferencesDataStore
}

private const val PREFERENCES_NAME = "preferences"
private val Context.preferencesDataStore by preferencesDataStore(name = PREFERENCES_NAME)
