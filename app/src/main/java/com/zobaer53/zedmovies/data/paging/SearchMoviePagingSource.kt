

package com.zobaer53.zedmovies.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zobaer53.zedmovies.data.common.result.HttpException
import com.zobaer53.zedmovies.data.common.result.isFailure
import com.zobaer53.zedmovies.data.common.result.isSuccess
import com.zobaer53.zedmovies.core.data.mapper.asMovieModel
import com.zobaer53.zedmovies.core.data.util.Constants
import com.zobaer53.zedmovies.data.datastore.PreferencesDataStoreDataSource
import com.zobaer53.zedmovies.core.domain.model.MovieModel
import com.zobaer53.zedmovies.core.network.model.movie.NetworkMovie
import com.zobaer53.zedmovies.core.network.source.MovieNetworkDataSource
import com.zobaer53.zedmovies.core.network.util.DEFAULT_PAGE
import kotlinx.coroutines.flow.first
import java.io.IOException

class SearchMoviePagingSource(
    private val query: String,
    private val networkDataSource: MovieNetworkDataSource,
    private val preferencesDataStoreDataSource: PreferencesDataStoreDataSource
) : PagingSource<Int, MovieModel>() {

    override fun getRefreshKey(state: PagingState<Int, MovieModel>) = state.anchorPosition

    @Suppress("ReturnCount")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieModel> {
        return try {
            val currentPage = params.key ?: DEFAULT_PAGE
            val response = networkDataSource.search(
                query = query,
                language = preferencesDataStoreDataSource.getContentLanguage().first(),
                page = currentPage
            )

            when {
                response.isSuccess() -> {
                    val data = response.value.results.map(NetworkMovie::asMovieModel)
                    val endOfPaginationReached = data.isEmpty()

                    val prevPage = if (currentPage == 1) null else currentPage - 1
                    val nextPage = if (endOfPaginationReached) null else currentPage + 1

                    LoadResult.Page(
                        data = data,
                        prevKey = prevPage,
                        nextKey = nextPage
                    )
                }
                response.isFailure() -> return LoadResult.Error(response.error)
                else -> error("${Constants.Messages.UNHANDLED_STATE} $response")
            }
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
