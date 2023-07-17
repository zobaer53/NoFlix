

package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.core.network.util.buildImageUrl

internal fun String.asImageUrl() = buildImageUrl(path = this)
