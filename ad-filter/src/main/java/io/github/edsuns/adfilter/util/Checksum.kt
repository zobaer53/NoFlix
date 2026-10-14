package io.github.edsuns.adfilter.util

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.security.MessageDigest
import java.util.Base64

/**
 * Created by Edsuns@qq.com on 2021/1/7.
 *
 * Reference: [validateChecksum.py](https://hg.adblockplus.org/adblockplus/file/tip/validateChecksum.py)
 */
class Checksum private constructor(val checksumIn: String?, val checksumCalc: String) {

    fun validate() = validate(checksumIn, checksumCalc)

    fun validate(checksumIn: String?) = validate(checksumIn, checksumCalc)

    private fun validate(checksumIn: String?, checksumCalc: String): Boolean =
        checksumIn == null || checksumIn == checksumCalc

    companion object {
        private val checksumRegexp = Regex(
            "^[ \\t\\u000B\\f]*!\\s*checksum[\\s\\-:]+([\\w+/=]+)",
            RegexOption.IGNORE_CASE
        )

        /**
         * Reads a filter list once, line by line, so a list of any size can be checked without
         * holding it in memory. [onComment] gets every `!` comment line (UTF-8, without `\r\n`).
         *
         * The checksum is the MD5 of the list normalized as in validateChecksum.py: every `\r`
         * removed, runs of `\n` collapsed into one, and the first checksum line (with the
         * whitespace-only lines right before it) removed.
         */
        fun of(input: InputStream, onComment: (String) -> Unit = {}): Checksum =
            Normalizer(onComment).apply { read(input) }.result()
    }

    private class Normalizer(private val onComment: (String) -> Unit) {
        private val md5 = MessageDigest.getInstance("MD5")
        private val line = ByteArrayOutputStream()
        // Whitespace-only lines since the last real line: a checksum line right after them
        // removes them too (the pattern starts with `^\s*`).
        private val pending = ByteArrayOutputStream()
        private var atStart = true
        private var checksumIn: String? = null
        private var found = false

        fun read(input: InputStream) {
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                var start = 0
                for (i in 0 until count) {
                    val b = buffer[i]
                    if (b != CR && b != LF) continue
                    line.write(buffer, start, i - start)
                    start = i + 1
                    if (b == LF) endLine(terminated = true)
                }
                line.write(buffer, start, count - start)
            }
            endLine(terminated = false)
            feed(pending)
        }

        fun result() = Checksum(
            checksumIn,
            Base64.getEncoder().withoutPadding().encodeToString(md5.digest())
        )

        private fun endLine(terminated: Boolean) {
            val bytes = line.toByteArray()
            line.reset()
            if (bytes.isEmpty()) {
                // Collapsed into the previous line break, except at the very start.
                if (terminated && atStart) {
                    atStart = false
                    pending.write(LF.toInt())
                }
                return
            }
            atStart = false
            val first = bytes.indexOfFirst { it !in WHITESPACE }
            val comment = first >= 0 && bytes[first] == '!'.code.toByte()
            val text = if (comment) String(bytes, Charsets.UTF_8) else null
            text?.let(onComment)
            when {
                found -> Unit
                first < 0 -> {
                    pending.write(bytes)
                    if (terminated) pending.write(LF.toInt())
                    return
                }
                // The pattern ends with `\n`, so an unterminated last line isn't a checksum line.
                terminated && text != null -> checksumRegexp.find(text)?.let {
                    checksumIn = it.groupValues[1]
                    found = true
                    pending.reset()
                    return
                }
            }
            feed(pending)
            md5.update(bytes)
            if (terminated) md5.update(LF)
        }

        private fun feed(bytes: ByteArrayOutputStream) {
            if (bytes.size() == 0) return
            md5.update(bytes.toByteArray())
            bytes.reset()
        }
    }
}

private const val CR = '\r'.code.toByte()
private const val LF = '\n'.code.toByte()
private val WHITESPACE = byteArrayOf(' '.code.toByte(), '\t'.code.toByte(), 0x0B, 0x0C)
