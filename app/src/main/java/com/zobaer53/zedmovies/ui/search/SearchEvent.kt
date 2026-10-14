package com.zobaer53.zedmovies.ui.search

import com.zobaer53.zedmovies.data.model.ActiveFilter
import com.zobaer53.zedmovies.data.model.SearchFilters

sealed interface SearchEvent {
    data class ChangeQuery(val value: String) : SearchEvent
    data class ApplyFilters(val filters: SearchFilters) : SearchEvent
    data class RemoveFilter(val filter: ActiveFilter) : SearchEvent

    object ResetFilters : SearchEvent
    object LoadFilterOptions : SearchEvent
    object Refresh : SearchEvent
    object Retry : SearchEvent
    object ClearError : SearchEvent
}
