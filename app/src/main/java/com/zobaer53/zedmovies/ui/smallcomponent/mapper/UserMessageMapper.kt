
package com.zobaer53.zedmovies.ui.smallcomponent.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.data.model.UserMessage
import com.zobaer53.zedmovies.R
import java.io.IOException

@Composable
fun UserMessage.asString() = stringResource(id = messageResourceId, formatArgs = args)

fun Throwable.asUserMessage() = UserMessage(
    messageResourceId = when (this) {
        is IOException -> R.string.no_internet_connection
        else -> R.string.unknown_error
    }
)
