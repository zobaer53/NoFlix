

package com.zobaer53.zedmovies.core.ui.common

import androidx.annotation.StringRes
import com.zobaer53.zedmovies.R


enum class MediaTab(@StringRes val titleResourceId: Int) {
    Movies(titleResourceId = R.string.movies),
    TvShows(titleResourceId = R.string.tv_shows)
}
