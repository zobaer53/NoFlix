
package com.zobaer53.zedmovies.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zobaer53.zedmovies.core.database.converter.CreditsConverter
import com.zobaer53.zedmovies.core.database.converter.ListConverter
import com.zobaer53.zedmovies.core.database.converter.LocalDateConverter
import com.zobaer53.zedmovies.core.database.dao.movie.MovieDao
import com.zobaer53.zedmovies.core.database.dao.movie.MovieDetailsDao
import com.zobaer53.zedmovies.core.database.dao.movie.MovieRemoteKeyDao
import com.zobaer53.zedmovies.core.database.dao.tvshow.TvShowDao
import com.zobaer53.zedmovies.core.database.dao.tvshow.TvShowDetailsDao
import com.zobaer53.zedmovies.core.database.dao.tvshow.TvShowRemoteKeyDao
import com.zobaer53.zedmovies.core.database.dao.wishlist.WishlistDao
import com.zobaer53.zedmovies.core.database.model.movie.MovieDetailsEntity
import com.zobaer53.zedmovies.core.database.model.movie.MovieEntity
import com.zobaer53.zedmovies.core.database.model.movie.MovieRemoteKeyEntity
import com.zobaer53.zedmovies.core.database.model.tvshow.TvShowDetailsEntity
import com.zobaer53.zedmovies.core.database.model.tvshow.TvShowEntity
import com.zobaer53.zedmovies.core.database.model.tvshow.TvShowRemoteKeyEntity
import com.zobaer53.zedmovies.core.database.model.wishlist.WishlistEntity

@Database(
    entities = [
        MovieEntity::class, MovieRemoteKeyEntity::class,
        TvShowEntity::class, TvShowRemoteKeyEntity::class,
        MovieDetailsEntity::class, TvShowDetailsEntity::class,
        WishlistEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(ListConverter::class, LocalDateConverter::class, CreditsConverter::class)
abstract class zedMoviesDatabase : RoomDatabase() {
    abstract val movieDao: MovieDao
    abstract val tvShowDao: TvShowDao

    abstract val movieRemoteKeyDao: MovieRemoteKeyDao
    abstract val tvShowRemoteKeyDao: TvShowRemoteKeyDao

    abstract val movieDetailsDao: MovieDetailsDao
    abstract val tvShowDetailsDao: TvShowDetailsDao

    abstract val wishlistDao: WishlistDao

    companion object {
        const val zedMovies_DATABASE = "zedMovies.db"
    }
}
