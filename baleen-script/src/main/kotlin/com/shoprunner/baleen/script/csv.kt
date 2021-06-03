package com.shoprunner.baleen.script

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.csv.FlowableUtil
import com.shoprunner.baleen.dataTrace
import java.io.File
import java.io.InputStream

fun csv(filename: String, delimiter: Char = ',', quote: Char = '"', escape: Char = '\\', vararg tags: Pair<String, String>): DataAccess =
    csv(File(filename), delimiter, quote, escape, *tags)

fun csv(file: File, delimiter: Char = ',', quote: Char = '"', escape: Char = '\\', vararg tags: Pair<String, String>): DataAccess =
    csv(file.inputStream(), delimiter, quote, escape, "file" to file.name, *tags)

fun csv(inputStream: InputStream, delimiter: Char = ',', quote: Char = '"', escape: Char = '\\', vararg tags: Pair<String, String>): DataAccess =
    { dataDescription ->
        sequence {
            var headMap = emptyMap<String, Int>()
            CSVReaderBuilder(inputStream.bufferedReader())
                .withCSVParser(
                    CSVParserBuilder()
                        .withSeparator(delimiter)
                        .withQuoteChar(quote)
                        .withEscapeChar(escape)
                        .build()
                )
                .build()
                .asIterable()
                .forEachIndexed { idx, row ->
                    if (idx == 0) {
                        headMap = row.mapIndexed { cidx, col -> col to cidx }.toMap()
                    } else {
                        val data = FlowableUtil.CsvData(headMap, row, headMap.keys)
                        val dataTrace = dataTrace().tag("format" to "csv", *tags)
                        if (dataDescription is DataDescription) {
                            yieldAll(dataDescription.validate(Context(data, dataTrace)).results)
                        } else {
                            yieldAll(dataDescription.validate(dataTrace, data))
                        }
                    }
                }
        }.asIterable()
    }

fun csv(delimiter: Char = ',', quote: Char = '"', escape: Char = '\\', vararg tags: Pair<String, String>): HttpDataAccess = { responseBody ->
    if (responseBody != null) {
        csv(responseBody, delimiter, quote, escape, *tags)
    } else {
        { emptySequence<ValidationResult>().asIterable() }
    }
}
