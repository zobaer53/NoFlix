package com.zobaer53.zedmovies.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zobaer53.zedmovies.data.common.result.HttpException
import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import com.zobaer53.zedmovies.data.common.result.isFailure
import com.zobaer53.zedmovies.data.common.result.isSuccess
import com.zobaer53.zedmovies.data.network.util.DEFAULT_PAGE
import com.zobaer53.zedmovies.data.util.Constants
import java.io.IOException

/** TMDB pages one load may request when filtering in the app (see [TmdbPagingSource]). */
internal const val FILTERED_REQUESTS_PER_LOAD = 5

/** One TMDB results page: the titles to show and how many pages TMDB has in total. */
internal data class ResultsPage<T>(val results: List<T>, val totalPages: Int)

/**
 * Network-only paging over a TMDB list endpoint (search and discover; nothing is cached in Room).
 *
 * [fetch] may drop titles from a page (in-app filters). When that leaves a page empty, the next
 * TMDB page is fetched within the same load, up to [maxRequestsPerLoad] requests, and an empty page
 * that still has a next key is returned after that, so a strict filter neither stalls the list nor
 * loops forever. Paging stops at TMDB's `total_pages` or [maxPage], whichever is lower.
 */
internal class TmdbPagingSource<T : Any>(
    private val maxPage: Int = Int.MAX_VALUE,
    private val maxRequestsPerLoad: Int = 1,
    private val fetch: suspend (page: Int) -> ZedMoviesResult<ResultsPage<T>>
) : PagingSource<Int, T>() {

    // Keys can skip pages, so a refresh starts over from the first page.
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    @Suppress("ReturnCount")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        var page = params.key ?: DEFAULT_PAGE
        var requests = 0
        try {
            while (true) {
                val response = fetch(page)
                requests++
                when {
                    response.isSuccess() -> {
                        val results = response.value.results
                        val lastPage = minOf(response.value.totalPages, maxPage)
                        val nextPage = (page + 1).takeIf { page < lastPage }
                        if (results.isNotEmpty() || nextPage == null || requests >= maxRequestsPerLoad) {
                            return LoadResult.Page(data = results, prevKey = null, nextKey = nextPage)
                        }
                        page = nextPage
                    }
                    response.isFailure() -> return LoadResult.Error(response.error)
                    else -> error("${Constants.Messages.UNHANDLED_STATE} $response")
                }
            }
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
