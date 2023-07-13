package com.zobaer53.zedmovies.core.ui.webview

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoWebView(url: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Enable JavaScript and other settings
                settings.apply {
                    javaScriptEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                }

                // Set WebViewClient and WebChromeClient
                webViewClient = ContentBlockingWebViewClient(context)
                webChromeClient = WebChromeClient()

                // Load the video URL
                loadUrl(url)
            }
        }
    )
}
