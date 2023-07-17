

package com.zobaer53.zedmovies.data.database.source

import androidx.paging.PagingSource
import com.zobaer53.zedmovies.data.database.dao.tvshow.TvShowDao
import com.zobaer53.zedmovies.data.database.dao.tvshow.TvShowRemoteKeyDao
import com.zobaer53.zedmovies.data.database.model.common.MediaType
import com.zobaer53.zedmovies.data.database.model.tvshow.TvShowEntity
import com.zobaer53.zedmovies.data.database.model.tvshow.TvShowRemoteKeyEntity
import com.zobaer53.zedmovies.data.database.util.zedMoviesDatabaseTransactionProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TvShowDatabaseDataSource @Inject constructor(
    private val tvShowDao: TvShowDao,
    private val tvShowRemoteKeyDao: TvShowRemoteKeyDao,
    private val transactionProvider: zedMoviesDatabaseTransactionProvider
) {
    fun getByMediaType(mediaType: MediaType.TvShow, pageSize: Int): Flow<List<TvShowEntity>> =
        tvShowDao.getByMediaType(mediaType, pageSize)

    fun getPagingByMediaType(mediaType: MediaType.TvShow): PagingSource<Int, TvShowEntity> =
        tvShowDao.getPagingByMediaType(mediaType)

    suspend fun insertAll(tvShows: List<TvShowEntity>) = tvShowDao.insertAll(tvShows)

    suspend fun deleteByMediaTypeAndInsertAll(
        mediaType: MediaType.TvShow,
        tvShows: List<TvShowEntity>
    ) = transactionProvider.runWithTransaction {
        tvShowDao.deleteByMediaType(mediaType)
        tvShowDao.insertAll(tvShows)
    }

    suspend fun getRemoteKeyByIdAndMediaType(id: Int, mediaType: MediaType.TvShow) =
        tvShowRemoteKeyDao.getByIdAndMediaType(id, mediaType)

    suspend fun handlePaging(
        mediaType: MediaType.TvShow,
        tvShows: List<TvShowEntity>,
        remoteKeys: List<TvShowRemoteKeyEntity>,
        shouldDeleteTvShowsAndRemoteKeys: Boolean
    ) = transactionProvider.runWithTransaction {
        if (shouldDeleteTvShowsAndRemoteKeys) {
            tvShowDao.deleteByMediaType(mediaType)
            tvShowRemoteKeyDao.deleteByMediaType(mediaType)
        }
        tvShowRemoteKeyDao.insertAll(remoteKeys)
        tvShowDao.insertAll(tvShows)
    }
}
