package com.zobaer53.zedmovies.ui.webview

import android.net.Uri

fun isStreamSiteUrl(url: String?): Boolean =
    url != null && Uri.parse(url).host?.endsWith(Uri.parse(STREAM_BASE_URL).host!!) == true

fun isStreamWatchUrl(url: String?): Boolean =
    isStreamSiteUrl(url) && Uri.parse(url).path?.startsWith("/watch/") == true

// The site's welcome popup reads this flag once, when its scripts start, so it has to be
// set before they run (STREAM_PAGE_SCRIPT also sets it, but only after the page loads).
const val STREAM_DOCUMENT_START_SCRIPT =
    "try { localStorage.setItem('framextv_welcome_seen', 'true'); } catch (e) {}"

// Hides the site's logo header, bottom menu bar and footer, plus the "Cast" and
// "More Like This" sections (tagged nfx-hidden by the script below). On /watch/ pages
// the player iframe is stretched over the whole viewport so the video plays full screen.
private const val STREAM_PAGE_CSS = """
header.sticky.top-0,
nav.fixed.bottom-0,
#root footer,
section.nfx-hidden { display: none !important; }

html.nfx-watch, html.nfx-watch body { overflow: hidden !important; background: #000 !important; }
html.nfx-watch iframe[data-testid="player-iframe"] {
  position: fixed !important;
  inset: 0 !important;
  /* 100% rather than vw/vh: vh resolves to 0 inside the Compose-hosted WebView. */
  width: 100% !important;
  height: 100% !important;
  z-index: 2147483647 !important;
}
"""

// Idempotent: safe to run on every page callback. The site is a SPA, so route
// changes are tracked through history.pushState/replaceState and popstate.
val STREAM_PAGE_SCRIPT = """
(function () {
  try { localStorage.setItem('framextv_welcome_seen', 'true'); } catch (e) {}
  var d = document;
  if (!d.getElementById('nfx-style')) {
    var s = d.createElement('style');
    s.id = 'nfx-style';
    s.textContent = ${org.json.JSONObject.quote(STREAM_PAGE_CSS)};
    (d.head || d.documentElement).appendChild(s);
  }
  function sync() {
    d.documentElement.classList.toggle('nfx-watch', location.pathname.indexOf('/watch/') === 0);
  }
  // These sections have no stable class names, so they are matched by heading text.
  function hideSections() {
    d.querySelectorAll('section h2, section h3').forEach(function (h) {
      if (/^(Cast\b|More Like This)/i.test(h.textContent.trim())) {
        h.closest('section').classList.add('nfx-hidden');
      }
    });
  }
  sync();
  hideSections();
  if (!window.__nfxHooked) {
    new MutationObserver(hideSections).observe(d.documentElement, { childList: true, subtree: true });
    window.__nfxHooked = true;
    ['pushState', 'replaceState'].forEach(function (m) {
      var original = history[m];
      history[m] = function () {
        var result = original.apply(this, arguments);
        sync();
        return result;
      };
    });
    window.addEventListener('popstate', sync);
  }
})();
"""
