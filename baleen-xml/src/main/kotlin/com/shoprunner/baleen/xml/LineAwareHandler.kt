package com.shoprunner.baleen.xml

import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataValue
import com.shoprunner.baleen.datawrappers.HashData
import org.xml.sax.Attributes
import org.xml.sax.Locator
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.util.Stack

/**
 * Simplify alias so we don't have to type all of the generics all the time
 */
internal typealias LineAwareMap = MutableMap<String, DataValue<*>>

/**
 * SAX Parser Handler to create the LineAware data from XML
 */
internal class LineAwareHandler : DefaultHandler() {
    /**
     * Locator set by ContentHandler to get information like line or column numbers
     */
    private var locator: Locator? = null

    /**
     * The parsed data from XML, with line and column info
     */
    private val data = mutableMapOf<String, DataValue<*>>()

    /**
     * Stack used to create nested data structures
     */
    private val elementStack = Stack<DataValue<LineAwareMap>>()

    /**
     * Temporary store for text data between start and end tags.
     */
    private val textBuffer = StringBuilder()

    /**
     * Get the final parsed data after processing.
     */
    fun getData(): Data = HashData(data)

    /**
     * Set by ContentHandler from within SAX Parser. No need to set it ourselves.
     */
    override fun setDocumentLocator(locator: Locator) {
        this.locator = locator
    }

    /**
     * Triggered when an start tag is seen. Ex: <start> or <startend/>
     *
     * It creates a new LineAwareValue for the element and adds it to the stack and to its parent.
     * It processes each attribute as a child.
     */
    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attrs: Attributes) {
        addTextIfNeeded()

        val lineNumber = locator?.lineNumber ?: -1
        val columnNumber = locator?.columnNumber

        val currentNode: DataValue<LineAwareMap> = DataValue(mutableMapOf(), lineNumber, columnNumber)
        for (i in 0 until attrs.length) {
            currentNode.value[attrs.getLocalName(i)] =
                DataValue<Any?>(attrs.getValue(i), lineNumber, columnNumber)
        }

        if (this.elementStack.isNotEmpty()) {
            val parent = this.elementStack.peek()
            parent.value[localName] = currentNode
        } else {
            data[localName] = currentNode
        }

        this.elementStack.push(currentNode)
    }

    /**
     * Triggered when an end tag is seen or its the end of single element tag. Ex: </end> or <startend/>
     *
     * It pops the current lemnt from the stack and transforms it into Data element for Baleen.
     */
    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        addTextIfNeeded()
        val currentElement = this.elementStack.pop()

        // Transform Maps into Data for Baleen to work
        if (this.elementStack.isNotEmpty()) {
            val parent = this.elementStack.peek()
            parent.value[localName] = transform(currentElement)
        } else {
            data[localName] = transform(currentElement)
        }
    }

    /**
     * Triggered when processing text within an element (including whitespace).
     */
    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        textBuffer.append(ch, start, length)
    }

    /**
     * If the textBuffer has data, then we concatenate it to the #text attribute
     */
    private fun addTextIfNeeded() {
        if (textBuffer.isNotEmpty()) {
            val currentElement = elementStack.peek()
            val currentValue = currentElement.value["#text"]
            val currentText = StringBuilder().append(currentValue?.value ?: "")
            val newText = currentText.append(textBuffer).toString().trim()
            if (newText.isNotBlank()) {
                currentElement.value["#text"] =
                    DataValue(newText, currentElement.lineNumber, currentElement.columnNumber)
            }
            textBuffer.delete(0, textBuffer.length)
        }
    }

    /**
     * Transforms LineAwareMap into Data(lineAwareMap) for Baleen or promotes #text to a first level
     * value if it is the only attribute or handles null values when nil is present.
     */
    private fun transform(currentElement: DataValue<LineAwareMap>): DataValue<*> {
        if (currentElement.value.containsKey("nil")) {
            return DataValue(
                null,
                currentElement.lineNumber,
                currentElement.columnNumber
            )
        } else if (currentElement.value.size == 1 && currentElement.value.containsKey("#text")) {
            return DataValue(
                currentElement.value["#text"]?.value,
                currentElement.lineNumber,
                currentElement.columnNumber
            )
        } else {
            return DataValue(
                HashData(currentElement.value),
                currentElement.lineNumber,
                currentElement.columnNumber
            )
        }
    }
}