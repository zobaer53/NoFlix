

package com.zobaer53.zedmovies.data.database.model.wishlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zobaer53.zedmovies.core.database.model.common.MediaType
import com.zobaer53.zedmovies.core.database.util.Constants

@Entity(tableName = Constants.Tables.WISHLIST)
data class WishlistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.Fields.ID)
    val id: Int = 0,

    @ColumnInfo(name = Constants.Fields.NETWORK_ID)
    val networkId: Int,

    @ColumnInfo(name = Constants.Fields.MEDIA_TYPE)
    val mediaType: MediaType.Wishlist
)
