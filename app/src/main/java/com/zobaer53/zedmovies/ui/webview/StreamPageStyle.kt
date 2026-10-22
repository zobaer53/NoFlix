package com.zobaer53.zedmovies.ui.webview

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Allowed origin rules for WebMessageListener and DocumentStartScript. */
val STREAM_ALLOWED_ORIGIN_RULES = setOf(
    STREAM_BASE_URL,
    "https://*.framextv.cc",
    "https://framextv.com",
    "https://*.framextv.com",
    "https://framextv.xyz",
    "https://*.framextv.xyz",
    "https://framextv.to",
    "https://*.framextv.to",
    "https://framextv.net",
    "https://*.framextv.net"
)

/** The JS object the stream site's pages post its resume data to (see [STREAM_DOCUMENT_START_SCRIPT]). */
const val STREAM_BRIDGE_NAME = "NoFlixBridge"

// Runs in every stream-site frame before the site's own scripts.
// - The site's welcome popup reads its flag once, when its scripts start, so it has to be set
//   before they run (STREAM_PAGE_SCRIPT also sets it, but only after the page loads).
// - The top frame posts the site's resume data to the app whenever it changes. The player
//   saves progress to localStorage, which the top frame posts via the bridge. Posts are coalesced to one a second.
val STREAM_DOCUMENT_START_SCRIPT = """
(function () {
  try {
    localStorage.setItem('framextv_welcome_seen', 'true');
    var p = location.pathname || '';
    if (p.indexOf('/watch/') === 0 || p.indexOf('/embed') === 0) {
      document.documentElement.classList.add('nfx-watch');
    }
  } catch (e) {}
  if (window.top !== window) return;
  var key = '$STREAM_WATCH_PROGRESS_KEY', last = null, timer = 0;
  function post() {
    timer = 0;
    try {
      var bridge = window.$STREAM_BRIDGE_NAME, value = localStorage.getItem(key);
      if (bridge && value && value !== last) { last = value; bridge.postMessage(value); }
    } catch (e) {}
  }
  function schedule() { if (!timer) timer = setTimeout(post, 1000); }
  window.addEventListener('storage', function (e) { if (e.key === key) schedule(); });
  window.addEventListener('framextv:progress_updated', schedule);
  window.addEventListener('load', schedule);
  window.addEventListener('pagehide', post);

  function recordVideoProgress(video) {
    if (!video || !video.duration || isNaN(video.duration)) return;
    var path = location.pathname || '';
    var epMatch = path.match(/\/(?:watch|embed(?:\/tv)?)\/(\d+)\/(\d+)\/(\d+)/);
    var movieMatch = !epMatch && path.match(/\/(?:watch|embed(?:\/movie)?)\/(\d+)/);
    if (!epMatch && !movieMatch) return;

    var mediaType = epMatch ? 'tv' : 'movie';
    var id = epMatch ? epMatch[1] : movieMatch[1];
    var season = epMatch ? parseInt(epMatch[2], 10) : undefined;
    var episode = epMatch ? parseInt(epMatch[3], 10) : undefined;
    var currentTime = video.currentTime || 0;
    var duration = video.duration || 0;
    var completed = duration > 0 && (currentTime / duration >= 0.92);

    try {
      var raw = localStorage.getItem(key);
      var list = raw ? JSON.parse(raw) : [];
      if (!Array.isArray(list)) list = [];

      var entryKey = mediaType === 'tv'
        ? ('tv:' + id + ':s' + (season || 1) + ':e' + (episode || 1))
        : ('movie:' + id);

      var entry = {
        mediaType: mediaType,
        id: id,
        timestamp: currentTime,
        duration: duration,
        completed: completed,
        updatedAt: Date.now()
      };
      if (season !== undefined) entry.season = season;
      if (episode !== undefined) entry.episode = episode;

      var filtered = list.filter(function (it) {
        if (!it || !it.id) return false;
        var k = it.mediaType === 'tv'
          ? ('tv:' + it.id + ':s' + (it.season || 1) + ':e' + (it.episode || 1))
          : ('movie:' + it.id);
        return k !== entryKey;
      });
      filtered.unshift(entry);
      localStorage.setItem(key, JSON.stringify(filtered.slice(0, 30)));
      schedule();
    } catch (e) {}
  }

  function notifyPlaybackActive(video) {
    if (!video || video.tagName !== 'VIDEO') return;
    var r = video.getBoundingClientRect();
    if (video.videoWidth > 0 && video.videoHeight > 0 && r.height > 50 && !video.paused && video.currentTime > 0) {
      try {
        var bridge = window.$STREAM_BRIDGE_NAME;
        if (bridge) {
          bridge.postMessage(JSON.stringify({
            type: 'playback_active',
            videoWidth: video.videoWidth,
            videoHeight: video.videoHeight,
            renderedWidth: Math.round(r.width),
            renderedHeight: Math.round(r.height),
            currentTime: Math.round(video.currentTime * 10) / 10
          }));
        }
      } catch (e) {}
    }
  }

  var lastRecord = 0;
  document.addEventListener('timeupdate', function (e) {
    if (e.target && e.target.tagName === 'VIDEO') {
      notifyPlaybackActive(e.target);
    }
    var now = Date.now();
    if (now - lastRecord < 3000) return;
    lastRecord = now;
    if (e.target && e.target.tagName === 'VIDEO') recordVideoProgress(e.target);
  }, true);
  document.addEventListener('playing', function (e) {
    if (e.target && e.target.tagName === 'VIDEO') {
      notifyPlaybackActive(e.target);
      recordVideoProgress(e.target);
    }
  }, true);
  document.addEventListener('pause', function (e) {
    if (e.target && e.target.tagName === 'VIDEO') recordVideoProgress(e.target);
  }, true);
  document.addEventListener('ended', function (e) {
    if (e.target && e.target.tagName === 'VIDEO') recordVideoProgress(e.target);
  }, true);
})();
"""

// Hides the site's logo header, bottom menu bar and footer, plus the "Cast" and
// "More Like This" sections (tagged nfx-hidden by the script below). On /watch/ and /embed/ pages
// the player is stretched over the whole viewport so the video plays full screen.
// Tailwind's h-screen uses 100vh which collapses to 0 inside Compose-hosted WebViews,
// so explicit 100% height and fixed inset pinning are required.
private const val STREAM_PAGE_CSS = """
header.sticky.top-0,
nav.fixed.bottom-0,
#root footer,
section.nfx-hidden { display: none !important; }

html.nfx-watch, html.nfx-watch body {
  width: 100% !important;
  height: 100% !important;
  position: fixed !important;
  inset: 0 !important;
  margin: 0 !important;
  padding: 0 !important;
  overflow: hidden !important;
  background: #000 !important;
}

html.nfx-watch #root,
html.nfx-watch #root > div,
html.nfx-watch .h-screen,
html.nfx-watch [class*="h-screen"] {
  width: 100% !important;
  height: 100% !important;
  min-height: 100% !important;
  max-height: 100% !important;
  overflow: hidden !important;
  background: #000 !important;
}

html.nfx-watch iframe[data-testid="player-iframe"] {
  position: fixed !important;
  inset: 0 !important;
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
    s.textContent = ${Json.encodeToString(STREAM_PAGE_CSS)};
    (d.head || d.documentElement).appendChild(s);
  }
  function sync() {
    var p = location.pathname;
    d.documentElement.classList.toggle('nfx-watch', p.indexOf('/watch/') === 0 || p.indexOf('/embed') === 0);
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
