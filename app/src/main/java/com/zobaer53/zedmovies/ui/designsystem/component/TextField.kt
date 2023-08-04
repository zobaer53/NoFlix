

package com.zobaer53.zedmovies.ui.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun zedMoviesTextField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes placeholderResourceId: Int,
    @DrawableRes iconResourceId: Int,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit) = {
        Text(text = stringResource(id = placeholderResourceId))
    },
    leadingIcon: @Composable (() -> Unit) = {
        zedMoviesIcon(
            iconResourceId = iconResourceId,
            contentDescription = stringResource(id = placeholderResourceId)
        )
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    shape: Shape = zedMoviesTheme.shapes.extraMedium,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = zedMoviesTheme.colors.primaryVariant,
        unfocusedContainerColor = zedMoviesTheme.colors.primaryVariant,
        disabledContainerColor = zedMoviesTheme.colors.primaryVariant,
        //textColor = zedMoviesTheme.colors.textPrimary,
        cursorColor = zedMoviesTheme.colors.accent,
        selectionColors = TextSelectionColors(
            handleColor = zedMoviesTheme.colors.accent,
            backgroundColor = zedMoviesTheme.colors.accent.copy(
                alpha = TextSelectionColorsBackgroundColorAlpha
            )
        ),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = zedMoviesTheme.colors.textSecondary,
        unfocusedLeadingIconColor = zedMoviesTheme.colors.textSecondary,
        focusedTrailingIconColor = zedMoviesTheme.colors.textSecondary,
        unfocusedTrailingIconColor = zedMoviesTheme.colors.textSecondary,
        //placeholderColor = zedMoviesTheme.colors.textSecondary,
    )
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        shape = shape,
        colors = colors
    )
}

private const val TextSelectionColorsBackgroundColorAlpha = 0.4f
