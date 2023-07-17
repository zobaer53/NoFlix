

package com.zobaer53.zedmovies.ui.designsystem.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.zobaer53.zedmovies.ui.designsystem.theme.zedMoviesTheme

@Composable
fun zedMoviesOutlinedButton(
    @StringRes textResourceId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = zedMoviesTheme.shapes.medium,
    containerColor: Color = zedMoviesTheme.colors.primaryVariant,
    contentColor: Color = zedMoviesTheme.colors.accent,
    borderColor: Color = contentColor,
    textStyle: TextStyle = zedMoviesTheme.typography.medium.h5
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(borderColor))
    ) {
        Text(text = stringResource(id = textResourceId), style = textStyle)
    }
}

@Composable
fun zedMoviesIconButton(
    @DrawableRes iconResourceId: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    zedMoviesIconButtonContent(
        modifier = modifier,
        onClick = onClick
    ) {
        zedMoviesIcon(
            iconResourceId = iconResourceId,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Composable
fun zedMoviesIconButton(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    zedMoviesIconButtonContent(
        modifier = modifier,
        onClick = onClick
    ) {
        zedMoviesIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("ReusedModifierInstance")
@Composable
private fun zedMoviesIconButtonContent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false
    ) {
        IconButton(modifier = modifier, onClick = onClick, content = content)
    }
}
