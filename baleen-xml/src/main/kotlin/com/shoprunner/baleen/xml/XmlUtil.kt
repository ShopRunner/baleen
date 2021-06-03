package com.shoprunner.baleen.xml

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

object XmlUtil {

    /**
     * Given an XML InputStream, return data context after parsing the XML.
     */
    @JvmStatic
    fun fromXmlToContext(dataTrace: DataTrace, inputStream: InputStream): Context {
        val factory = SAXParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newSAXParser()
        val handler = LineAwareHandler()

        parser.parse(inputStream, handler)
        return Context(handler.data, dataTrace)
    }

    /**
     * Given an InputStream where data is the root, validate it against the DataDescription
     */
    @JvmStatic
    fun validateFromRoot(description: BaleenType, inputStream: InputStream, dataTrace: DataTrace = DataTrace()): Validation {
        val root = Baleen.describe("root") {
            it.attr(description.name(), description)
        }
        val context = fromXmlToContext(dataTrace, inputStream)
        return root.validate(context)
    }

    /**
     * Given an File where data is the root, validate it against the DataDescription
     */
    @JvmStatic
    fun validateFromRoot(description: BaleenType, file: File, dataTrace: DataTrace = DataTrace()): Validation {
        return with(file.inputStream()) {
            validateFromRoot(description, this, dataTrace)
        }
    }

    /**
     * Given an String where data is the root, validate it against the DataDescription
     */
    @JvmStatic
    fun validateFromRoot(description: BaleenType, xml: String, dataTrace: DataTrace = DataTrace()): Validation {
        return with(ByteArrayInputStream(xml.toByteArray(Charsets.UTF_8))) {
            validateFromRoot(description, this, dataTrace)
        }
    }
}
