package io.github.edsuns.adfilter.impl

import android.content.Context
import io.github.edsuns.adfilter.impl.Constants.FILE_STORE_DIR
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * Created by Edsuns@qq.com on 2020/10/24.
 */
internal class BinaryDataStore(private val dir: File) {

    init {
        if (!dir.exists() && !dir.mkdirs()) {
            Timber.v("BinaryDataStore: failed to create store dirs")
        }
    }

    fun hasData(name: String): Boolean = File(dir, name).exists()

    fun loadData(name: String): ByteArray =
        File(dir, name).readBytes()

    fun saveData(name: String, byteArray: ByteArray) {
        File(dir, name).writeBytes(byteArray)
    }

    fun clearData(name: String) {
        File(dir, name).delete()
    }

    fun fileOf(name: String): File = File(dir, name)

    /**
     * Lets [write] fill a temporary file and then renames it to [name], so a process killed
     * mid-write never leaves a truncated file behind (native code would crash reading it).
     */
    fun saveFile(name: String, write: (File) -> Unit) {
        val tmp = File(dir, "$name.tmp")
        try {
            write(tmp)
            if (!tmp.renameTo(File(dir, name))) throw IOException("Couldn't save $name")
        } finally {
            tmp.delete()
        }
    }

    companion object {
        /**
         * The filter data store. Workers open it with this instead of through [AdFilterImpl]:
         * creating the singleton in a worker deadlocks (its init waits for a WorkManager query
         * queued behind that worker) and ANRs the next WebView launch.
         */
        fun of(context: Context) = BinaryDataStore(File(context.filesDir, FILE_STORE_DIR))
    }
}