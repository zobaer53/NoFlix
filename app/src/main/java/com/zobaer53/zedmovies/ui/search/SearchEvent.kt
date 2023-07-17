
package com.zobaer53.zedmovies.ui.search

sealed interface SearchEvent {
    data class ChangeQuery(val value: String) : SearchEvent

    object Refresh : SearchEvent
    object Retry : SearchEvent
    object ClearError : SearchEvent
}
