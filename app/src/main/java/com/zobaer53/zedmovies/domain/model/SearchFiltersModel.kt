package com.zobaer53.zedmovies.domain.model

/** Filters for the Search tab, shared by the Movies and TV Shows tabs. */
data class SearchFiltersModel(
    val sort: SortModel = SortModel.POPULARITY_DESC,
    val genres: Set<FilterGenreModel> = emptySet(),
    val genreMatch: GenreMatchModel = GenreMatchModel.ANY,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val minRating: Double? = null,
    val originalLanguage: String? = null,
    /** Replaces the hidden vote floors of the rating sort and the minimum rating. */
    val minVotes: Int? = null,
    /** Minutes; episode length for TV. */
    val runtimeMin: Int? = null,
    val runtimeMax: Int? = null,
    /** ISO 3166-1. */
    val originCountry: String? = null,
    val excludedGenres: Set<FilterGenreModel> = emptySet(),
    /** Movies only; codes of [certificationCountry] (e.g. "PG-13" for "US"). */
    val certifications: Set<String> = emptySet(),
    val certificationCountry: String? = null,
    /** TMDB provider ids, available in [watchRegion]. */
    val watchProviders: Set<Int> = emptySet(),
    val watchRegion: String? = null,
    /** TV only; TMDB network ids. */
    val tvNetworks: Set<Int> = emptySet(),
    val tvStatuses: Set<TvStatusModel> = emptySet(),
    val tvTypes: Set<TvTypeModel> = emptySet()
) {
    val isActive: Boolean get() = this != SearchFiltersModel()

    /**
     * Selected genres that [mediaType] doesn't have on TMDB, when they leave nothing to match:
     * all of them for "match all", or every selected genre for "match any". Empty otherwise.
     */
    fun unmatchableGenres(mediaType: FilterMediaTypeModel): Set<FilterGenreModel> {
        val missing = genres.filterTo(mutableSetOf()) { it.idFor(mediaType) == null }
        return when {
            missing.isEmpty() -> emptySet()
            genreMatch == GenreMatchModel.ALL || missing.size == genres.size -> missing
            else -> emptySet()
        }
    }
}

enum class FilterMediaTypeModel { MOVIE, TV_SHOW }

/** `with_status` values of `/discover/tv`. */
enum class TvStatusModel(val id: Int) {
    RETURNING(0),
    PLANNED(1),
    IN_PRODUCTION(2),
    ENDED(3),
    CANCELLED(4),
    PILOT(5)
}

/** `with_type` values of `/discover/tv`. */
enum class TvTypeModel(val id: Int) {
    DOCUMENTARY(0),
    NEWS(1),
    MINISERIES(2),
    REALITY(3),
    SCRIPTED(4),
    TALK_SHOW(5),
    VIDEO(6)
}

enum class GenreMatchModel { ANY, ALL }

/** Sorts that both `/discover/movie` and `/discover/tv` support. */
enum class SortModel {
    POPULARITY_DESC,
    RATING_DESC,
    RELEASE_DATE_DESC,
    RELEASE_DATE_ASC,
    TITLE_ASC
}

/**
 * A user-facing genre. Movie and TV genre ids differ on TMDB, and some genres exist for only one
 * of them (TV merges Action and Adventure into "Action & Adventure", for example).
 */
enum class FilterGenreModel(val movieId: Int?, val tvId: Int?) {
    ACTION(movieId = 28, tvId = 10759),
    ADVENTURE(movieId = 12, tvId = 10759),
    ANIMATION(movieId = 16, tvId = 16),
    COMEDY(movieId = 35, tvId = 35),
    CRIME(movieId = 80, tvId = 80),
    DOCUMENTARY(movieId = 99, tvId = 99),
    DRAMA(movieId = 18, tvId = 18),
    FAMILY(movieId = 10751, tvId = 10751),
    FANTASY(movieId = 14, tvId = 10765),
    HISTORY(movieId = 36, tvId = null),
    HORROR(movieId = 27, tvId = null),
    KIDS(movieId = null, tvId = 10762),
    MUSIC(movieId = 10402, tvId = null),
    MYSTERY(movieId = 9648, tvId = 9648),
    NEWS(movieId = null, tvId = 10763),
    REALITY(movieId = null, tvId = 10764),
    ROMANCE(movieId = 10749, tvId = null),
    SCIENCE_FICTION(movieId = 878, tvId = 10765),
    SOAP(movieId = null, tvId = 10766),
    TALK(movieId = null, tvId = 10767),
    THRILLER(movieId = 53, tvId = null),
    TV_MOVIE(movieId = 10770, tvId = null),
    WAR(movieId = 10752, tvId = 10768),
    WESTERN(movieId = 37, tvId = 37);

    fun idFor(mediaType: FilterMediaTypeModel) = when (mediaType) {
        FilterMediaTypeModel.MOVIE -> movieId
        FilterMediaTypeModel.TV_SHOW -> tvId
    }
}
