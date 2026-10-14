package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.domain.model.FilterGenreModel
import com.zobaer53.zedmovies.domain.model.FilterMediaTypeModel
import com.zobaer53.zedmovies.domain.model.GenreMatchModel
import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.model.SortModel
import com.zobaer53.zedmovies.domain.model.TvStatusModel
import com.zobaer53.zedmovies.domain.model.TvTypeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class SearchFiltersMapperTest {
    private val today = LocalDate.of(2026, 10, 14)

    @Test
    fun `default filters only sort by popularity and exclude adult titles`() {
        val expected = mapOf("include_adult" to "false", "sort_by" to "popularity.desc")
        assertEquals(expected, SearchFiltersModel().asDiscoverMovieQuery(today))
        assertEquals(expected, SearchFiltersModel().asDiscoverTvQuery(today))
    }

    @Test
    fun `genres map to the media type's ids`() {
        val filters = SearchFiltersModel(genres = setOf(FilterGenreModel.SCIENCE_FICTION, FilterGenreModel.DRAMA))
        assertEquals(setOf("878", "18"), filters.asDiscoverMovieQuery(today).genreIds("|"))
        assertEquals(setOf("10765", "18"), filters.asDiscoverTvQuery(today).genreIds("|"))
    }

    @Test
    fun `match all joins genres with commas and merged TV genres appear once`() {
        val filters = SearchFiltersModel(
            genres = setOf(FilterGenreModel.ACTION, FilterGenreModel.ADVENTURE),
            genreMatch = GenreMatchModel.ALL
        )
        assertEquals(setOf("28", "12"), filters.asDiscoverMovieQuery(today).genreIds(","))
        assertEquals("10759", filters.asDiscoverTvQuery(today)["with_genres"])
    }

    @Test
    fun `genres a media type lacks are left out`() {
        val filters = SearchFiltersModel(genres = setOf(FilterGenreModel.HORROR, FilterGenreModel.COMEDY))
        assertEquals("35", filters.asDiscoverTvQuery(today)["with_genres"])
        assertFalse("with_genres" in SearchFiltersModel(genres = setOf(FilterGenreModel.HORROR)).asDiscoverTvQuery(today))
    }

    @Test
    fun `unmatchable genres depend on the genre match`() {
        val horror = SearchFiltersModel(genres = setOf(FilterGenreModel.HORROR))
        assertEquals(setOf(FilterGenreModel.HORROR), horror.unmatchableGenres(FilterMediaTypeModel.TV_SHOW))
        assertTrue(horror.unmatchableGenres(FilterMediaTypeModel.MOVIE).isEmpty())

        val horrorOrComedy = horror.copy(genres = setOf(FilterGenreModel.HORROR, FilterGenreModel.COMEDY))
        assertTrue(horrorOrComedy.unmatchableGenres(FilterMediaTypeModel.TV_SHOW).isEmpty())
        assertEquals(
            setOf(FilterGenreModel.HORROR),
            horrorOrComedy.copy(genreMatch = GenreMatchModel.ALL).unmatchableGenres(FilterMediaTypeModel.TV_SHOW)
        )
    }

    @Test
    fun `year range becomes date bounds per media type`() {
        val filters = SearchFiltersModel(yearFrom = 2015, yearTo = 2024)
        val movie = filters.asDiscoverMovieQuery(today)
        assertEquals("2015-01-01", movie["primary_release_date.gte"])
        assertEquals("2024-12-31", movie["primary_release_date.lte"])
        val tv = filters.asDiscoverTvQuery(today)
        assertEquals("2015-01-01", tv["first_air_date.gte"])
        assertEquals("2024-12-31", tv["first_air_date.lte"])
    }

    @Test
    fun `newest first stops at today unless a later year is set`() {
        val newest = SearchFiltersModel(sort = SortModel.RELEASE_DATE_DESC)
        assertEquals("primary_release_date.desc", newest.asDiscoverMovieQuery(today)["sort_by"])
        assertEquals("2026-10-14", newest.asDiscoverMovieQuery(today)["primary_release_date.lte"])
        assertEquals("first_air_date.desc", newest.asDiscoverTvQuery(today)["sort_by"])
        assertEquals("2027-12-31", newest.copy(yearTo = 2027).asDiscoverMovieQuery(today)["primary_release_date.lte"])
    }

    @Test
    fun `rating sort adds a vote floor`() {
        val filters = SearchFiltersModel(sort = SortModel.RATING_DESC)
        assertEquals("200", filters.asDiscoverMovieQuery(today)["vote_count.gte"])
        assertEquals("100", filters.asDiscoverTvQuery(today)["vote_count.gte"])
        assertFalse("vote_count.gte" in SearchFiltersModel().asDiscoverMovieQuery(today))
    }

    @Test
    fun `minimum rating and original language are passed through`() {
        val query = SearchFiltersModel(minRating = 7.0, originalLanguage = "ko").asDiscoverTvQuery(today)
        assertEquals("7.0", query["vote_average.gte"])
        assertEquals("20", query["vote_count.gte"])
        assertEquals("ko", query["with_original_language"])
    }

    @Test
    fun `title sort uses the media type's field`() {
        val filters = SearchFiltersModel(sort = SortModel.TITLE_ASC)
        assertEquals("title.asc", filters.asDiscoverMovieQuery(today)["sort_by"])
        assertEquals("name.asc", filters.asDiscoverTvQuery(today)["sort_by"])
    }

    @Test
    fun `minimum votes replaces the hidden vote floor`() {
        val filters = SearchFiltersModel(sort = SortModel.RATING_DESC, minVotes = 1000)
        assertEquals("1000", filters.asDiscoverMovieQuery(today)["vote_count.gte"])
        assertEquals("50", SearchFiltersModel(minVotes = 50).asDiscoverTvQuery(today)["vote_count.gte"])
    }

    @Test
    fun `runtime, origin country and excluded genres apply to both types`() {
        val filters = SearchFiltersModel(
            runtimeMin = 90,
            runtimeMax = 150,
            originCountry = "KR",
            excludedGenres = setOf(FilterGenreModel.HORROR, FilterGenreModel.SCIENCE_FICTION)
        )
        val movie = filters.asDiscoverMovieQuery(today)
        assertEquals("90", movie["with_runtime.gte"])
        assertEquals("150", movie["with_runtime.lte"])
        assertEquals("KR", movie["with_origin_country"])
        assertEquals(setOf("27", "878"), movie.getValue("without_genres").split(",").toSet())
        // Horror has no TV id, so only Sci-Fi & Fantasy is excluded on TV.
        assertEquals("10765", filters.asDiscoverTvQuery(today)["without_genres"])
    }

    @Test
    fun `age ratings apply to movies only and need their country`() {
        val filters = SearchFiltersModel(certifications = setOf("PG", "PG-13"), certificationCountry = "US")
        val movie = filters.asDiscoverMovieQuery(today)
        assertEquals(setOf("PG", "PG-13"), movie.getValue("certification").split("|").toSet())
        assertEquals("US", movie["certification_country"])
        assertFalse("certification" in filters.asDiscoverTvQuery(today))
        assertFalse("certification" in filters.copy(certificationCountry = null).asDiscoverMovieQuery(today))
    }

    @Test
    fun `streaming services apply to both types with their region`() {
        val filters = SearchFiltersModel(watchProviders = setOf(8, 9), watchRegion = "US")
        for (query in listOf(filters.asDiscoverMovieQuery(today), filters.asDiscoverTvQuery(today))) {
            assertEquals(setOf("8", "9"), query.getValue("with_watch_providers").split("|").toSet())
            assertEquals("US", query["watch_region"])
        }
    }

    @Test
    fun `TV network, status and type apply to TV only`() {
        val filters = SearchFiltersModel(
            tvNetworks = setOf(213, 49),
            tvStatuses = setOf(TvStatusModel.RETURNING, TvStatusModel.ENDED),
            tvTypes = setOf(TvTypeModel.MINISERIES)
        )
        val tv = filters.asDiscoverTvQuery(today)
        assertEquals(setOf("213", "49"), tv.getValue("with_networks").split("|").toSet())
        assertEquals(setOf("0", "3"), tv.getValue("with_status").split("|").toSet())
        assertEquals("2", tv["with_type"])
        val movie = filters.asDiscoverMovieQuery(today)
        assertFalse("with_networks" in movie || "with_status" in movie || "with_type" in movie)
    }

    private fun Map<String, String>.genreIds(separator: String) =
        getValue("with_genres").split(separator).toSet()
}
