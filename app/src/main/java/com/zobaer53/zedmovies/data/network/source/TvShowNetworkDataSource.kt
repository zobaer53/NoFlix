package com.zobaer53.zedmovies.data.network.source

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.network.api.service.TvShowService
import com.zobaer53.zedmovies.data.network.model.common.NetworkMediaType
import com.zobaer53.zedmovies.data.network.model.response.TvShowResponseDto
import com.zobaer53.zedmovies.data.network.util.Constants.DEFAULT_PAGE
import javax.inject.Inject

class TvShowNetworkDataSource @Inject constructor(private val tvShowService: TvShowService) {
    suspend fun getByMediaType(
        mediaType: NetworkMediaType.TvShow,
        language: String,
        page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<TvShowResponseDto> = when (mediaType) {
        NetworkMediaType.TvShow.TOP_RATED -> tvShowService.getTopRated(language, page)
        NetworkMediaType.TvShow.POPULAR -> tvShowService.getPopular(language, page)
        NetworkMediaType.TvShow.NOW_PLAYING -> tvShowService.getOnTheAir(language, page)
        NetworkMediaType.TvShow.DISCOVER -> tvShowService.getDiscover(language, page)
        NetworkMediaType.TvShow.TRENDING -> tvShowService.getTrending(language, page)
    }

    suspend fun search(
        query: String,
        language: String,
        page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<TvShowResponseDto> = tvShowService.search(query, language, page)
}
