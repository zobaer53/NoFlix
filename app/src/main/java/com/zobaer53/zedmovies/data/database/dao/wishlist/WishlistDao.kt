

package com.zobaer53.zedmovies.data.database.dao.wishlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zobaer53.zedmovies.data.database.model.common.MediaType
import com.zobaer53.zedmovies.data.database.model.wishlist.WishlistEntity
import com.zobaer53.zedmovies.data.database.util.Constants.Tables.WISHLIST
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("SELECT * FROM $WISHLIST WHERE media_type = :mediaType")
    fun getByMediaType(mediaType: MediaType.Wishlist): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wishlist: WishlistEntity)

    @Query("DELETE FROM $WISHLIST WHERE media_type = :mediaType AND network_id = :id")
    suspend fun deleteByMediaTypeAndNetworkId(mediaType: MediaType.Wishlist, id: Int)

    @Query("SELECT EXISTS(SELECT * FROM $WISHLIST WHERE media_type = :mediaType AND network_id = :id)")
    suspend fun isWishlisted(mediaType: MediaType.Wishlist, id: Int): Boolean
}
