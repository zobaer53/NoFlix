package com.zobaer53.zedmovies.ui.webview

import android.graphics.Bitmap
import android.util.Log
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.zobaer53.zedmovies.BuildConfig
import io.github.edsuns.adfilter.AdFilter

class MyWebClient(
    private val onUrlChange: (String) -> Unit = {},
    private val onPageLoaded: (WebView, String) -> Unit = { _, _ -> },
    private val onRendererGone: (WebView) -> Unit = {}
) : WebViewClient() {

    private val filter = AdFilter.get()

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val result = filter.shouldIntercept(view!!, request!!)
        // Debug builds log blocked requests; .maestro/run.sh counts them.
        if (result.shouldBlock && BuildConfig.DEBUG) Log.d("NoFlixAdBlock", "blocked ${result.resourceUrl}")
        return result.resourceResponse
    }

    // The site's ad scripts send the whole page to other domains ("click-under" redirects on a
    // tap). The player only ever needs the stream site, so top-level navigations elsewhere are
    // dropped. The first loadUrl() doesn't come through here.
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request == null || !request.isForMainFrame) return false
        val url = request.url.toString()
        if (isStreamSiteUrl(url)) return false
        if (BuildConfig.DEBUG) Log.d("NoFlixAdBlock", "blocked navigation to $url")
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        filter.performScript(view, url)
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        injectStreamPageStyle(view, url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        injectStreamPageStyle(view, url)
        if (view != null && url != null) onPageLoaded(view, url)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        url?.let(onUrlChange)
    }

    // The renderer can die (a Chromium crash, or the system reclaiming its memory). Returning
    // false kills the whole app; returning true keeps it alive, but this WebView is unusable
    // from now on and must be removed and destroyed, which onRendererGone arranges.
    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        Log.w("NoFlixWebView", "renderer gone, crashed=${detail?.didCrash()}")
        view?.let(onRendererGone)
        return true
    }

    private fun injectStreamPageStyle(view: WebView?, url: String?) {
        if (isStreamSiteUrl(url)) view?.evaluateJavascript(STREAM_PAGE_SCRIPT, null)
    }
}

