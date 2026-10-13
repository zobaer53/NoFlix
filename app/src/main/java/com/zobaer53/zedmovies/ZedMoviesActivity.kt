
package com.zobaer53.zedmovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zobaer53.zedmovies.ui.zedMoviesApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class zedMoviesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            zedMoviesApp()
        }
    }
}
