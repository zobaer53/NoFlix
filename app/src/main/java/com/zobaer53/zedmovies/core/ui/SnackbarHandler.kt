
package com.zobaer53.zedmovies.core.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.zobaer53.zedmovies.core.designsystem.component.LocalSnackbarHostState
import com.zobaer53.zedmovies.core.model.UserMessage
import com.zobaer53.zedmovies.core.ui.mapper.asString

@Composable
fun SnackbarUserMessageHandler(
    userMessage: UserMessage?,
    onShowMessage: (String) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current
) {
    if (userMessage == null) return
    val message = userMessage.asString()
    LaunchedEffect(snackbarHostState, userMessage) {
        onShowMessage(message)
        onDismiss()
    }
}
