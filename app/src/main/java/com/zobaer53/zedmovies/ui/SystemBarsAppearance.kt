package com.zobaer53.zedmovies.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * The system bars are transparent (edge-to-edge), so only their icon color is chosen:
 * dark icons over light content, light icons over dark content.
 */
@Stable
class SystemBarsAppearance(private val window: Window?, private val view: View) {
    fun setIconsFor(color: Color) {
        val window = window ?: return
        val lightBackground = color.luminance() > 0.5f
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = lightBackground
            isAppearanceLightNavigationBars = lightBackground
        }
    }
}

@Composable
fun rememberSystemBarsAppearance(): SystemBarsAppearance {
    val view = LocalView.current
    return remember(view) { SystemBarsAppearance(view.context.findActivity()?.window, view) }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
