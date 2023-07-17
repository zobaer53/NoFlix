package com.zobaer53.zedmovies.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zobaer53.zedmovies.ui.home.navigation.HomeDestination
import com.zobaer53.zedmovies.ui.search.navigation.SearchDestination
import com.zobaer53.zedmovies.ui.wishlist.navigation.WishlistDestination
import com.zobaer53.zedmovies.R

enum class TopLevelDestination(
    override val route: String,
    override val destination: String,
    @DrawableRes val iconResourceId: Int,
    @StringRes val textResourceId: Int
) : zedMoviesNavigationDestination {
    Home(
        route = HomeDestination.route,
        destination = HomeDestination.destination,
        iconResourceId = R.drawable.ic_home,
        textResourceId = R.string.home
    ),
    Search(
        route = SearchDestination.route,
        destination = SearchDestination.destination,
        iconResourceId = R.drawable.ic_search,
        textResourceId = R.string.search
    ),
    Wishlist(
        route = WishlistDestination.route,
        destination = WishlistDestination.destination,
        iconResourceId = R.drawable.ic_wishlist,
        textResourceId = R.string.wishlist
    )

}
