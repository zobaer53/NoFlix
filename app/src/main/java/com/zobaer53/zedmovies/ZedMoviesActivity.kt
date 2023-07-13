
package com.zobaer53.zedmovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.zobaer53.zedmovies.ui.zedMoviesApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class zedMoviesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            zedMoviesApp()
        }
    }
}
