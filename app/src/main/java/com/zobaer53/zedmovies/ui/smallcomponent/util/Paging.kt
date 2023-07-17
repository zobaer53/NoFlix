
package com.zobaer53.zedmovies.ui.smallcomponent.util

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

val LoadState.isLoading: Boolean get() = this is LoadState.Loading
val LoadState.isFinished: Boolean get() = this is LoadState.NotLoading
val LoadState.isError: Boolean get() = this is LoadState.Error

val LoadState.error: Throwable
    get() = (this as LoadState.Error).error

fun <T : Any> LazyPagingItems<T>.isEmpty() =
    loadState.append.endOfPaginationReached && itemCount == 0

fun <T : Any> LazyPagingItems<T>.isNotEmpty() = itemCount > 0
