

package com.zobaer53.zedmovies.data.database.dao.tvshow

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zobaer53.zedmovies.core.database.model.common.MediaType
import com.zobaer53.zedmovies.core.database.model.tvshow.TvShowEntity
import com.zobaer53.zedmovies.core.database.util.Constants.Tables.TV_SHOWS
import kotlinx.coroutines.flow.Flow

@Dao
interface TvShowDao {
    @Query("SELECT * FROM $TV_SHOWS WHERE media_type = :mediaType LIMIT :pageSize")
    fun getByMediaType(mediaType: MediaType.TvShow, pageSize: Int): Flow<List<TvShowEntity>>

    @Query("SELECT * FROM $TV_SHOWS WHERE media_type = :mediaType")
    fun getPagingByMediaType(mediaType: MediaType.TvShow): PagingSource<Int, TvShowEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tvShows: List<TvShowEntity>)

    @Query("DELETE FROM $TV_SHOWS WHERE media_type = :mediaType")
    suspend fun deleteByMediaType(mediaType: MediaType.TvShow)
}
