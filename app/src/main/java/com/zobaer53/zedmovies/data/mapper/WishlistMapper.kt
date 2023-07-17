

package com.zobaer53.zedmovies.data.mapper

import com.zobaer53.zedmovies.data.database.model.wishlist.WishlistEntity
import com.zobaer53.zedmovies.domain.model.WishlistModel

internal fun WishlistEntity.asWishlistModel() = WishlistModel(
    id = networkId,
    mediaType = mediaType.asMediaTypeModel()
)
