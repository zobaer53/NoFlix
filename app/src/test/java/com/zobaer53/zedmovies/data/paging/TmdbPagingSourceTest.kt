package com.zobaer53.zedmovies.data.paging

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import com.zobaer53.zedmovies.data.common.result.ZedMoviesResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class TmdbPagingSourceTest {
    private fun refresh() = LoadParams.Refresh<Int>(key = null, loadSize = 20, placeholdersEnabled = false)
    private fun append(key: Int) = LoadParams.Append(key = key, loadSize = 20, placeholdersEnabled = false)

    @Test
    fun `pages until total pages`() = runTest {
        val source = TmdbPagingSource { page -> ZedMoviesResult.success(ResultsPage(listOf(page), totalPages = 2)) }
        val first = source.load(refresh()) as LoadResult.Page
        assertEquals(listOf(1), first.data)
        assertEquals(2, first.nextKey)
        assertNull(first.prevKey)
        val second = source.load(append(2)) as LoadResult.Page
        assertNull(second.nextKey)
    }

    @Test
    fun `stops at the max page`() = runTest {
        val source = TmdbPagingSource(maxPage = 3) { page ->
            ZedMoviesResult.success(ResultsPage(listOf(page), totalPages = 10_000))
        }
        assertNull((source.load(append(3)) as LoadResult.Page).nextKey)
    }

    @Test
    fun `empty filtered pages are skipped within one load`() = runTest {
        val requested = mutableListOf<Int>()
        val source = TmdbPagingSource(maxRequestsPerLoad = 5) { page ->
            requested += page
            ZedMoviesResult.success(ResultsPage(if (page == 3) listOf(page) else emptyList(), totalPages = 10))
        }
        val result = source.load(refresh()) as LoadResult.Page
        assertEquals(listOf(1, 2, 3), requested)
        assertEquals(listOf(3), result.data)
        assertEquals(4, result.nextKey)
    }

    @Test
    fun `an empty page after the request limit still has a next key`() = runTest {
        val source = TmdbPagingSource(maxRequestsPerLoad = 5) { _ ->
            ZedMoviesResult.success(ResultsPage(emptyList<Int>(), totalPages = 100))
        }
        val result = source.load(refresh()) as LoadResult.Page
        assertTrue(result.data.isEmpty())
        assertEquals(6, result.nextKey)
    }

    @Test
    fun `empty last page ends paging`() = runTest {
        val source = TmdbPagingSource(maxRequestsPerLoad = 5) { _ ->
            ZedMoviesResult.success(ResultsPage(emptyList<Int>(), totalPages = 2))
        }
        val result = source.load(refresh()) as LoadResult.Page
        assertTrue(result.data.isEmpty())
        assertNull(result.nextKey)
    }

    @Test
    fun `failures become load errors`() = runTest {
        val error = IOException("offline")
        val source = TmdbPagingSource<Int> { _ -> ZedMoviesResult.failure(error) }
        assertEquals(error, (source.load(refresh()) as LoadResult.Error).throwable)
    }
}
