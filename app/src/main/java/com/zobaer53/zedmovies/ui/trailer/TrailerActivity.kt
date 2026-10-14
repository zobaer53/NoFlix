package com.zobaer53.zedmovies.ui.trailer

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.zobaer53.zedmovies.R

/**
 * Plays a YouTube trailer in YouTube's embedded player.
 *
 * A separate activity (not a Compose dialog) so it can rotate without being recreated
 * (`configChanges` in the manifest) while the main activity stays portrait. Videos that can't be
 * embedded are handed to the YouTube app or the browser.
 */
class TrailerActivity : ComponentActivity() {

    private lateinit var root: FrameLayout
    private lateinit var playerView: YouTubePlayerView
    private lateinit var closeButton: ImageButton

    private var fullscreenView: View? = null
    private var exitFullscreen: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        val trailerKey = intent.getStringExtra(EXTRA_TRAILER_KEY)
        if (trailerKey.isNullOrBlank()) {
            finish()
            return
        }

        playerView = YouTubePlayerView(this).apply {
            id = R.id.trailer_player
            enableAutomaticInitialization = false
        }
        closeButton = ImageButton(this).apply {
            setImageResource(R.drawable.ic_arrow_back)
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = getString(R.string.close_trailer)
            setOnClickListener { finish() }
        }
        root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            addView(playerView, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, Gravity.CENTER))
            addView(closeButton, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.TOP or Gravity.START))
            // Keep the player 16:9 and inside the screen in landscape too.
            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> fitPlayer() }
        }
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
            insets
        }
        setContentView(root)

        lifecycle.addObserver(playerView)
        playerView.addFullscreenListener(fullscreenListener)
        playerView.initialize(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadOrCueVideo(lifecycle, trailerKey, 0f)
                }

                override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                    onPlayerError(trailerKey, error)
                }
            },
            IFramePlayerOptions.Builder(this)
                .controls(1)
                .fullscreen(1)
                .build()
        )

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                exitFullscreen?.invoke() ?: finish()
            }
        })
    }

    private fun fitPlayer() {
        if (!::playerView.isInitialized) return
        val availableWidth = root.width - root.paddingLeft - root.paddingRight
        val availableHeight = root.height - root.paddingTop - root.paddingBottom
        if (availableWidth <= 0 || availableHeight <= 0) return
        val width = minOf(availableWidth, availableHeight * 16 / 9)
        val params = playerView.layoutParams
        if (params.width != width) {
            params.width = width
            // Changing layout params during layout needs another pass.
            playerView.post { playerView.layoutParams = params }
        }
    }

    private val fullscreenListener = object : FullscreenListener {
        override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
            this@TrailerActivity.fullscreenView = fullscreenView
            this@TrailerActivity.exitFullscreen = exitFullscreen
            playerView.isVisible = false
            closeButton.isVisible = false
            root.setPadding(0, 0, 0, 0)
            root.addView(fullscreenView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))
            systemBars().hide(WindowInsetsCompat.Type.systemBars())
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }

        override fun onExitFullscreen() {
            fullscreenView?.let(root::removeView)
            fullscreenView = null
            exitFullscreen = null
            playerView.isVisible = true
            closeButton.isVisible = true
            systemBars().show(WindowInsetsCompat.Type.systemBars())
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            ViewCompat.requestApplyInsets(root)
        }
    }

    private fun systemBars() = WindowCompat.getInsetsController(window, window.decorView).apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    private fun onPlayerError(trailerKey: String, error: PlayerConstants.PlayerError) {
        when (error) {
            PlayerConstants.PlayerError.VIDEO_NOT_FOUND,
            PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST ->
                Toast.makeText(this, R.string.trailer_unavailable, Toast.LENGTH_SHORT).show()

            // Embedding disabled by the owner, region locks, referrer checks: YouTube itself can
            // usually still play it.
            else -> try {
                startActivity(Intent(Intent.ACTION_VIEW, "$YOUTUBE_WATCH_URL$trailerKey".toUri()))
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(this, R.string.trailer_unavailable, Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }

    companion object {
        private const val EXTRA_TRAILER_KEY = "trailerKey"
        private const val YOUTUBE_WATCH_URL = "https://www.youtube.com/watch?v="

        fun intent(context: Context, trailerKey: String) =
            Intent(context, TrailerActivity::class.java).putExtra(EXTRA_TRAILER_KEY, trailerKey)
    }
}
