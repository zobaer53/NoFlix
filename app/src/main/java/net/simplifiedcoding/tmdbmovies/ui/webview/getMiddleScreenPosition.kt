package net.simplifiedcoding.tmdbmovies.ui.webview

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

fun getMiddleScreenPosition(context: Context): Pair<Int, Int> {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)

    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    val middleX = -screenWidth / 2
    val middleY = -screenHeight / 2

    return Pair(middleX, middleY)
}
