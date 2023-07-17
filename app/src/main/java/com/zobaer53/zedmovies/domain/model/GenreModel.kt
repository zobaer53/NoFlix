

package com.zobaer53.zedmovies.domain.model

enum class GenreModel(val genreName: String) {
    ACTION(com.zobaer53.zedmovies.domain.model.ACTION_NAME),
    ADVENTURE(com.zobaer53.zedmovies.domain.model.ADVENTURE_NAME),
    ACTION_ADVENTURE(com.zobaer53.zedmovies.domain.model.ACTION_ADVENTURE_NAME),
    ANIMATION(com.zobaer53.zedmovies.domain.model.ANIMATION_NAME),
    COMEDY(com.zobaer53.zedmovies.domain.model.COMEDY_NAME),
    CRIME(com.zobaer53.zedmovies.domain.model.CRIME_NAME),
    DOCUMENTARY(com.zobaer53.zedmovies.domain.model.DOCUMENTARY_NAME),
    DRAMA(com.zobaer53.zedmovies.domain.model.DRAMA_NAME),
    FAMILY(com.zobaer53.zedmovies.domain.model.FAMILY_NAME),
    FANTASY(com.zobaer53.zedmovies.domain.model.FANTASY_NAME),
    HISTORY(com.zobaer53.zedmovies.domain.model.HISTORY_NAME),
    HORROR(com.zobaer53.zedmovies.domain.model.HORROR_NAME),
    KIDS(com.zobaer53.zedmovies.domain.model.KIDS_NAME),
    MUSIC(com.zobaer53.zedmovies.domain.model.MUSIC_NAME),
    MYSTERY(com.zobaer53.zedmovies.domain.model.MYSTERY_NAME),
    NEWS(com.zobaer53.zedmovies.domain.model.NEWS_NAME),
    REALITY(com.zobaer53.zedmovies.domain.model.REALITY_NAME),
    ROMANCE(com.zobaer53.zedmovies.domain.model.ROMANCE_NAME),
    SCIENCE_FICTION(com.zobaer53.zedmovies.domain.model.SCIENCE_FICTION_NAME),
    SCIENCE_FICTION_FANTASY(com.zobaer53.zedmovies.domain.model.SCIENCE_FICTION_FANTASY_NAME),
    SOAP(com.zobaer53.zedmovies.domain.model.SOAP_NAME),
    TALK(com.zobaer53.zedmovies.domain.model.TALK_NAME),
    THRILLER(com.zobaer53.zedmovies.domain.model.THRILLER_NAME),
    TV_MOVIE(com.zobaer53.zedmovies.domain.model.TV_MOVIE_NAME),
    WAR(com.zobaer53.zedmovies.domain.model.WAR_NAME),
    WAR_POLITICS(com.zobaer53.zedmovies.domain.model.WAR_POLITICS_NAME),
    WESTERN(com.zobaer53.zedmovies.domain.model.WESTERN_NAME);

    companion object {
        private val genres = values().associateBy(com.zobaer53.zedmovies.domain.model.GenreModel::genreName)
        operator fun get(genre: String) = checkNotNull(com.zobaer53.zedmovies.domain.model.GenreModel.Companion.genres[genre]) {
            "${com.zobaer53.zedmovies.domain.model.INVALID_GENRE_MESSAGE} $genre"
        }
    }
}

private const val ACTION_NAME = "action"
private const val ADVENTURE_NAME = "adventure"
private const val ACTION_ADVENTURE_NAME = "action_adventure"
private const val ANIMATION_NAME = "animation"
private const val COMEDY_NAME = "comedy"
private const val CRIME_NAME = "crime"
private const val DOCUMENTARY_NAME = "documentary"
private const val DRAMA_NAME = "drama"
private const val FAMILY_NAME = "family"
private const val FANTASY_NAME = "fantasy"
private const val HISTORY_NAME = "history"
private const val HORROR_NAME = "horror"
private const val KIDS_NAME = "kids"
private const val MUSIC_NAME = "music"
private const val MYSTERY_NAME = "mystery"
private const val NEWS_NAME = "news"
private const val REALITY_NAME = "reality"
private const val ROMANCE_NAME = "romance"
private const val SCIENCE_FICTION_NAME = "science_fiction"
private const val SCIENCE_FICTION_FANTASY_NAME = "science_fiction_fantasy"
private const val SOAP_NAME = "soap"
private const val TALK_NAME = "talk"
private const val THRILLER_NAME = "thriller"
private const val TV_MOVIE_NAME = "tv_movie"
private const val WAR_NAME = "war"
private const val WAR_POLITICS_NAME = "war_politics"
private const val WESTERN_NAME = "western"

private const val INVALID_GENRE_MESSAGE = "Invalid genre."
