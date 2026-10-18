
package com.zobaer53.zedmovies.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zobaer53.zedmovies.data.database.converter.CreditsConverter
import com.zobaer53.zedmovies.data.database.converter.ListConverter
import com.zobaer53.zedmovies.data.database.converter.LocalDateConverter
import com.zobaer53.zedmovies.data.database.dao.movie.MovieDao
import com.zobaer53.zedmovies.data.database.dao.movie.MovieDetailsDao
import com.zobaer53.zedmovies.data.database.dao.movie.MovieRemoteKeyDao
import com.zobaer53.zedmovies.data.database.dao.tvshow.TvShowDao
import com.zobaer53.zedmovies.data.database.dao.tvshow.TvShowDetailsDao
import com.zobaer53.zedmovies.data.database.dao.tvshow.TvShowRemoteKeyDao
import com.zobaer53.zedmovies.data.database.dao.watch.LastWatchedEpisodeDao
import com.zobaer53.zedmovies.data.database.dao.wishlist.WishlistDao
import com.zobaer53.zedmovies.data.database.model.movie.MovieDetailsEntity
import com.zobaer53.zedmovies.data.database.model.movie.MovieEntity
import com.zobaer53.zedmovies.data.database.model.movie.MovieRemoteKeyEntity
import com.zobaer53.zedmovies.data.database.model.tvshow.TvShowDetailsEntity
import com.zobaer53.zedmovies.data.database.model.tvshow.TvShowEntity
import com.zobaer53.zedmovies.data.database.model.tvshow.TvShowRemoteKeyEntity
import com.zobaer53.zedmovies.data.database.model.watch.LastWatchedEpisodeEntity
import com.zobaer53.zedmovies.data.database.model.wishlist.WishlistEntity

@Database(
    entities = [
        MovieEntity::class, MovieRemoteKeyEntity::class,
        TvShowEntity::class, TvShowRemoteKeyEntity::class,
        MovieDetailsEntity::class, TvShowDetailsEntity::class,
        WishlistEntity::class, LastWatchedEpisodeEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        // 2 -> 3: nullable trailer_key on movie_details and tv_show_details.
        AutoMigration(from = 2, to = 3),
        // 3 -> 4: last_watched_episodes (resume a TV show at the last opened episode).
        AutoMigration(from = 3, to = 4)
    ]
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

    abstract val lastWatchedEpisodeDao: LastWatchedEpisodeDao

    companion object {
        const val zedMovies_DATABASE = "zedMovies.db"
    }
}
