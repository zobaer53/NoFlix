package com.zobaer53.zedmovies.ui.stremio.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.zobaer53.zedmovies.navigation.zedMoviesNavigationDestination
import com.zobaer53.zedmovies.ui.stremio.StremioScreen

object StremioDestination : zedMoviesNavigationDestination {
    override val route = "stremio_route"
    override val destination = "stremio_destination"
}

fun NavGraphBuilder.stremioGraph(
    onBackClick: (() -> Unit)? = null
) = composable(route = StremioDestination.route) {
    StremioScreen(onBackClick = onBackClick)
}

