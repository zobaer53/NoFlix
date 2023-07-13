

package com.zobaer53.zedmovies.core.ui.mapper

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun <T : Any, R : Any> Flow<PagingData<T>>.pagingMap(
    transform: (T) -> R
): Flow<PagingData<R>> = map { it.map(transform) }
