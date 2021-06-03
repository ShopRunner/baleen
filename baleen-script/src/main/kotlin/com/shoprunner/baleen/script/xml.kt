package com.shoprunner.baleen.script

import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.xml.XmlUtil
import java.io.File
import java.io.InputStream

fun xml(filename: String, vararg tags: Pair<String, String>): DataAccess =
    xml(File(filename), *tags)

fun xml(file: File, vararg tags: Pair<String, String>): DataAccess =
    xml(file.inputStream(), "file" to file.name, *tags)

fun xml(inputStream: InputStream, vararg tags: Pair<String, String>): DataAccess =
    { dataDescription ->
        XmlUtil.validateFromRoot(dataDescription, inputStream, dataTrace().tag("format" to "xml", *tags))
            .results
    }

fun xml(vararg tags: Pair<String, String>): HttpDataAccess = { responseBody ->
    if (responseBody != null) {
        xml(responseBody, *tags)
    } else {
        { emptySequence<ValidationResult>().asIterable() }
    }
}
