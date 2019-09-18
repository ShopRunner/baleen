package com.shoprunner.baleen.json

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.datawrappers.HashData
import java.io.InputStream
import java.util.Scanner
import java.util.regex.Pattern

/**
 * Validation functions for various Json inputs. Functions support validating single json objects, arrays of json
 * objects and newline (or custom) delimited streams of json objects.
 */
object JsonUtil {

    /**
     * Given an input stream that consists of one Json object or primitive, validate it against the given
     * BaleenType.
     *
     * @param baleenType The type to validate
     * @param dataTrace The starting place for tracing through the data. Example: filename or stream name
     * @param inputStream The data to evaluate. Must by a single json object/primitive.
     * @param options Some parsing option set to defaults
     */
    @JvmStatic
    fun validate(baleenType: BaleenType, dataTrace: DataTrace, inputStream: InputStream, options: Options = Options()): Validation {
        val jsonFactory = JsonFactory()
        val jsonParser = jsonFactory.createParser(inputStream)
        val token = jsonParser.nextToken()
        val parentDataTrace = dataTrace
            .tag("line", jsonParser.currentLocation.lineNr.toString())
            .tag("column", jsonParser.currentLocation.columnNr.toString())

        val value = parseValue(token, jsonParser, options).let {
            when (it) {
                is JsonDataLeaf -> it.value
                is JsonDataNode -> it
            }
        }

        if (baleenType is DataDescription && value is Data) {
            return baleenType.validate(Context(value, parentDataTrace))
        } else {
            val results = baleenType.validate(parentDataTrace, value).asIterable()
            return Validation(Context(HashData(mapOf("root" to value)), parentDataTrace), results)
        }
    }

    /**
     * Given an input stream consisting of an array of json objects/primitives, validate it against the given BaleenType.
     * It efficiently creates a lazy sequence of results from parsing the array. As a stream based approach, it will
     * be efficient in memory, but the results will need to be transformed into a list or otherwise acted on before the
     * inputStream is processed.
     *
     * This returns a ValidationError if not a Json array.
     *
     * @param baleenType The type to validate
     * @param dataTrace The starting place for tracing through the data. Example: filename or stream name
     * @param inputStream The data to evaluate. Must by a single json array.
     */
    @JvmStatic
    fun validateRootJsonArray(baleenType: BaleenType, dataTrace: DataTrace, inputStream: InputStream, options: Options = Options()): Validation {
        val jsonFactory = JsonFactory()
        val jsonParser = jsonFactory.createParser(inputStream)
        val parentDataTrace = dataTrace
            .tag("line", jsonParser.currentLocation.lineNr.toString())
            .tag("column", jsonParser.currentLocation.columnNr.toString())

        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            val error = ValidationError(parentDataTrace, "is not an array", null)
            return Validation(Context(HashData(emptyMap()), parentDataTrace), listOf(error))
        }

        val results = streamParseArray(jsonParser, options)
            .map {
                when (it) {
                    is JsonDataLeaf -> it.value
                    is JsonDataNode -> it
                }
            }
            .mapIndexed { index, value ->
                val indexTrace = parentDataTrace.plus("index $index")
                    .tag("line", jsonParser.currentLocation.lineNr.toString())
                    .tag("column", jsonParser.currentLocation.columnNr.toString())

                if (baleenType is DataDescription && value is Data) {
                    baleenType.validate(Context(value, indexTrace)).results
                } else {
                    baleenType.validate(indexTrace, value).asIterable()
                }
            }.flatten()

        return Validation(Context(HashData(emptyMap()), dataTrace), results.asIterable())
    }

    /**
     * Given an input stream consisting of delimited stream of json objects/primitives, validate it against the given BaleenType.
     * It efficiently creates a lazy sequence of results from parsing the the delimited stream. As a stream based approach, it will
     * be efficient in memory, but the results will need to be transformed into a list or otherwise acted on before the
     * inputStream is processed.
     *
     * The delimiter is defaulted to "\n" as if each json object/primitive was on a separate line in a file.
     *
     * @param baleenType The type to validate
     * @param dataTrace The starting place for tracing through the data. Example: filename or stream name
     * @param inputStream The data to evaluate. Each json object/primitive separated by a delimiter
     * @param recordDelimiter The delimiter pattern splitting each record. Defaults to newline "\n".
     */
    @JvmStatic
    fun validateJsonStream(baleenType: BaleenType, dataTrace: DataTrace, inputStream: InputStream, recordDelimiter: Pattern = "\n".toPattern(), options: Options = Options()): Validation {
        with(Scanner(inputStream).useDelimiter(recordDelimiter)) {
            val results = this.iterator().asSequence()
                .mapIndexed { i, v ->
                    val indexedTrace = dataTrace.plus("index $i").tag("line", i.toString())
                    validate(baleenType, indexedTrace, v.byteInputStream(), options).results
                }
                .flatten()

            return Validation(Context(HashData(emptyMap()), dataTrace), results.asIterable())
        }
    }

    /**
     * Given a token, parse the appropriate value and return the data element.
     */
    private fun parseValue(jsonToken: JsonToken, jsonParser: JsonParser, options: Options): JsonData {
        val line = jsonParser.currentLocation.lineNr
        val column = jsonParser.currentLocation.columnNr
        return when (jsonToken) {
            JsonToken.VALUE_FALSE -> JsonDataLeaf(jsonParser.valueAsBoolean, line, column)
            JsonToken.VALUE_TRUE -> JsonDataLeaf(jsonParser.valueAsBoolean, line, column)
            JsonToken.VALUE_NULL -> JsonDataLeaf(null, line, column)
            JsonToken.VALUE_NUMBER_FLOAT -> when (options.decimalOptions) {
                DecimalOptions.FLOAT -> JsonDataLeaf(jsonParser.floatValue, line, column)
                DecimalOptions.DOUBLE -> JsonDataLeaf(jsonParser.valueAsDouble, line, column)
                DecimalOptions.BIG_DECIMAL -> JsonDataLeaf(jsonParser.decimalValue, line, column)
            }
            JsonToken.VALUE_NUMBER_INT -> when (options.integerOptions) {
                IntegerOptions.INT -> JsonDataLeaf(jsonParser.valueAsInt, line, column)
                IntegerOptions.LONG -> JsonDataLeaf(jsonParser.valueAsLong, line, column)
                IntegerOptions.BIG_INTEGER -> JsonDataLeaf(jsonParser.bigIntegerValue, line, column)
                IntegerOptions.BIG_DECIMAL -> JsonDataLeaf(jsonParser.decimalValue, line, column)
            }
            JsonToken.VALUE_STRING -> JsonDataLeaf(jsonParser.valueAsString, line, column)
            JsonToken.START_OBJECT -> parseObject(jsonParser, options)
            JsonToken.START_ARRAY -> parseArray(jsonParser, options)
            else -> throw JsonParseException(jsonParser, "Unable to parse json into a Baleen DataValue: Seeing '$jsonToken'")
        }
    }

    /**
     * Given that have started parsing an object, parse the rest of the object.
     */
    private fun parseObject(jsonParser: JsonParser, options: Options): JsonData {
        return JsonDataNode(jsonParser.currentLocation.lineNr, jsonParser.currentLocation.columnNr).apply {
            generateSequence { jsonParser.nextToken() }
                .takeWhile { it != JsonToken.END_OBJECT }
                .forEach { jsonToken ->
                    if (jsonToken == JsonToken.FIELD_NAME) {
                        val attrName = jsonParser.currentName
                        val valueToken = jsonParser.nextToken()
                        val value = parseValue(valueToken, jsonParser, options)

                        this.hash[attrName] = value
                    }
                }
        }
    }

    /**
     * Given that have started parsing an array, parse the rest of the array.
     */
    private fun parseArray(jsonParser: JsonParser, options: Options): JsonData {
        val elements = streamParseArray(jsonParser, options).map {
            when (it) {
                is JsonDataLeaf -> it.value
                is JsonDataNode -> it
            }
        }.toList()

        return JsonDataLeaf(elements, jsonParser.currentLocation.lineNr, jsonParser.currentLocation.columnNr)
    }

    /**
     * Helper function to process the array in a stream/sequence.
     */
    private fun streamParseArray(jsonParser: JsonParser, options: Options): Sequence<JsonData> =
        generateSequence { jsonParser.nextToken() }
            .takeWhile { it != JsonToken.END_ARRAY }
            .map { jsonToken ->
                parseValue(jsonToken, jsonParser, options)
            }
}
