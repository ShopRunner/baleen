package com.shoprunner.baleen.version2

import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation
import com.shoprunner.baleen.csv.FlowableUtil
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.datawrappers.HashData
import com.shoprunner.baleen.xml.XmlUtil
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import java.io.InputStream
import kotlin.reflect.KClass

// API Improvements, that we may want to add to the Baleen API

fun <T : Any> KClass<T>.learnSchema(): DataDescription {
    throw NotImplementedError()
}

fun <T : Any> T.learnSchema(): DataDescription {
    return this::class.learnSchema()
}

fun Iterable<Map<String, Any?>>.learnSchema(): DataDescription {
    throw NotImplementedError()
}

fun InputStream.learnSchema(dataHandler: DataHandler): DataDescription {
    throw NotImplementedError()
}

fun Schema.learnSchema(): DataDescription {
    throw NotImplementedError()
}

fun GenericRecord.learnSchema(): DataDescription {
    return this.schema.learnSchema()
}

fun Map<String, Any?>.validate(schema: DataDescription, dataTrace: DataTrace = dataTrace()): Validation {
    return schema.validate(Context(HashData(this), dataTrace))
}

fun Iterable<Map<String, Any?>>.validate(schema: DataDescription, dataTrace: DataTrace = dataTrace()): Validation {
    val results = this.asSequence().mapIndexed { index, map ->
        map.validate(schema, dataTrace + "index $index").results.asSequence()
    }.flatten()

    return Validation(Context(HashData(emptyMap()), dataTrace), results.asIterable())
}

typealias DataHandler = (InputStream, DataTrace) -> Iterable<Context>

class CsvDataHandler(
    private val delimiter: Char = ',',
    private val quote: Char = '"',
    private val escape: Char = '\\'
) : DataHandler {
    override fun invoke(inputStream: InputStream, dataTrace: DataTrace): Iterable<Context> {
        return FlowableUtil.fromCsvWithHeader(dataTrace, { inputStream.bufferedReader() }, delimiter, quote, escape).blockingIterable()
    }
}

val XmlDataHandler: DataHandler = { inputStream: InputStream, dataTrace: DataTrace ->
    sequenceOf(XmlUtil.fromXmlToContext(dataTrace, inputStream)).asIterable()
}

val JsonDataHandler: DataHandler = { inputStream: InputStream, dataTrace: DataTrace ->
    throw NotImplementedError()
}

val JsonPerLineDataHandler: DataHandler = { inputStream: InputStream, dataTrace: DataTrace ->
    throw NotImplementedError()
}

val JsonArrayDataHandler: DataHandler = { inputStream: InputStream, dataTrace: DataTrace ->
    throw NotImplementedError()
}

val AvroDataHandler: DataHandler = { inputStream: InputStream, dataTrace: DataTrace ->
    throw NotImplementedError()
}

fun InputStream.validate(schema: DataDescription, dataHandler: DataHandler, dataTrace: DataTrace = dataTrace()): Validation {
    val results = dataHandler(this, dataTrace).asSequence().flatMap { ctx ->
        schema.validate(ctx).results.asSequence()
    }

    return Validation(Context(HashData(emptyMap()), dataTrace), results.asIterable())
}

class AvroData(val record: GenericRecord) : Data {
    override fun containsKey(key: String): Boolean {
        return record.schema.getField(key) != null
    }

    // returns null if value does not exist
    override operator fun get(key: String): Any? = record.get(key)

    override val keys: Set<String> = record.schema.fields.map { it.name() }.toSet()
}

fun GenericRecord.validate(schema: DataDescription, dataTrace: DataTrace = dataTrace()): Validation {
    return schema.validate(Context(AvroData(this), dataTrace))
}

fun <T> T.validate(schema: DataDescription, dataTrace: DataTrace = dataTrace()): Validation {
    throw NotImplementedError()
}