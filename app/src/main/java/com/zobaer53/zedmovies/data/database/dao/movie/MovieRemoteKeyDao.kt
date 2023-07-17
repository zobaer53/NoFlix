

package com.zobaer53.zedmovies.data.database.dao.movie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zobaer53.zedmovies.data.database.model.common.MediaType
import com.zobaer53.zedmovies.data.database.model.movie.MovieRemoteKeyEntity
import com.zobaer53.zedmovies.data.database.util.Constants.Tables.MOVIES_REMOTE_KEYS

@Dao
interface MovieRemoteKeyDao {
    @Query("SELECT * FROM $MOVIES_REMOTE_KEYS WHERE id = :id AND media_type = :mediaType")
    suspend fun getByIdAndMediaType(id: Int, mediaType: MediaType.Movie): MovieRemoteKeyEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<MovieRemoteKeyEntity>)

    @Query("DELETE FROM $MOVIES_REMOTE_KEYS WHERE media_type = :mediaType")
    suspend fun deleteByMediaType(mediaType: MediaType.Movie)
}
