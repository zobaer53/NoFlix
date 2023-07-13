

package com.zobaer53.zedmovies.feature.wishlist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zobaer53.zedmovies.core.model.MediaType
import com.zobaer53.zedmovies.core.navigation.zedMoviesNavigationDestination
import com.zobaer53.zedmovies.feature.wishlist.WishlistRoute

object WishlistDestination : zedMoviesNavigationDestination {
    override val route = "wishlist_route"
    override val destination = "wishlist_destination"
}

fun NavGraphBuilder.wishlistGraph(
    onNavigateToDetailsDestination: (MediaType.Details) -> Unit
) = composable(route = WishlistDestination.route) {
    WishlistRoute(
        onMovieClick = { onNavigateToDetailsDestination(MediaType.Details.Movie(it)) },
        onTvShowClick = { onNavigateToDetailsDestination(MediaType.Details.TvShow(it)) }
    )
}
