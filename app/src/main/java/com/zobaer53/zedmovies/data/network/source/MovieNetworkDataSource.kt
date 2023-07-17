
package com.zobaer53.zedmovies.data.network.source

import com.zobaer53.zedmovies.data.common.result.zedMoviesResult
import com.zobaer53.zedmovies.data.network.api.service.MovieService
import com.zobaer53.zedmovies.data.network.model.common.NetworkMediaType
import com.zobaer53.zedmovies.data.network.model.response.MovieResponseDto
import com.zobaer53.zedmovies.data.network.util.Constants.DEFAULT_PAGE
import javax.inject.Inject

class MovieNetworkDataSource @Inject constructor(private val movieService: MovieService) {
    suspend fun getByMediaType(
        mediaType: NetworkMediaType.Movie,
        language: String,
        page: Int = DEFAULT_PAGE
    ): zedMoviesResult<MovieResponseDto> = when (mediaType) {
        NetworkMediaType.Movie.UPCOMING -> movieService.getUpcoming(language, page)
        NetworkMediaType.Movie.TOP_RATED -> movieService.getTopRated(language, page)
        NetworkMediaType.Movie.POPULAR -> movieService.getPopular(language, page)
        NetworkMediaType.Movie.NOW_PLAYING -> movieService.getNowPlaying(language, page)
        NetworkMediaType.Movie.DISCOVER -> movieService.getDiscover(language, page)
        NetworkMediaType.Movie.TRENDING -> movieService.getTrending(language, page)
    }

    suspend fun search(
        query: String,
        language: String,
        page: Int = DEFAULT_PAGE
    ): zedMoviesResult<MovieResponseDto> = movieService.search(query, language, page)
}
