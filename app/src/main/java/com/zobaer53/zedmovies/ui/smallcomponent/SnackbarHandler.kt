
package com.zobaer53.zedmovies.ui.smallcomponent

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.zobaer53.zedmovies.ui.designsystem.component.LocalSnackbarHostState
import com.zobaer53.zedmovies.data.model.UserMessage
import com.zobaer53.zedmovies.ui.smallcomponent.mapper.asString

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
