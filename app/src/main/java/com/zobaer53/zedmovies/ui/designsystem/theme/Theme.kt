

package com.zobaer53.zedmovies.ui.designsystem.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun zedMoviesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        shapes = Shapes,
        typography = Typography
    ) { ProvidezedMoviesThemeDependencies(content = content) }
}

@Composable
private fun ProvidezedMoviesThemeDependencies(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalzedMoviesColors provides if (darkTheme) DarkzedMoviesColors else LightzedMoviesColors,
        LocalzedMoviesShapes provides zedMoviesShapes(),
        LocalzedMoviesTypography provides zedMoviesTypography(),
        LocalzedMoviesSpacing provides zedMoviesSpacing()
    ) { ProvideMaterialThemeDependencies(content = content) }
}

@Composable
private fun ProvideMaterialThemeDependencies(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalIndication provides rememberzedMoviesRipple(),
        LocalRippleTheme provides zedMoviesRippleTheme
    ) {
        ProvideTextStyle(value = zedMoviesTheme.typography.regular.h4, content = content)
    }
}

object zedMoviesTheme {
    val colors: zedMoviesColors
        @Composable
        @ReadOnlyComposable
        get() = LocalzedMoviesColors.current

    val shapes: zedMoviesShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalzedMoviesShapes.current

    val typography: zedMoviesTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalzedMoviesTypography.current

    val spacing: zedMoviesSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalzedMoviesSpacing.current
}
