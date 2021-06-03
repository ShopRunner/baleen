package com.shoprunner.baleen.script

import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.json.JsonUtil
import com.shoprunner.baleen.json.Options
import com.shoprunner.baleen.types.tag
import java.io.File
import java.io.InputStream

fun json(filename: String, options: Options = Options(), vararg tags: Pair<String, String>): DataAccess =
    json(File(filename), options, *tags)

fun json(file: File, options: Options = Options(), vararg tags: Pair<String, String>): DataAccess =
    json(file.inputStream(), options, "file" to file.name, *tags)

fun json(inputStream: InputStream, options: Options = Options(), vararg tags: Pair<String, String>): DataAccess =
    { dataDescription ->
        JsonUtil.validate(
            dataDescription,
            dataTrace().tag(*tags),
            inputStream,
            options
        ).results
    }

fun json(options: Options = Options(), vararg tags: Pair<String, String>): HttpDataAccess = { responseBody ->
    if (responseBody != null) {
        json(responseBody, options, "format" to "json", *tags)
    } else {
        { emptySequence<ValidationResult>().asIterable() }
    }
}
