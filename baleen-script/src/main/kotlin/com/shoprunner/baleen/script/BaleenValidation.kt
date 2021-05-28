package com.shoprunner.baleen.script

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.createSummary
import com.shoprunner.baleen.csv.FlowableUtil
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.groupByTag
import com.shoprunner.baleen.json.JsonUtil
import com.shoprunner.baleen.json.Options
import com.shoprunner.baleen.xml.XmlUtil
import java.io.File
import java.io.InputStream

class BaleenValidation {
    private var validationResults: Sequence<ValidationResult> = emptySequence()

    val results: Sequence<ValidationResult> get() = validationResults

    fun database(body: DatabaseValidationWorker.() -> Unit) {
        val worker = DatabaseValidationWorker()
        worker.use(body)
        validationResults += worker.results
    }

    fun csv(filename: String, delimiter: Char = ',', quote: Char = '"', escape: Char = '\\', description: DataDescription.() -> Unit) =
        csv(File(filename), delimiter, quote, escape, description)

    fun csv(file: File, delimiter: Char = ',', quote: Char = '"', escape: Char = '\\', description: DataDescription.() -> Unit) {
        csv(file.name, file.inputStream(), delimiter, quote, escape, description)
    }

    fun csv(name: String, inputStream: InputStream, delimiter: Char = ',', quote: Char = '"', escape: Char = '\\', description: DataDescription.() -> Unit) {
        val dataDescription = name.describeAs(description = description)

        val results = sequence {
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
                        val dataTrace = dataTrace().tag("file" to name)
                        yieldAll(dataDescription.validate(Context(data, dataTrace)).results)
                    }
                }
        }

        validationResults += results.createSummary(groupBy = groupByTag("file"))
    }

    fun json(filename: String, options: Options = Options(), description: DataDescription.() -> Unit) =
        json(File(filename), options, description)

    fun json(file: File, options: Options = Options(), description: DataDescription.() -> Unit) {
        json(file.name, file.inputStream(), options, description)
    }

    fun json(name: String, inputStream: InputStream, options: Options = Options(), description: DataDescription.() -> Unit) {
        val dataDescription = name.describeAs(description = description)
        validationResults += JsonUtil.validate(
            dataDescription,
            dataTrace().tag("file" to name),
            inputStream,
            options
        )
            .results
            .createSummary(groupBy = groupByTag("file"))
    }

    fun xml(filename: String, description: DataDescription.() -> Unit) =
        xml(File(filename), description)

    fun xml(file: File, description: DataDescription.() -> Unit) {
        xml(file.name, file.inputStream(), description)
    }

    fun xml(name: String, inputStream: InputStream, description: DataDescription.() -> Unit) {
        val root = Baleen.describe("root", description = description)
        val context = XmlUtil.fromXmlToContext(DataTrace().tag("file" to name), inputStream)
        validationResults += root.validate(context).results.createSummary(groupBy = groupByTag("file"))
    }

    fun http(body: HttpValidationWorker.() -> Unit) {
        HttpValidationWorker(this).apply(body)
    }
}
