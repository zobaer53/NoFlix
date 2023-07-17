
package com.zobaer53.zedmovies.core.database.dao.movie

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zobaer53.zedmovies.core.database.model.common.MediaType
import com.zobaer53.zedmovies.core.database.model.movie.MovieEntity
import com.zobaer53.zedmovies.core.database.util.Constants.Tables.MOVIES
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM $MOVIES WHERE media_type = :mediaType LIMIT :pageSize")
    fun getByMediaType(mediaType: MediaType.Movie, pageSize: Int): Flow<List<MovieEntity>>

    @Query("SELECT * FROM $MOVIES WHERE media_type = :mediaType")
    fun getPagingByMediaType(mediaType: MediaType.Movie): PagingSource<Int, MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM $MOVIES WHERE media_type = :mediaType")
    suspend fun deleteByMediaType(mediaType: MediaType.Movie)
}
