

package com.zobaer53.zedmovies.data.network.api

interface zedMoviesApiKeyProvider {
    val apiKey: String?
}

internal fun zedMoviesApiKeyProvider.requireApiKey() = checkNotNull(apiKey)
