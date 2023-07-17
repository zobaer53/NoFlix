

package com.zobaer53.zedmovies.data.database.model.movie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zobaer53.zedmovies.core.database.model.common.MediaType
import com.zobaer53.zedmovies.core.database.util.Constants

@Entity(tableName = Constants.Tables.MOVIES_REMOTE_KEYS)
data class MovieRemoteKeyEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Constants.Fields.ID)
    val id: Int,

    @ColumnInfo(name = Constants.Fields.MEDIA_TYPE)
    val mediaType: MediaType.Movie,

    @ColumnInfo(name = Constants.Fields.PREV_PAGE)
    val prevPage: Int?,

    @ColumnInfo(name = Constants.Fields.NEXT_PAGE)
    val nextPage: Int?
)
