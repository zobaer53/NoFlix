

package com.zobaer53.zedmovies.di

import android.content.Context
import androidx.room.Room
import com.zobaer53.zedmovies.data.database.zedMoviesDatabase
import com.zobaer53.zedmovies.data.database.util.zedMoviesVersionProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providezedMoviesDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        zedMoviesDatabase::class.java,
        zedMoviesDatabase.zedMovies_DATABASE
    ).build()

    @Provides
    fun providezedMoviesVersionProvider() = object : zedMoviesVersionProvider {
        override val version: String = BuildConfig.VERSION_NAME
    }

    @Provides
    fun provideMovieDao(database: zedMoviesDatabase) = database.movieDao

    @Provides
    fun provideTvShowDao(database: zedMoviesDatabase) = database.tvShowDao

    @Provides
    fun provideMovieRemoteKeyDao(database: zedMoviesDatabase) = database.movieRemoteKeyDao

    @Provides
    fun provideTvShowRemoteKeyDao(database: zedMoviesDatabase) = database.tvShowRemoteKeyDao

    @Provides
    fun provideMovieDetailsDao(database: zedMoviesDatabase) = database.movieDetailsDao

    @Provides
    fun provideTvShowDetailsDao(database: zedMoviesDatabase) = database.tvShowDetailsDao

    @Provides
    fun provideWishlistDao(database: zedMoviesDatabase) = database.wishlistDao
}
object BuildConfig {
    val DEBUG = true
    const val APPLICATION_ID = "com.zobaer53.zedmovies"
    const val BUILD_TYPE = "debug"
    const val VERSION_CODE = 5
    const val VERSION_NAME = "1.0.4"

    // Field from default config.
    const val zedMovies_API_KEY = "yourApiKey"
}
