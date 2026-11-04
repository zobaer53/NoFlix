package com.zobaer53.zedmovies.ui.webview

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.zobaer53.zedmovies.R
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
    var isFullScreen by remember { mutableStateOf(false) }
    val activity = LocalActivity.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var customView by remember { mutableStateOf<View?>(null) }
    var customViewCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    var pageUrl by remember { mutableStateOf(url) }
    var clearHistoryOnShowPage by remember { mutableStateOf<Int?>(null) }
    var fileChooserCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    val currentOnEpisodeOpened by rememberUpdatedState(onEpisodeOpened)
    val currentOnWatchProgress by rememberUpdatedState(onWatchProgress)
    // Bumped to replace a WebView whose renderer died; see onRendererGone below.
    var webViewGeneration by remember { mutableIntStateOf(0) }
    var rendererCrashes by remember { mutableIntStateOf(0) }

    // The player's "Upload .srt / .vtt" button is an <input type="file">, which a WebView only
    // opens through onShowFileChooser. Any file type is offered: Android has no MIME type for
    // .srt/.ass, so the input's accept list would grey out the files it asks for. The callback
    // must always be answered (null on cancel), or the input stops responding.
    val fileChooser = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        fileChooserCallback?.onReceiveValue(uri?.let { arrayOf(it) })
        fileChooserCallback = null
    }

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

    // A dead renderer leaves its WebView unusable: drop it and build a new one on the page
    // that was open. A page that keeps crashing the renderer closes the player instead.
    fun onRendererGone() {
        if (customView != null) hideCustomView()
        rendererCrashes++
        if (rendererCrashes > MAX_RENDERER_RECOVERIES) {
            activity?.let {
                Toast.makeText(it, R.string.player_renderer_crashed, Toast.LENGTH_LONG).show()
                it.finish()
            }
        } else {
            webViewGeneration++
        }
    }

    // Add filter list subscriptions on first installation (once, not per WebView).
    LaunchedEffect(Unit) {
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
    }

    // Clear the cache when the filters change; the page has to be reloaded to apply them.
    DisposableEffect(lifecycleOwner) {
        val observer = Observer<Any> { webView?.clearCache(false) }
        filterViewModel.onDirty.observe(lifecycleOwner, observer)
        onDispose { filterViewModel.onDirty.removeObserver(observer) }
    }

    key(webViewGeneration) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                if (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                    WebView.setWebContentsDebuggingEnabled(true)
                }
                WebView(context).apply {
                    // Setup AdblockAndroid for this WebView.
                    filter.setupWebView(this)

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
                            this, STREAM_DOCUMENT_START_SCRIPT, STREAM_ALLOWED_ORIGIN_RULES
                        )
                    }
                    // Only the stream site's own origin gets the bridge object.
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) {
                        WebViewCompat.addWebMessageListener(
                            this, STREAM_BRIDGE_NAME, STREAM_ALLOWED_ORIGIN_RULES
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

                        override fun onShowFileChooser(
                            webView: WebView?,
                            filePathCallback: ValueCallback<Array<Uri>>?,
                            fileChooserParams: FileChooserParams?
                        ): Boolean {
                            // Only one picker at a time: cancel a request that is still open.
                            fileChooserCallback?.onReceiveValue(null)
                            fileChooserCallback = filePathCallback
                            return try {
                                fileChooser.launch("*/*")
                                true
                            } catch (e: ActivityNotFoundException) {
                                fileChooserCallback = null
                                false
                            }
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
                        },
                        onRendererGone = { onRendererGone() }
                    )
                    webView = this

                    // The current page: the start URL, or where a crashed WebView was.
                    loadUrl(pageUrl)
                }
            },
            onRelease = { view ->
                if (webView === view) webView = null
                view.destroy()
            }
        )
    }
}

private const val MAX_RENDERER_RECOVERIES = 3
