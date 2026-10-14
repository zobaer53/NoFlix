package io.github.edsuns.adfilter.impl

import io.github.edsuns.adblockclient.AdBlockClient
import io.github.edsuns.adfilter.CustomFilter
import timber.log.Timber
import java.io.IOException

/**
 * Created by Edsuns@qq.com on 2020/10/24.
 */
internal class FilterDataLoader(
    val detector: Detector,
    private val binaryDataStore: BinaryDataStore
) {

    fun load(id: String) {
        if (binaryDataStore.hasData(id)) {
            val client = AdBlockClient(id)
            try {
                client.loadProcessedData(binaryDataStore.fileOf(id))
            } catch (e: IOException) {
                Timber.v(e, "Couldn't load client processed data: $id")
                return
            }
            if (id == ID_CUSTOM) {
                detector.customFilterClient = client
            } else {
                detector.addClient(client)
            }
        } else {
            Timber.v("Couldn't find client processed data: $id")
        }
    }

    fun unload(id: String) {
        detector.removeClient(id)
    }

    fun unloadAll() {
        detector.clearAllClient()
    }

    fun remove(id: String) {
        binaryDataStore.clearData(id)
        binaryDataStore.clearData("_$id")
        unload(id)
    }

    fun isCustomFilterEnabled() = detector.customFilterClient != null

    fun getCustomFilter(): CustomFilter {
        if (binaryDataStore.hasData(RAW_CUSTOM)) {
            return CustomFilterImpl(this, String(binaryDataStore.loadData(RAW_CUSTOM)))
        }
        return CustomFilterImpl(this)
    }

    fun loadCustomFilter(rawData: ByteArray) {
        binaryDataStore.saveData(RAW_CUSTOM, rawData)
        val client = AdBlockClient(ID_CUSTOM)
        client.loadBasicData(rawData, true)
        binaryDataStore.saveFile(ID_CUSTOM) { client.saveProcessedData(it) }
        load(ID_CUSTOM)
    }

    fun unloadCustomFilter() {
        detector.customFilterClient = null
    }

    companion object {
        const val RAW_CUSTOM = "_custom"
        const val ID_CUSTOM = "custom"
    }
}