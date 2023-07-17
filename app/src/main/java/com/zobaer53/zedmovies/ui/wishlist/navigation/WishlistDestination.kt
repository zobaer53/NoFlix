

package com.zobaer53.zedmovies.ui.wishlist.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zobaer53.zedmovies.data.model.MediaType
import com.zobaer53.zedmovies.navigation.zedMoviesNavigationDestination
import com.zobaer53.zedmovies.ui.wishlist.WishlistRoute

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
