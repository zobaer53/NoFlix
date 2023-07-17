

package com.zobaer53.zedmovies.data.database.mapper

import com.zobaer53.zedmovies.data.database.model.common.MediaType
import com.zobaer53.zedmovies.data.database.model.wishlist.WishlistEntity

internal fun MediaType.Wishlist.asWishlistEntity(id: Int) = WishlistEntity(
    mediaType = this,
    networkId = id
)
