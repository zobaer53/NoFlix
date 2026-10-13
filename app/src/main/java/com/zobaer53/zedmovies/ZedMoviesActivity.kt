
package com.zobaer53.zedmovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.graphics.Color
import android.os.Build
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zobaer53.zedmovies.ui.zedMoviesApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class zedMoviesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // No scrim behind 3-button navigation (API < 35): the app's background shows through,
        // as before edge-to-edge. Icon colors are set by SystemBarsAppearance.
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            zedMoviesApp()
        }
    }
}
