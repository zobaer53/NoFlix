

package com.zobaer53.zedmovies.core.database.dao.tvshow

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zobaer53.zedmovies.core.database.model.common.MediaType
import com.zobaer53.zedmovies.core.database.model.tvshow.TvShowRemoteKeyEntity
import com.zobaer53.zedmovies.core.database.util.Constants.Tables.TV_SHOWS_REMOTE_KEYS

@Dao
interface TvShowRemoteKeyDao {
    @Query("SELECT * FROM $TV_SHOWS_REMOTE_KEYS WHERE id = :id AND media_type = :mediaType")
    suspend fun getByIdAndMediaType(id: Int, mediaType: MediaType.TvShow): TvShowRemoteKeyEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<TvShowRemoteKeyEntity>)

    @Query("DELETE FROM $TV_SHOWS_REMOTE_KEYS WHERE media_type = :mediaType")
    suspend fun deleteByMediaType(mediaType: MediaType.TvShow)
}
