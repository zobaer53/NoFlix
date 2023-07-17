
package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.core.database.model.common.MediaType
import com.zobaer53.zedmovies.core.domain.model.MediaTypeModel
import com.zobaer53.zedmovies.core.network.model.common.NetworkMediaType

internal fun MediaTypeModel.Movie.asMediaType() = MediaType.Movie[mediaType]
internal fun MediaTypeModel.TvShow.asMediaType() = MediaType.TvShow[mediaType]

internal fun MediaType.Movie.asNetworkMediaType() = NetworkMediaType.Movie[mediaType]
internal fun MediaType.TvShow.asNetworkMediaType() = NetworkMediaType.TvShow[mediaType]

internal fun MediaType.Wishlist.asMediaTypeModel() = when (this) {
    MediaType.Wishlist.Movie -> MediaTypeModel.Wishlist.Movie
    MediaType.Wishlist.TvShow -> MediaTypeModel.Wishlist.TvShow
}
