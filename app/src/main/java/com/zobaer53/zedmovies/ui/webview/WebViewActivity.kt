package com.zobaer53.zedmovies.ui.webview

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import io.github.edsuns.adfilter.AdFilter

class WebViewActivity : ComponentActivity() {
    private val TAG = "WebViewActivity"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable WebView debugging in Chrome DevTools
        WebView.setWebContentsDebuggingEnabled(true)
        
        // Enable full screen video
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        
        // Get movie details from intent
        val movieName = intent.getStringExtra("movieName") ?: ""
        val movieYear = intent.getStringExtra("movieYear") ?: ""
        val type = intent.getStringExtra("type") ?: ""
        val replacedString = movieName.replace(" ", "-").lowercase()
        
        // Log movie details
        Log.d(TAG, "Movie Name: $movieName")
        Log.d(TAG, "Movie Year: $movieYear")
        Log.d(TAG, "Type: $type")
        Log.d(TAG, "Replaced String: $replacedString")
        
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val websiteUrl = "https://sflix.to/search/"
                backgroundTaskScreen(websiteUrl, replacedString, movieYear, type, this@WebViewActivity).apply {
                    if (this.isNotEmpty()) {
                        Log.d(TAG, "URL found: $this")
                        VideoWebView(url = this, lifecycleOwner = this@WebViewActivity)
                    } else {
                        Log.d(TAG, "No URL was returned from backgroundTaskScreen")
                    }
                }
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "WebViewActivity paused")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "WebViewActivity resumed")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "WebViewActivity destroyed")
    }
}

