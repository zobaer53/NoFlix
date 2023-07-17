

package com.zobaer53.zedmovies.data.database.model.tvshow

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zobaer53.zedmovies.data.database.model.common.MediaType
import com.zobaer53.zedmovies.data.database.util.Constants

@Entity(tableName = Constants.Tables.TV_SHOWS_REMOTE_KEYS)
data class TvShowRemoteKeyEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Constants.Fields.ID)
    val id: Int,

    @ColumnInfo(name = Constants.Fields.MEDIA_TYPE)
    val mediaType: MediaType.TvShow,

    @ColumnInfo(name = Constants.Fields.PREV_PAGE)
    val prevPage: Int?,

    @ColumnInfo(name = Constants.Fields.NEXT_PAGE)
    val nextPage: Int?
)
