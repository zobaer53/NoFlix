package io.github.edsuns.adfilter.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.security.MessageDigest
import java.util.Base64

class ChecksumTest {

    /** The original whole-String implementation, which [Checksum] streams. */
    private object Reference {
        val checksumRegexp = Regex(
            "^\\s*!\\s*checksum[\\s\\-:]+([\\w+/=]+).*\\r?\\n",
            setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
        )

        fun checksumIn(data: String) = checksumRegexp.find(data)?.groupValues?.get(1)

        fun checksumCalc(data: String): String {
            var normalize = data.replace("\r", "")
            normalize = Regex("\n+").replace(normalize, "\n")
            normalize = checksumRegexp.replaceFirst(normalize, "")
            val md5 = MessageDigest.getInstance("MD5").digest(normalize.toByteArray())
            return Base64.getEncoder().withoutPadding().encodeToString(md5)
        }
    }

    private fun checksum(data: String, onComment: (String) -> Unit = {}) =
        Checksum.of(data.byteInputStream(), onComment)

    private fun assertMatchesReference(data: String) {
        val checksum = checksum(data)
        assertEquals(data, Reference.checksumIn(data), checksum.checksumIn)
        assertEquals(data, Reference.checksumCalc(data), checksum.checksumCalc)
    }

    @Test
    fun matchesReference() {
        listOf(
            "",
            "\n",
            "\r\n\r\n",
            "||ads.example^",
            "! Checksum: abc\n||ads.example^\n",
            "! Checksum: abc\r\n! Title: T\r\n\r\n||ads.example^\r\n",
            "[Adblock Plus 2.0]\n! Checksum: abc\n! Title: T\n||a^\n",
            "\n\n! Checksum: abc\n||a^",
            "||a^\n   \n\t\n  ! checksum - :x+/=y  trailing\n||b^\n",
            "||a^\n\n\n! Checksum: first\n||b^\n! Checksum: second\n||c^\n",
            "||a^\n! Checksum: last-without-newline",
            "||a^\n! Checksum: abc\n\n\n||b^\n\n",
            "||a^\r\r\n||b^\r\n",
            "! Title: 广告过滤\n##.ad-中文\n",
            "! no checksum here\n||a^  \n  ||b^\n",
        ).forEach(::assertMatchesReference)
    }

    @Test
    fun largeList() {
        val data = buildString {
            append("! Checksum: dnuo+hnu8LvOBFwEWX2kFw\r\n! Title: Big\r\n")
            repeat(200_000) { append("||ads$it.example^\r\n") }
        }
        assertMatchesReference(data)
    }

    @Test
    fun validatesAgainstDeclaredChecksum() {
        val body = "! Title: T\n||a^\n"
        val declared = Reference.checksumCalc(body)
        assertTrue(checksum("! Checksum: $declared\r\n$body").validate())
        assertFalse(checksum("! Checksum: $declared\r\n$body||b^\n").validate())
        assertTrue(checksum(body).validate())
    }

    @Test
    fun reportsCommentLines() {
        val comments = mutableListOf<String>()
        checksum("! Checksum: abc\r\n  ! Title: T\r\n||a^\r\n!x", comments::add)
        assertEquals(listOf("! Checksum: abc", "  ! Title: T", "!x"), comments)
    }
}
