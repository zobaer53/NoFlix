package io.github.edsuns.adfilter.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.github.edsuns.adblockclient.AdBlockClient
import io.github.edsuns.adfilter.impl.BinaryDataStore
import io.github.edsuns.adfilter.impl.Constants.KEY_ALREADY_UP_TO_DATE
import io.github.edsuns.adfilter.impl.Constants.KEY_CHECK_LICENSE
import io.github.edsuns.adfilter.impl.Constants.KEY_DOWNLOADED_DATA
import io.github.edsuns.adfilter.impl.Constants.KEY_FILTERS_COUNT
import io.github.edsuns.adfilter.impl.Constants.KEY_FILTER_ID
import io.github.edsuns.adfilter.impl.Constants.KEY_FILTER_NAME
import io.github.edsuns.adfilter.impl.Constants.KEY_RAW_CHECKSUM
import io.github.edsuns.adfilter.util.Checksum
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
 * Created by Edsuns@qq.com on 2021/1/5.
 */
internal class InstallationWorker(context: Context, params: WorkerParameters) : Worker(
    context,
    params
) {
    private val binaryDataStore = BinaryDataStore.of(applicationContext)

    override fun doWork(): Result {
        val id = inputData.getString(KEY_FILTER_ID) ?: return Result.failure()
        val downloadedDataName = inputData.getString(KEY_DOWNLOADED_DATA) ?: return Result.failure()
        val rawChecksum = inputData.getString(KEY_RAW_CHECKSUM) ?: return Result.failure()
        val checkLicense = inputData.getBoolean(KEY_CHECK_LICENSE, false)
        // Streamed: the lists are tens of MB, too big for small heaps as a ByteArray plus String.
        val rawFile = binaryDataStore.fileOf(downloadedDataName)
        var name: String? = null
        var hasLicense = false
        val checksum = try {
            rawFile.inputStream().use { input ->
                Checksum.of(input) { comment ->
                    if (name == null) name = extractTitle(comment)
                    if (!hasLicense) hasLicense = validateLicense(comment)
                }
            }
        } catch (e: IOException) {
            Timber.v(e, "Couldn't read filter: $id")
            return Result.failure()
        }
        // reject filter that doesn't include both checksum and license if checkLicense is true
        if (checksum.checksumIn == null && checkLicense && !hasLicense) {
            Timber.v("Filter is invalid: $id")
            return Result.success()
        }
        Timber.v("Checksum: $rawChecksum, ${checksum.checksumIn}, ${checksum.checksumCalc}, ${checksum.validate()}")
        if (!checksum.validate()) {
            return Result.failure()
        }
        if (checksum.validate(rawChecksum)) {
            Timber.v("Filter is up to date: $id")
            return Result.success(
                workDataOf(
                    KEY_FILTER_NAME to name,
                    KEY_ALREADY_UP_TO_DATE to true
                )
            )
        }
        val filtersCount = try {
            persistFilterData(id, rawFile)
        } catch (e: IOException) {
            Timber.v(e, "Couldn't install filter: $id")
            return Result.failure()
        }
        binaryDataStore.clearData(downloadedDataName)
        return Result.success(
            workDataOf(
                KEY_FILTERS_COUNT to filtersCount,
                KEY_FILTER_NAME to name,
                KEY_RAW_CHECKSUM to checksum.checksumCalc
            )
        )
    }

    private val licenseRegexp = Regex(
        "^\\s*!\\s*licen[sc]e[\\s\\-:]+([\\S ]+)$",
        setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
    )

    /**
     * Check if a comment line is a license.
     * A filter without one is often invalid.
     */
    private fun validateLicense(data: String): Boolean = licenseRegexp.containsMatchIn(data)

    private val titleRegexp = Regex(
        "^\\s*!\\s*title[\\s\\-:]+([\\S ]+)$",
        setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
    )

    private fun extractTitle(data: String): String? = titleRegexp.find(data)?.groupValues?.get(1)

    private fun persistFilterData(id: String, rawFile: File): Int {
        val client = AdBlockClient(id)
        client.loadBasicData(rawFile, true)
        binaryDataStore.saveFile(id) { client.saveProcessedData(it) }
        return client.getFiltersCount()
    }
}