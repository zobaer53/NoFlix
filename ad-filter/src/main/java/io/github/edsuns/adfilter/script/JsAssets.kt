package io.github.edsuns.adfilter.script

import android.content.res.AssetManager

/**
 * Reads the injected JavaScript files from `src/main/assets/adfilter/`.
 * [init] is called by AdFilterImpl before any script is used.
 */
internal object JsAssets {

    private lateinit var assets: AssetManager

    fun init(assets: AssetManager) {
        this.assets = assets
    }

    fun read(name: String): String =
        assets.open("adfilter/$name").bufferedReader(Charsets.UTF_8).use { it.readText() }
}
