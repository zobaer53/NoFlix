

package com.zobaer53.zedmovies.data.network.util

internal object Constants {
    internal const val API_URL = "https://api.themoviedb.org/3/"
    internal const val IMAGE_URL = "https://image.tmdb.org/t/p/w500"
    internal const val LOGO_URL = "https://image.tmdb.org/t/p/w92"
    internal const val API_KEY_QUERY_PARAM = "api_key"
    internal const val PAGE_SIZE = 20
    internal const val DEFAULT_PAGE = 1

    internal object Path {
        internal const val UPCOMING_MOVIE = "movie/upcoming"
        internal const val TOP_RATED_MOVIE = "movie/top_rated"
        internal const val POPULAR_MOVIE = "movie/popular"
        internal const val NOW_PLAYING_MOVIE = "movie/now_playing"
        internal const val DISCOVER_MOVIE = "discover/movie"
        internal const val TRENDING_MOVIE = "trending/movie/day"
        internal const val DETAILS_MOVIE = "movie/{id}"
        internal const val SEARCH_MOVIE = "search/movie"

        internal const val TOP_RATED_TV_SHOW = "tv/top_rated"
        internal const val POPULAR_TV_SHOW = "tv/popular"
        internal const val ON_THE_AIR_TV_SHOW = "tv/on_the_air"
        internal const val DISCOVER_TV_SHOW = "discover/tv"
        internal const val TRENDING_TV_SHOW = "trending/tv/day"
        internal const val DETAILS_TV_SHOW = "tv/{id}"
        internal const val SEARCH_TV_SHOW = "search/tv"

        internal const val MOVIE_CERTIFICATIONS = "certification/movie/list"
        internal const val MOVIE_WATCH_PROVIDERS = "watch/providers/movie"
        internal const val TV_WATCH_PROVIDERS = "watch/providers/tv"
    }

    /** Query parameter names of `/discover/movie` and `/discover/tv`. */
    internal object Discover {
        internal const val SORT_BY = "sort_by"
        internal const val WITH_GENRES = "with_genres"
        internal const val VOTE_AVERAGE_GTE = "vote_average.gte"
        internal const val VOTE_COUNT_GTE = "vote_count.gte"
        internal const val WITH_ORIGINAL_LANGUAGE = "with_original_language"
        internal const val PRIMARY_RELEASE_DATE_GTE = "primary_release_date.gte"
        internal const val PRIMARY_RELEASE_DATE_LTE = "primary_release_date.lte"
        internal const val FIRST_AIR_DATE_GTE = "first_air_date.gte"
        internal const val FIRST_AIR_DATE_LTE = "first_air_date.lte"
        internal const val INCLUDE_ADULT = "include_adult"
        internal const val WITHOUT_GENRES = "without_genres"
        internal const val WITH_RUNTIME_GTE = "with_runtime.gte"
        internal const val WITH_RUNTIME_LTE = "with_runtime.lte"
        internal const val WITH_ORIGIN_COUNTRY = "with_origin_country"
        internal const val CERTIFICATION = "certification"
        internal const val CERTIFICATION_COUNTRY = "certification_country"
        internal const val WITH_WATCH_PROVIDERS = "with_watch_providers"
        internal const val WATCH_REGION = "watch_region"
        internal const val WITH_NETWORKS = "with_networks"
        internal const val WITH_STATUS = "with_status"
        internal const val WITH_TYPE = "with_type"

        /** The only filters `/search/movie` and `/search/tv` take besides the query. */
        internal const val PRIMARY_RELEASE_YEAR = "primary_release_year"
        internal const val FIRST_AIR_DATE_YEAR = "first_air_date_year"

        /** TMDB returns an error for any discover page above this. */
        internal const val MAX_PAGE = 500
    }

    internal object Fields {
        internal const val ID = "id"
        internal const val IMDB_ID = "imdb_id"
        internal const val CAST_ID = "cast_id"
        internal const val CREDIT_ID = "credit_id"
        internal const val SHOW_ID = "show_id"
        internal const val CAST = "cast"
        internal const val CREW = "crew"
        internal const val CREDITS = "credits"
        internal const val VIDEOS = "videos"
        internal const val KEY = "key"
        internal const val SITE = "site"
        internal const val OFFICIAL = "official"
        internal const val CHARACTER = "character"
        internal const val BUDGET = "budget"
        internal const val BELONGS_TO_COLLECTION = "belongs_to_collection"
        internal const val CREATED_BY = "created_by"
        internal const val PRODUCTION_COMPANIES = "production_companies"
        internal const val PRODUCTION_COUNTRIES = "production_countries"
        internal const val PRODUCTION_CODE = "production_code"
        internal const val SPOKEN_LANGUAGES = "spoken_languages"
        internal const val SEASON_NUMBER = "season_number"
        internal const val STATUS = "status"
        internal const val SEASONS = "seasons"
        internal const val TYPE = "type"
        internal const val TAGLINE = "tagline"
        internal const val HOMEPAGE = "homepage"
        internal const val AIR_DATE = "air_date"
        internal const val EPISODE_NUMBER = "episode_number"
        internal const val EPISODE_COUNT = "episode_count"
        internal const val EPISODE_RUN_TIME = "episode_run_time"
        internal const val PAGE = "page"
        internal const val TOTAL_PAGES = "total_pages"
        internal const val RESULTS = "results"
        internal const val TOTAL_RESULTS = "total_results"
        internal const val DATES = "dates"
        internal const val DEPARTMENT = "department"
        internal const val TITLE = "title"
        internal const val NAME = "name"
        internal const val ENGLISH_NAME = "english_name"
        internal const val OVERVIEW = "overview"
        internal const val POPULARITY = "popularity"
        internal const val RELEASE_DATE = "release_date"
        internal const val FIRST_AIR_DATE = "first_air_date"
        internal const val LAST_AIR_DATE = "last_air_date"
        internal const val LAST_EPISODE_TO_AIR = "last_episode_to_air"
        internal const val NEXT_EPISODE_TO_AIR = "next_episode_to_air"
        internal const val ADULT = "adult"
        internal const val KNOWN_FOR_DEPARTMENT = "known_for_department"
        internal const val NETWORKS = "networks"
        internal const val NUMBER_OF_EPISODES = "number_of_episodes"
        internal const val NUMBER_OF_SEASONS = "number_of_seasons"
        internal const val LANGUAGES = "languages"
        internal const val IN_PRODUCTION = "in_production"
        internal const val ORDER = "order"
        internal const val JOB = "job"
        internal const val REVENUE = "revenue"
        internal const val RUNTIME = "runtime"
        internal const val GENDER = "gender"
        internal const val GENRES = "genres"
        internal const val GENRE_IDS = "genre_ids"
        internal const val ORIGINAL_TITLE = "original_title"
        internal const val ORIGINAL_NAME = "original_name"
        internal const val ORIGINAL_LANGUAGE = "original_language"
        internal const val ORIGIN_COUNTRY = "origin_country"
        internal const val VOTE_AVERAGE = "vote_average"
        internal const val VOTE_COUNT = "vote_count"
        internal const val POSTER_PATH = "poster_path"
        internal const val BACKDROP_PATH = "backdrop_path"
        internal const val PROFILE_PATH = "profile_path"
        internal const val LOGO_PATH = "logo_path"
        internal const val STILL_PATH = "still_path"
        internal const val VIDEO = "video"
        internal const val MAXIMUM = "maximum"
        internal const val MINIMUM = "minimum"
        internal const val ISO_3166_1 = "iso_3166_1"
        internal const val ISO_639_1 = "iso_639_1"
        internal const val APPEND_TO_RESPONSE = "append_to_response"
        internal const val INCLUDE_VIDEO_LANGUAGE = "include_video_language"
        internal const val QUERY = "query"
        internal const val LANGUAGE = "language"
        internal const val CERTIFICATIONS = "certifications"
        internal const val CERTIFICATION = "certification"
        internal const val MEANING = "meaning"
        internal const val PROVIDER_ID = "provider_id"
        internal const val PROVIDER_NAME = "provider_name"
        internal const val DISPLAY_PRIORITY = "display_priority"

        internal val DETAILS_APPEND_TO_RESPONSE = buildAppendToResponse(CREDITS, VIDEOS)

        private fun buildAppendToResponse(vararg fields: String) =
            fields.joinToString(separator = APPEND_TO_RESPONSE_SEPARATOR)

        /**
         * TMDB only returns videos in the request language unless more are listed here, and most
         * trailers are in English or have no language ("null").
         */
        internal fun buildIncludeVideoLanguage(language: String) =
            listOf(language, ENGLISH_LANGUAGE, NO_LANGUAGE).distinct()
                .joinToString(separator = APPEND_TO_RESPONSE_SEPARATOR)

        private const val ENGLISH_LANGUAGE = "en"
        private const val NO_LANGUAGE = "null"

        private const val APPEND_TO_RESPONSE_SEPARATOR = ","
    }
}

const val PAGE_SIZE = Constants.PAGE_SIZE
const val DEFAULT_PAGE = Constants.DEFAULT_PAGE

@PublishedApi
internal const val MESSAGE_UNHANDLED_STATE = "Unhandled state."
