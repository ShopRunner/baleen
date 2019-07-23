package com.shoprunner.baleen.xml

import java.util.Stack
import org.xml.sax.Attributes
import org.xml.sax.Locator
import org.xml.sax.helpers.DefaultHandler

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
    val data = XmlDataNode()

    /**
     * Stack used to create nested data structures
     */
    private val elementStack = Stack<XmlDataNode>().apply { push(data) }

    /**
     * Temporary store for text data between start and end tags.
     */
    private val textBuffer = StringBuilder()

    /**
     * store whether the current node is null
     */
    private var nil = false

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
    override fun startElement(uri: String, localName: String, qName: String, attrs: Attributes) {
        val lineNumber = locator?.lineNumber
        val columnNumber = locator?.columnNumber

        val parent = elementStack.peek()
        val currentNode = XmlDataNode(line = lineNumber, column = columnNumber)
        for (i in 0 until attrs.length) {
            val key = attrs.getLocalName(i)
            val value = attrs.getValue(i)
            if (key == "nil" && value == "true") {
                parent.hash[localName] = XmlDataLeaf(value = null, line = lineNumber, column = columnNumber)
                nil = true
                return
            } else {
                currentNode.hash[key] = XmlDataLeaf(value = value, line = lineNumber, column = columnNumber)
            }
        }

        pushParent(parent, localName, currentNode)

        elementStack.push(currentNode)
    }

    private fun pushParent(parent: XmlDataNode, key: String, value: XmlDataNode) {
        // If the key is present
        if (parent.containsKey(key)) {
            val existing = parent.hash[key]
            if (existing is XmlDataLeaf && existing.value is Iterable<*>) {
                parent.hash[key] = XmlDataLeaf(existing.value + value, existing.line, existing.column)
            } else {
                parent.hash[key] = XmlDataLeaf(listOf(existing, value), existing?.line, existing?.column)
            }
        } else {
            parent.hash[key] = value
        }
    }

    /**
     * Triggered when an end tag is seen or its the end of single element tag. Ex: </end> or <startend/>
     *
     * It pops the current element from the stack and transforms it into Data element for Baleen.
     */
    override fun endElement(uri: String, localName: String, qName: String) {
        when {
            nil -> nil = false
            textBuffer.trim().isNotEmpty() -> {
                elementStack.pop()
                val current = elementStack.peek()
                val old = current.hash[localName]!!

                if (old is XmlDataLeaf && old.value is Iterable<*>) {
                    val head = old.value.flatMap {
                        when (it) {
                            is XmlDataLeaf -> listOf(it.value)
                            is String -> listOf(it)
                            else -> emptyList()
                        }
                    }
                    current.hash[localName] = XmlDataLeaf(head + textBuffer.trim(), old.line, old.column)
                } else {
                    current.hash[localName] = XmlDataLeaf(value = textBuffer.trim(), line = old.line, column = old.column)
                }
                textBuffer.clear()
            }
            else -> elementStack.pop()
        }
    }

    /**
     * Triggered when processing text within an element (including whitespace).
     */
    override fun characters(ch: CharArray, start: Int, length: Int) {
        textBuffer.append(ch, start, length)
    }
}
