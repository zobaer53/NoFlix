package com.zobaer53.zedmovies.ui.webview

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.edsuns.adfilter.AdFilter


class WebViewActivity : ComponentActivity()  {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            // Draw edge to edge (including the camera cutout) with system bars hidden.
            WindowCompat.setDecorFitsSystemWindows(window, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            val filter = AdFilter.create(this)
            val movieName = intent.getStringExtra("movieName") ?: ""
            val movieYear = intent.getStringExtra("movieYear") ?: ""
            val type = intent.getStringExtra("type") ?: ""
            val tmdbId = intent.getIntExtra("tmdbId", 0)
            val replacedString = movieName.replace(" ", "-").lowercase()
        setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (tmdbId > 0) {
                        VideoWebView(url = buildStreamUrl(tmdbId, type), lifecycleOwner = this)
                    } else {
                        val websiteUrl = "https://sflix.to/search/"
                        backgroundTaskScreen(websiteUrl,replacedString,movieYear,type,this).apply {
                            if(this.isNotEmpty()){
                                VideoWebView(url = this, lifecycleOwner = this@WebViewActivity)
                            }
                        }
                    }

                }
        }
    }
}

