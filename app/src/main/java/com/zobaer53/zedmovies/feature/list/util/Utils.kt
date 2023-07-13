
package com.zobaer53.zedmovies.feature.list.util

import androidx.annotation.StringRes
import com.zobaer53.zedmovies.core.model.MediaType
import com.zobaer53.zedmovies.core.model.MediaType.Common.Discover
import com.zobaer53.zedmovies.core.model.MediaType.Common.NowPlaying
import com.zobaer53.zedmovies.core.model.MediaType.Common.Popular
import com.zobaer53.zedmovies.core.model.MediaType.Common.TopRated
import com.zobaer53.zedmovies.core.model.MediaType.Common.Trending
import com.zobaer53.zedmovies.core.model.MediaType.Common.Upcoming
import com.zobaer53.zedmovies.R


@StringRes
internal fun MediaType.Common.asTitleResourceId() = mediaTypesTitleResources.getValue(this)

private val mediaTypesTitleResources = mapOf(
    Upcoming to R.string.upcoming_movies,
    TopRated to R.string.top_rated,
    Popular to R.string.most_popular,
    NowPlaying to R.string.now_playing,
    Discover to R.string.discover,
    Trending to R.string.trending
)
