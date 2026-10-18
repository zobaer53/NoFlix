package com.zobaer53.zedmovies.ui.webview

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import io.github.edsuns.adfilter.AdFilter

// RestrictedApi: addDocumentStartJavaScript is still marked internal in androidx.webkit 1.4.0;
// the public version needs webkit 1.9+, which requires compileSdk 34.
@SuppressLint("SetJavaScriptEnabled", "RequiresFeature", "RestrictedApi")
@Composable
fun VideoWebView(
    url: String,
    lifecycleOwner: LifecycleOwner,
    onEpisodeOpened: (StreamEpisode) -> Unit = {},
    onWatchProgress: (String) -> Unit = {}
) {
    val filter = AdFilter.get()
    val filterViewModel = filter.viewModel
    var rememberUrl by remember { mutableStateOf(url) }
    var isFullScreen by remember { mutableStateOf(false) }
    val activity = LocalActivity.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var customView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    var pageUrl by remember { mutableStateOf(url) }
    var clearHistoryOnShowPage by remember { mutableStateOf<Int?>(null) }
    val currentOnEpisodeOpened by rememberUpdatedState(onEpisodeOpened)
    val currentOnWatchProgress by rememberUpdatedState(onWatchProgress)

    // Videos (watch pages and full-screen players) play in landscape; every other page is
    // a phone layout, so it's locked to portrait. Unlocking instead would leave the app
    // sideways after Back while the phone is still held that way from watching.
    fun updateOrientation() {
        activity?.requestedOrientation = if (customView != null || isStreamWatchUrl(pageUrl)) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    fun hideCustomView() {
        (activity?.window?.decorView as? FrameLayout)?.removeView(customView)
        customView = null
        customViewCallback?.onCustomViewHidden()
        customViewCallback = null
        isFullScreen = false
        updateOrientation()
    }

    // Back from an episode goes to its show page. Playback can start straight on an episode,
    // and auto-advance adds a history entry per episode, so plain goBack() would step through
    // earlier episodes first. A freshly loaded show page drops the history behind it.
    fun openShowPage(view: WebView, episode: StreamEpisode) {
        val history = view.copyBackForwardList()
        val previousUrl = history.getItemAtIndex(history.currentIndex - 1)?.url
        if (isStreamShowUrl(previousUrl, episode.tvShowId)) {
            view.goBack()
        } else {
            clearHistoryOnShowPage = episode.tvShowId
            view.loadUrl(buildStreamShowUrl(episode.tvShowId, episode.season))
        }
    }

    BackHandler {
        val view = webView
        val episode = parseStreamEpisode(pageUrl)
        when {
            customView != null -> hideCustomView()
            view != null && episode != null -> openShowPage(view, episode)
            view?.canGoBack() == true -> view.goBack()
            else -> activity?.finish()
        }
    }

    // Setup AdblockAndroid for your WebView.

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            if (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                WebView.setWebContentsDebuggingEnabled(true)
            }
            WebView(context).apply {
                filter.setupWebView(this)
                // Add filter list subscriptions on first installation.
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

                // Enable JavaScript and other settings
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    // Never auto-darken the site (setForceDark is a no-op from targetSdk 33).
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                        WebSettingsCompat.setAlgorithmicDarkeningAllowed(this, false)
                    }
                    cacheMode =WebSettings.LOAD_DEFAULT
                }
                if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
                    WebViewCompat.addDocumentStartJavaScript(
                        this, STREAM_DOCUMENT_START_SCRIPT, setOf(STREAM_BASE_URL)
                    )
                }
                // Only the stream site's own origin gets the bridge object.
                if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
                    WebViewCompat.addWebMessageListener(
                        this, STREAM_BRIDGE_NAME, setOf(STREAM_BASE_URL)
                    ) { _, message, _, isMainFrame, _ ->
                        if (isMainFrame) message.data?.let { currentOnWatchProgress(it) }
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        val decor = activity?.window?.decorView as? FrameLayout
                        if (view == null || decor == null || customView != null) {
                            callback?.onCustomViewHidden()
                            return
                        }
                        // Enter fullscreen mode here
                        decor.addView(
                            view,
                            FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                        )
                        customView = view
                        customViewCallback = callback
                        isFullScreen = true
                        updateOrientation()
                    }

                    override fun onHideCustomView() {
                        // Exit fullscreen mode here
                        hideCustomView()
                    }
                }

                webViewClient = MyWebClient(
                    onUrlChange = { newUrl ->
                        pageUrl = newUrl
                        updateOrientation()
                        parseStreamEpisode(newUrl)?.let { currentOnEpisodeOpened(it) }
                    },
                    onPageLoaded = { view, loadedUrl ->
                        val showId = clearHistoryOnShowPage
                        if (showId != null && isStreamShowUrl(loadedUrl, showId)) {
                            view.clearHistory()
                            clearHistoryOnShowPage = null
                        }
                    }
                )
                webView = this

                // Load the video URL
                loadUrl(rememberUrl)
            }
        }
    )
}
