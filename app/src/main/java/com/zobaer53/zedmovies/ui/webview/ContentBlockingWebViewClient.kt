package com.zobaer53.zedmovies.ui.webview

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.zobaer53.zedmovies.BuildConfig
import io.github.edsuns.adfilter.AdFilter

class MyWebClient(private val onUrlChange: (String) -> Unit = {}) : WebViewClient() {

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

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        filter.performScript(view, url)
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        injectStreamPageStyle(view, url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        injectStreamPageStyle(view, url)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        url?.let(onUrlChange)
    }

    private fun injectStreamPageStyle(view: WebView?, url: String?) {
        if (isStreamSiteUrl(url)) view?.evaluateJavascript(STREAM_PAGE_SCRIPT, null)
    }
}

