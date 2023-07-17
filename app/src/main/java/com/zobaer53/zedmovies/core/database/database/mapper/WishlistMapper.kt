

package com.zobaer53.zedmovies.core.database.mapper

import com.zobaer53.zedmovies.core.database.model.common.MediaType
import com.zobaer53.zedmovies.core.database.model.wishlist.WishlistEntity

internal fun MediaType.Wishlist.asWishlistEntity(id: Int) = WishlistEntity(
    mediaType = this,
    networkId = id
)
