package com.zobaer53.zedmovies.data.model

import androidx.annotation.StringRes
import com.zobaer53.zedmovies.R
import kotlinx.serialization.Serializable

/** The Search tab's filters (UI copy of `SearchFiltersModel`); serializable for the saved state. */
@Serializable
data class SearchFilters(
    val sort: FilterSort = FilterSort.POPULAR,
    val genres: Set<FilterGenre> = emptySet(),
    val matchAllGenres: Boolean = false,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val minRating: Double? = null,
    val originalLanguage: String? = null,
    val minVotes: Int? = null,
    val runtimeMin: Int? = null,
    val runtimeMax: Int? = null,
    val originCountry: String? = null,
    val excludedGenres: Set<FilterGenre> = emptySet(),
    val certifications: Set<String> = emptySet(),
    val certificationCountry: String? = null,
    val watchProviders: Set<WatchProvider> = emptySet(),
    val watchRegion: String? = null,
    val tvNetworks: Set<FilterTvNetwork> = emptySet(),
    val tvStatuses: Set<FilterTvStatus> = emptySet(),
    val tvTypes: Set<FilterTvType> = emptySet()
) {
    val isActive: Boolean get() = this != SearchFilters()

    /** Filters that search results can't be checked against (see `SearchFiltersModel.matches`). */
    val hasSearchIgnoredFilters: Boolean
        get() = sort != FilterSort.POPULAR || runtimeMin != null || runtimeMax != null ||
            originCountry != null || certifications.isNotEmpty() || watchProviders.isNotEmpty() ||
            tvNetworks.isNotEmpty() || tvStatuses.isNotEmpty() || tvTypes.isNotEmpty()

    /** Anything set in the sheet's "More filters" section. */
    val hasMoreFilters: Boolean
        get() = minVotes != null || runtimeMin != null || runtimeMax != null ||
            originCountry != null || excludedGenres.isNotEmpty() || certifications.isNotEmpty() ||
            watchProviders.isNotEmpty() || tvNetworks.isNotEmpty() || tvStatuses.isNotEmpty() ||
            tvTypes.isNotEmpty()

    /** One entry per removable chip, in display order. */
    val activeFilters: List<ActiveFilter>
        get() = buildList {
            if (sort != FilterSort.POPULAR) add(ActiveFilter.Sort(sort))
            genres.sorted().forEach { add(ActiveFilter.Genre(it)) }
            if (yearFrom != null || yearTo != null) add(ActiveFilter.Years(yearFrom, yearTo))
            minRating?.let { add(ActiveFilter.Rating(it)) }
            originalLanguage?.let { add(ActiveFilter.Language(it)) }
            minVotes?.let { add(ActiveFilter.MinVotes(it)) }
            if (runtimeMin != null || runtimeMax != null) add(ActiveFilter.Runtime(runtimeMin, runtimeMax))
            originCountry?.let { add(ActiveFilter.Country(it)) }
            excludedGenres.sorted().forEach { add(ActiveFilter.ExcludedGenre(it)) }
            certifications.forEach { add(ActiveFilter.Certification(it)) }
            watchProviders.forEach { add(ActiveFilter.Provider(it)) }
            tvNetworks.sorted().forEach { add(ActiveFilter.Network(it)) }
            tvStatuses.sorted().forEach { add(ActiveFilter.TvStatus(it)) }
            tvTypes.sorted().forEach { add(ActiveFilter.TvType(it)) }
        }

    fun without(filter: ActiveFilter) = when (filter) {
        is ActiveFilter.Sort -> copy(sort = FilterSort.POPULAR)
        is ActiveFilter.Genre -> copy(genres = genres - filter.genre)
        is ActiveFilter.Years -> copy(yearFrom = null, yearTo = null)
        is ActiveFilter.Rating -> copy(minRating = null)
        is ActiveFilter.Language -> copy(originalLanguage = null)
        is ActiveFilter.MinVotes -> copy(minVotes = null)
        is ActiveFilter.Runtime -> copy(runtimeMin = null, runtimeMax = null)
        is ActiveFilter.Country -> copy(originCountry = null)
        is ActiveFilter.ExcludedGenre -> copy(excludedGenres = excludedGenres - filter.genre)
        is ActiveFilter.Certification -> copy(certifications = certifications - filter.code)
        is ActiveFilter.Provider -> copy(watchProviders = watchProviders - filter.provider)
        is ActiveFilter.Network -> copy(tvNetworks = tvNetworks - filter.network)
        is ActiveFilter.TvStatus -> copy(tvStatuses = tvStatuses - filter.status)
        is ActiveFilter.TvType -> copy(tvTypes = tvTypes - filter.type)
    }.normalized()

    /** Drops settings that only make sense alongside others. */
    fun normalized() = copy(
        matchAllGenres = matchAllGenres && genres.size > 1,
        certificationCountry = certificationCountry.takeIf { certifications.isNotEmpty() },
        watchRegion = watchRegion.takeIf { watchProviders.isNotEmpty() }
    )
}

sealed interface ActiveFilter {
    data class Sort(val sort: FilterSort) : ActiveFilter
    data class Genre(val genre: FilterGenre) : ActiveFilter
    data class Years(val from: Int?, val to: Int?) : ActiveFilter
    data class Rating(val minRating: Double) : ActiveFilter
    data class Language(val code: String) : ActiveFilter
    data class MinVotes(val minVotes: Int) : ActiveFilter
    data class Runtime(val min: Int?, val max: Int?) : ActiveFilter
    data class Country(val code: String) : ActiveFilter
    data class ExcludedGenre(val genre: FilterGenre) : ActiveFilter
    data class Certification(val code: String) : ActiveFilter
    data class Provider(val provider: WatchProvider) : ActiveFilter
    data class Network(val network: FilterTvNetwork) : ActiveFilter
    data class TvStatus(val status: FilterTvStatus) : ActiveFilter
    data class TvType(val type: FilterTvType) : ActiveFilter
}

/** A streaming service, as TMDB lists it for the watch region. */
@Serializable
data class WatchProvider(val id: Int, val name: String, val logoUrl: String?)

/** TMDB data the sheet offers as choices, for one country/region. */
data class FilterOptions(
    val certificationCountry: String,
    val certifications: List<Certification>,
    val watchRegion: String,
    val watchProviders: List<WatchProvider>
)

data class Certification(val code: String, val meaning: String)

/** Popular TV networks (TMDB has no list endpoint); names as TMDB spells them. */
enum class FilterTvNetwork(val id: Int, val displayName: String) {
    NETFLIX(213, "Netflix"),
    PRIME_VIDEO(1024, "Prime Video"),
    DISNEY_PLUS(2739, "Disney+"),
    HBO(49, "HBO"),
    HBO_MAX(3186, "HBO Max"),
    APPLE_TV(2552, "Apple TV"),
    HULU(453, "Hulu"),
    PARAMOUNT_PLUS(4330, "Paramount+"),
    PEACOCK(3353, "Peacock"),
    CRUNCHYROLL(1112, "Crunchyroll"),
    BBC_ONE(4, "BBC One"),
    AMC(174, "AMC"),
    FX(88, "FX"),
    ABC(2, "ABC"),
    NBC(6, "NBC"),
    CBS(16, "CBS"),
    FOX(19, "FOX"),
    TVN(866, "tvN"),
    JTBC(885, "JTBC"),
    KBS2(342, "KBS2"),
    MBC(97, "MBC"),
    SBS(156, "SBS")
}

/** Same constants as `TvStatusModel`. */
enum class FilterTvStatus(@StringRes val labelResourceId: Int) {
    RETURNING(R.string.filters_tv_status_returning),
    PLANNED(R.string.filters_tv_status_planned),
    IN_PRODUCTION(R.string.filters_tv_status_in_production),
    ENDED(R.string.filters_tv_status_ended),
    CANCELLED(R.string.filters_tv_status_cancelled),
    PILOT(R.string.filters_tv_status_pilot)
}

/** Same constants as `TvTypeModel`. */
enum class FilterTvType(@StringRes val labelResourceId: Int) {
    SCRIPTED(R.string.filters_tv_type_scripted),
    MINISERIES(R.string.filters_tv_type_miniseries),
    DOCUMENTARY(R.string.filters_tv_type_documentary),
    REALITY(R.string.filters_tv_type_reality),
    TALK_SHOW(R.string.filters_tv_type_talk_show),
    NEWS(R.string.filters_tv_type_news),
    VIDEO(R.string.filters_tv_type_video)
}

enum class FilterSort(@StringRes val labelResourceId: Int) {
    POPULAR(R.string.filters_sort_popular),
    TOP_RATED(R.string.filters_sort_top_rated),
    NEWEST(R.string.filters_sort_newest),
    OLDEST(R.string.filters_sort_oldest),
    TITLE(R.string.filters_sort_title)
}

/** Same constants as `FilterGenreModel`. */
enum class FilterGenre(@StringRes val nameResourceId: Int) {
    ACTION(R.string.action),
    ADVENTURE(R.string.adventure),
    ANIMATION(R.string.animation),
    COMEDY(R.string.comedy),
    CRIME(R.string.crime),
    DOCUMENTARY(R.string.documentary),
    DRAMA(R.string.drama),
    FAMILY(R.string.family),
    FANTASY(R.string.fantasy),
    HISTORY(R.string.history),
    HORROR(R.string.horror),
    KIDS(R.string.kids),
    MUSIC(R.string.music),
    MYSTERY(R.string.mystery),
    NEWS(R.string.news),
    REALITY(R.string.reality),
    ROMANCE(R.string.romance),
    SCIENCE_FICTION(R.string.science_fiction),
    SOAP(R.string.soap),
    TALK(R.string.talk),
    THRILLER(R.string.thriller),
    TV_MOVIE(R.string.tv_movie),
    WAR(R.string.war),
    WESTERN(R.string.western)
}
