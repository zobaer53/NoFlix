

package com.zobaer53.zedmovies.data.network.api

import com.zobaer53.zedmovies.data.network.util.Constants
import okhttp3.Interceptor
import okhttp3.Response

internal class zedMoviesAuthInterceptor(private val apiKeyProvider: zedMoviesApiKeyProvider) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val url = request.url.newBuilder()
            .addQueryParameter(API_KEY_QUERY_PARAM, apiKeyProvider.requireApiKey())
            .build()

        val newRequest = request.newBuilder().url(url).build()
        return chain.proceed(newRequest)
    }

    private companion object {
        private const val API_KEY_QUERY_PARAM = Constants.API_KEY_QUERY_PARAM
    }
}
