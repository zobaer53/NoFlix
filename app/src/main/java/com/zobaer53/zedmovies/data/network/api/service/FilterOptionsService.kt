package com.zobaer53.zedmovies.data.network.api.service

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.network.model.common.NetworkCertificationsResponse
import com.zobaer53.zedmovies.data.network.model.common.NetworkWatchProvidersResponse
import com.zobaer53.zedmovies.data.network.util.Constants.Discover.WATCH_REGION
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.LANGUAGE
import com.zobaer53.zedmovies.data.network.util.Constants.Path.MOVIE_CERTIFICATIONS
import com.zobaer53.zedmovies.data.network.util.Constants.Path.MOVIE_WATCH_PROVIDERS
import com.zobaer53.zedmovies.data.network.util.Constants.Path.TV_WATCH_PROVIDERS
import retrofit2.http.GET
import retrofit2.http.Query

/** Choices for the Search tab's filters that TMDB maintains. */
interface FilterOptionsService {
    @GET(MOVIE_CERTIFICATIONS)
    suspend fun getMovieCertifications(): ZedMoviesResult<NetworkCertificationsResponse>

    @GET(MOVIE_WATCH_PROVIDERS)
    suspend fun getMovieWatchProviders(
        @Query(WATCH_REGION) region: String,
        @Query(LANGUAGE) language: String
    ): ZedMoviesResult<NetworkWatchProvidersResponse>

    @GET(TV_WATCH_PROVIDERS)
    suspend fun getTvWatchProviders(
        @Query(WATCH_REGION) region: String,
        @Query(LANGUAGE) language: String
    ): ZedMoviesResult<NetworkWatchProvidersResponse>
}
