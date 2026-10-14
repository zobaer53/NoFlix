package com.zobaer53.zedmovies.data.network.source

import com.zobaer53.zedmovies.data.network.api.service.FilterOptionsService
import javax.inject.Inject

class FilterOptionsNetworkDataSource @Inject constructor(private val service: FilterOptionsService) {
    suspend fun getMovieCertifications() = service.getMovieCertifications()

    suspend fun getMovieWatchProviders(region: String, language: String) =
        service.getMovieWatchProviders(region, language)

    suspend fun getTvWatchProviders(region: String, language: String) =
        service.getTvWatchProviders(region, language)
}
