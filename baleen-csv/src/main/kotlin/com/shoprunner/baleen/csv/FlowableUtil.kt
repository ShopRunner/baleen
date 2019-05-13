package com.shoprunner.baleen.csv

import com.opencsv.CSVReader
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.DataValue
import com.shoprunner.baleen.TraceLocation
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import java.io.Reader

object FlowableUtil {

    class CsvData(
        val headMap: Map<String, Int>,
        val row: Array<String>,
        val rowNumber: Int,
        override val keys: Set<String>
    ) : Data {

        override fun containsKey(key: String): Boolean = keys.contains(key)

        override fun get(key: String): Any? {
            return getDataValue(key)?.value
        }

        override fun getDataValue(key: String): DataValue<*>? {
            val index = headMap[key] ?: return null
            return DataValue(row[index], rowNumber)
        }

        override fun toString(): String {
            val mapString = headMap.map { entry -> "${entry.key}=${row[entry.value]}" }.joinToString()
            return "CsvData($mapString)"
        }
    }

    fun fromCsvWithHeader(dataTrace: DataTrace, readerSupplier: () -> Reader, delimiter: Char = ',', quote: Char = '"', escape: Char = '\\'): Flowable<Context> {
        val readerFactory = { CSVReader(readerSupplier(), delimiter, quote, escape) }

        val rows = Flowable.using(readerFactory, { Flowable.fromIterable(it) }, { it.close() })
        val head = rows.take(1).cache()
        val rest = rows.skip(1)

        val headSet = head.map { it.toSet() }.cache()
        val headMap = head.map { mapOf(*(it.withIndex().map { Pair(it.value, it.index) }.toTypedArray())) }.cache()

        val trace: DataTrace = dataTrace

        val lineNumbers = IntRange(2, Int.MAX_VALUE)

        val dataFlow = Flowables.zip(headMap.repeat(), rest, lineNumbers.toFlowable(), headSet.repeat(), ::CsvData)
        return Flowables.zip(dataFlow, lineNumbers.toFlowable(), { data, lineNumber -> Context(data, trace + TraceLocation("line $lineNumber", lineNumber)) })
    }
}