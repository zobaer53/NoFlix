package com.zobaer53.zedmovies.data.network.api.service

import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.network.model.response.TvShowResponseDto
import com.zobaer53.zedmovies.data.network.model.tvshow.NetworkTvShowDetails
import com.zobaer53.zedmovies.data.network.util.Constants.DEFAULT_PAGE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.APPEND_TO_RESPONSE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.DETAILS_APPEND_TO_RESPONSE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.ID
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.LANGUAGE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.PAGE
import com.zobaer53.zedmovies.data.network.util.Constants.Fields.QUERY
import com.zobaer53.zedmovies.data.network.util.Constants.Path.DETAILS_TV_SHOW
import com.zobaer53.zedmovies.data.network.util.Constants.Path.DISCOVER_TV_SHOW
import com.zobaer53.zedmovies.data.network.util.Constants.Path.ON_THE_AIR_TV_SHOW
import com.zobaer53.zedmovies.data.network.util.Constants.Path.POPULAR_TV_SHOW
import com.zobaer53.zedmovies.data.network.util.Constants.Path.SEARCH_TV_SHOW
import com.zobaer53.zedmovies.data.network.util.Constants.Path.TOP_RATED_TV_SHOW
import com.zobaer53.zedmovies.data.network.util.Constants.Path.TRENDING_TV_SHOW
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvShowService {
    @GET(TOP_RATED_TV_SHOW)
    suspend fun getTopRated(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<TvShowResponseDto>

    @GET(POPULAR_TV_SHOW)
    suspend fun getPopular(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<TvShowResponseDto>

    @GET(ON_THE_AIR_TV_SHOW)
    suspend fun getOnTheAir(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<TvShowResponseDto>

    @GET(DISCOVER_TV_SHOW)
    suspend fun getDiscover(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<TvShowResponseDto>

    @GET(TRENDING_TV_SHOW)
    suspend fun getTrending(
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<TvShowResponseDto>

    @GET(DETAILS_TV_SHOW)
    suspend fun getDetailsById(
        @Path(ID) id: Int,
        @Query(APPEND_TO_RESPONSE) appendToResponse: String = DETAILS_APPEND_TO_RESPONSE,
        @Query(LANGUAGE) language: String
    ): ZedMoviesResult<NetworkTvShowDetails>

    @GET(SEARCH_TV_SHOW)
    suspend fun search(
        @Query(QUERY) query: String,
        @Query(LANGUAGE) language: String,
        @Query(PAGE) page: Int = DEFAULT_PAGE
    ): ZedMoviesResult<TvShowResponseDto>
}
