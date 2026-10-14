package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.network.model.movie.NetworkMovie
import com.zobaer53.zedmovies.data.network.model.tvshow.NetworkTvShow
import com.zobaer53.zedmovies.domain.model.FilterGenreModel
import com.zobaer53.zedmovies.domain.model.GenreMatchModel
import com.zobaer53.zedmovies.domain.model.SearchFiltersModel
import com.zobaer53.zedmovies.domain.model.SortModel
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchFiltersMatcherTest {
    private val batmanBegins = movie(year = 2005, genreIds = listOf(28, 80, 18), voteAverage = 7.7)
    private val theBatman = movie(year = 2022, genreIds = listOf(80, 9648, 53), voteAverage = 7.6)

    @Test
    fun `no filters match everything`() {
        assertTrue(SearchFiltersModel().matches(batmanBegins))
        assertTrue(SearchFiltersModel().matches(movie(year = null)))
        assertFalse(SearchFiltersModel(sort = SortModel.RATING_DESC).filtersSearchResults)
    }

    @Test
    fun `year range is inclusive and drops undated titles`() {
        val filters = SearchFiltersModel(yearFrom = 2000, yearTo = 2010)
        assertTrue(filters.matches(batmanBegins))
        assertFalse(filters.matches(theBatman))
        assertFalse(filters.matches(movie(year = null)))
        assertTrue(SearchFiltersModel(yearFrom = 2005).matches(batmanBegins))
        assertTrue(SearchFiltersModel(yearTo = 2005).matches(batmanBegins))
    }

    @Test
    fun `only a single year is sent to TMDB`() {
        assertEquals(2005, SearchFiltersModel(yearFrom = 2005, yearTo = 2005).searchYear)
        assertNull(SearchFiltersModel(yearFrom = 2000, yearTo = 2010).searchYear)
        assertNull(SearchFiltersModel(yearFrom = 2005).searchYear)
    }

    @Test
    fun `genres match any or all`() {
        val dramaOrMystery = SearchFiltersModel(genres = setOf(FilterGenreModel.DRAMA, FilterGenreModel.MYSTERY))
        assertTrue(dramaOrMystery.matches(batmanBegins))
        assertTrue(dramaOrMystery.matches(theBatman))
        val crimeAndDrama = SearchFiltersModel(
            genres = setOf(FilterGenreModel.CRIME, FilterGenreModel.DRAMA),
            genreMatch = GenreMatchModel.ALL
        )
        assertTrue(crimeAndDrama.matches(batmanBegins))
        assertFalse(crimeAndDrama.matches(theBatman))
    }

    @Test
    fun `TV genres use TV ids`() {
        val sciFi = SearchFiltersModel(genres = setOf(FilterGenreModel.SCIENCE_FICTION))
        assertTrue(sciFi.matches(tvShow(genreIds = listOf(10765))))
        assertFalse(sciFi.matches(tvShow(genreIds = listOf(878))))
    }

    @Test
    fun `minimum rating and original language`() {
        assertTrue(SearchFiltersModel(minRating = 7.7).matches(batmanBegins))
        assertFalse(SearchFiltersModel(minRating = 7.7).matches(theBatman))
        assertFalse(SearchFiltersModel(originalLanguage = "ja").matches(batmanBegins))
        assertTrue(SearchFiltersModel(originalLanguage = "ja").matches(movie(language = "ja")))
        assertTrue(SearchFiltersModel(originalLanguage = "ko").matches(tvShow(language = "ko")))
    }

    @Test
    fun `minimum votes and excluded genres are checked in the app`() {
        assertTrue(SearchFiltersModel(minVotes = 100).filtersSearchResults)
        assertTrue(SearchFiltersModel(minVotes = 100).matches(movie(voteCount = 100)))
        assertFalse(SearchFiltersModel(minVotes = 100).matches(movie(voteCount = 99)))
        val notCrime = SearchFiltersModel(excludedGenres = setOf(FilterGenreModel.CRIME))
        assertFalse(notCrime.matches(batmanBegins))
        assertTrue(SearchFiltersModel(excludedGenres = setOf(FilterGenreModel.HORROR)).matches(batmanBegins))
    }

    @Test
    fun `discover-only filters don't make search filter in the app`() {
        val filters = SearchFiltersModel(watchProviders = setOf(8), watchRegion = "US", runtimeMin = 90)
        assertFalse(filters.filtersSearchResults)
        assertTrue(filters.matches(batmanBegins))
    }

    private fun movie(
        year: Int? = 2005,
        genreIds: List<Int> = emptyList(),
        voteAverage: Double = 0.0,
        language: String = "en",
        voteCount: Int = 0
    ) = NetworkMovie(
        id = 1,
        title = "",
        overview = "",
        popularity = 0.0,
        releaseDate = year?.let { LocalDate(it, 6, 15) },
        adult = false,
        genreIds = genreIds,
        originalTitle = "",
        originalLanguage = language,
        voteAverage = voteAverage,
        voteCount = voteCount,
        posterPath = null,
        backdropPath = null,
        video = false
    )

    private fun tvShow(genreIds: List<Int> = emptyList(), language: String = "en") = NetworkTvShow(
        id = 1,
        name = "",
        overview = "",
        popularity = 0.0,
        firstAirDate = LocalDate(2020, 1, 1),
        genreIds = genreIds,
        originalName = "",
        originalLanguage = language,
        originCountry = emptyList(),
        voteAverage = 0.0,
        voteCount = 0,
        posterPath = null,
        backdropPath = null
    )
}
