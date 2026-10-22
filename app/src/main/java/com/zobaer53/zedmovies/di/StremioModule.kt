package com.zobaer53.zedmovies.di

import com.zobaer53.zedmovies.data.stremio.repository.StremioRepository
import com.zobaer53.zedmovies.data.stremio.repository.StremioRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StremioRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindStremioRepository(impl: StremioRepositoryImpl): StremioRepository
}

@Module
@InstallIn(SingletonComponent::class)
object StremioNetworkModule {
    @Provides
    @Singleton
    fun provideStremioJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideStremioOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
}

