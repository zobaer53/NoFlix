package com.zobaer53.zedmovies.di

import com.zobaer53.zedmovies.core.network.api.zedMoviesApi
import com.zobaer53.zedmovies.core.network.api.zedMoviesApiKeyProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providezedMoviesApi(apiKeyProvider: zedMoviesApiKeyProvider) = zedMoviesApi(apiKeyProvider)

    @Provides
    fun providezedMoviesApiKeyProvider() = object : zedMoviesApiKeyProvider {
        override val apiKey: String = BuildConfig.zedMovies_API_KEY
    }

    @Provides
    fun provideMovieService(api: zedMoviesApi) = api.movieService

    @Provides
    fun provideTvShowService(api: zedMoviesApi) = api.tvShowService
}
