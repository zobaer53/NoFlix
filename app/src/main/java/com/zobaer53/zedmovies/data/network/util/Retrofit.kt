

package com.zobaer53.zedmovies.data.network.util

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.zobaer53.zedmovies.data.network.api.zedMoviesApiKeyProvider
import com.zobaer53.zedmovies.data.network.api.zedMoviesAuthInterceptor
import com.zobaer53.zedmovies.data.network.retrofit.ResultAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit

internal fun retrofit(
    apiKeyProvider: zedMoviesApiKeyProvider,
    json: Json = defaultJson
): Retrofit = Retrofit.Builder()
    .baseUrl(Constants.API_URL)
    .client(authorizedOkHttpClient(apiKeyProvider))
    .addConverterFactory(json.asConverterFactory(MIMETYPE_JSON))
    .addCallAdapterFactory(ResultAdapterFactory())
    .build()

private fun authorizedOkHttpClient(apiKeyProvider: zedMoviesApiKeyProvider): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(zedMoviesAuthInterceptor(apiKeyProvider))
        .build()
