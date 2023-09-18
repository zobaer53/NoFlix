package com.zobaer53.zedmovies.ui.webview

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import io.github.edsuns.adfilter.AdFilter


class WebViewActivity : ComponentActivity()  {
        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
            val filter = AdFilter.create(this)
            val movieName = intent.getStringExtra("movieName") ?: ""
            val movieYear = intent.getStringExtra("movieYear") ?: ""
            val type = intent.getStringExtra("type") ?: ""
            val replacedString = movieName.replace(" ", "-").lowercase()
        setContent {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val websiteUrl = "https://sflix.to/search/"
                    backgroundTaskScreen(websiteUrl,replacedString,movieYear,type,this).apply {
                        if(this.isNotEmpty()){
                            VideoWebView(url = this, lifecycleOwner =WebViewActivity())
                        }
                    }

                }
        }
    }
}

