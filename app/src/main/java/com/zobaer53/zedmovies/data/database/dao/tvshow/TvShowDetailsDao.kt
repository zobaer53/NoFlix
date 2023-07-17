
package com.zobaer53.zedmovies.data.database.dao.tvshow

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zobaer53.zedmovies.core.database.model.tvshow.TvShowDetailsEntity
import com.zobaer53.zedmovies.core.database.util.Constants.Tables.TV_SHOW_DETAILS
import kotlinx.coroutines.flow.Flow

@Dao
interface TvShowDetailsDao {
    @Query("SELECT * FROM $TV_SHOW_DETAILS WHERE id = :id")
    fun getById(id: Int): Flow<TvShowDetailsEntity?>

    @Query("SELECT * FROM $TV_SHOW_DETAILS WHERE id IN (:ids)")
    fun getByIds(ids: List<Int>): Flow<List<TvShowDetailsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tvShowDetails: TvShowDetailsEntity)

    @Query("DELETE FROM $TV_SHOW_DETAILS WHERE id = :id")
    suspend fun deleteById(id: Int)
}
