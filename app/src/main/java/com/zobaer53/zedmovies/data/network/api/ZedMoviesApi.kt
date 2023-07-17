
package com.zobaer53.zedmovies.data.network.api

import com.zobaer53.zedmovies.data.network.api.service.MovieService
import com.zobaer53.zedmovies.data.network.api.service.TvShowService
import com.zobaer53.zedmovies.data.network.util.retrofit
import retrofit2.create
import javax.inject.Inject

class zedMoviesApi @Inject constructor(private val apiKeyProvider: zedMoviesApiKeyProvider) {
    val movieService: MovieService by lazy { retrofit(apiKeyProvider).create() }
    val tvShowService: TvShowService by lazy { retrofit(apiKeyProvider).create() }
}
