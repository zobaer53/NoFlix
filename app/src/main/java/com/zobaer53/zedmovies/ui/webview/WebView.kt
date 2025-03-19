package com.zobaer53.zedmovies.ui.webview

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.webkit.WebSettingsCompat
import io.github.edsuns.adfilter.AdFilter

@SuppressLint("SetJavaScriptEnabled", "RequiresFeature")
@Composable
fun VideoWebView(url: String, lifecycleOwner: LifecycleOwner) {
    // Temporarily disable AdFilter to prevent crash
    // val filter = AdFilter.get()
    // val filterViewModel = filter.viewModel
    var rememberUrl by remember { mutableStateOf(url) }
    var isFullScreen by remember { mutableStateOf(false) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Temporarily disable AdFilter setup to prevent crash
                // filter.setupWebView(this)
                // Add filter list subscriptions on first installation.
                /*
                if (!filter.hasInstallation) {
                    val map = mapOf(
                        "AdGuard Base" to "https://filters.adtidy.org/extension/chromium/filters/2.txt",
                        "EasyPrivacy Lite" to "https://filters.adtidy.org/extension/chromium/filters/118_optimized.txt",
                        "AdGuard Tracking Protection" to "https://filters.adtidy.org/extension/chromium/filters/3.txt",
                        "AdGuard Annoyances" to "https://filters.adtidy.org/extension/chromium/filters/14.txt",
                        "AdGuard Chinese" to "https://filters.adtidy.org/extension/chromium/filters/224.txt",
                        "NoCoin Filter List" to "https://filters.adtidy.org/extension/chromium/filters/242.txt"
                    )
                    for ((key, value) in map) {
                        val subscription = filterViewModel.addFilter(key, value)
                        filterViewModel.download(subscription.id)
                    }
                }

                filterViewModel.onDirty.observe(lifecycleOwner) {
                    // Clear cache when there are changes to the filter.
                    // You need to refresh the page manually to make the changes take effect.
                    clearCache(false)
                }
                */

                // Enable JavaScript and other settings
                settings.apply {
                    javaScriptEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    WebSettingsCompat.setForceDark(this, WebSettingsCompat.FORCE_DARK_OFF)
                    cacheMode = WebSettings.LOAD_DEFAULT
                    domStorageEnabled = true
                    databaseEnabled = true
                    setSupportZoom(true)
                    builtInZoomControls = true
                    displayZoomControls = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    
                    // Additional settings for better video playback
                    allowContentAccess = true
                    allowFileAccess = true
                    javaScriptCanOpenWindowsAutomatically = false  // Prevent popups
                    setSupportMultipleWindows(false)  // Prevent new windows
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        super.onShowCustomView(view, callback)
                        // Enter fullscreen mode here
                        isFullScreen = true
                    }

                    override fun onHideCustomView() {
                        super.onHideCustomView()
                        // Exit fullscreen mode here
                        isFullScreen = false
                    }
                }

                // Use our custom WebViewClient that handles external links and blocks ads
                webViewClient = MyWebClient()

                // Load the video URL
                Log.d("VideoWebView", "Loading URL: $url")
                loadUrl(url)
            }
        }
    )
}
