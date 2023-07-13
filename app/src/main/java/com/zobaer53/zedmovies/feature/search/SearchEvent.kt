
package com.zobaer53.zedmovies.feature.search

sealed interface SearchEvent {
    data class ChangeQuery(val value: String) : SearchEvent

    object Refresh : SearchEvent
    object Retry : SearchEvent
    object ClearError : SearchEvent
}
