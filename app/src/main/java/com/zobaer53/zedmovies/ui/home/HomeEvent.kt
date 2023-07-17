

package com.zobaer53.zedmovies.ui.home

sealed interface HomeEvent {
    object Refresh : HomeEvent
    object Retry : HomeEvent
    object ClearError : HomeEvent
}
