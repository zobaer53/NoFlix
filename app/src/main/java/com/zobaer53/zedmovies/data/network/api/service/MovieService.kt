

package com.zobaer53.zedmovies.data.network.api.service

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.network.model.movie.NetworkMovieDetails
import com.zobaer53.zedmovies.data.network.model.response.MovieResponseDto
import com.zobaer53.zedmovies.data.network.util.Constants.DEFAULT_PAGE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.APPEND_TO_RESPONSE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.DETAILS_APPEND_TO_RESPONSE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.ID
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.LANGUAGE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.PAGE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.QUERY
import com.zobaer53.zedmovies.data.network.util.Constants.Path.DETAILS_MOVIE
import com.zobaer53.zedmovies.data.network.util.Constants.Path.DISCOVER_MOVIE
import com.zobaer53.zedmovies.data.network.util.Constants.Path.NOW_PLAYING_MOVIE
import com.zobaer53.zedmovies.data.network.util.Constants.Path.POPULAR_MOVIE
import com.zobaer53.zedmovies.data.network.util.Constants.Path.SEARCH_MOVIE
import com.zobaer53.zedmovies.data.network.util.Constants.Path.TOP_RATED_MOVIE
import com.zobaer53.zedmovies.data.network.util.Constants.Path.TRENDING_MOVIE
import com.zobaer53.zedmovies.data.network.util.Constants.Path.UPCOMING_MOVIE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET(UPCOMING_MOVIE)
    suspend fun getUpcoming(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<MovieResponseDto>

    @GET(TOP_RATED_MOVIE)
    suspend fun getTopRated(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<MovieResponseDto>

    @GET(POPULAR_MOVIE)
    suspend fun getPopular(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<MovieResponseDto>

    @GET(NOW_PLAYING_MOVIE)
    suspend fun getNowPlaying(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<MovieResponseDto>

    @GET(DISCOVER_MOVIE)
    suspend fun getDiscover(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<MovieResponseDto>

    @GET(TRENDING_MOVIE)
    suspend fun getTrending(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<MovieResponseDto>

    @GET(DETAILS_MOVIE)
    suspend fun getDetailsById(
        @Path(ID) id: Int,
        @Query(LANGUAGE) language: String,
        @Query(APPEND_TO_RESPONSE) appendToResponse: String = DETAILS_APPEND_TO_RESPONSE
    ): ZedMoviesResult<NetworkMovieDetails>

    @GET(SEARCH_MOVIE)
    suspend fun search(
        @Query(QUERY) query: String,
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<MovieResponseDto>
}
