package com.shoprunner.baleen.xml

import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.datawrappers.HashData
import org.w3c.dom.Node
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

object XmlUtil {

    fun createMapFromXml(inputStream: InputStream): Map<String, Any> {
        val dbf = DocumentBuilderFactory.newInstance()
        dbf.isNamespaceAware = true
        val db = dbf.newDocumentBuilder()
        val document = db.parse(inputStream)
        val root = document.documentElement
        val nodeName = root.nodeName ?: throw IllegalStateException("No root element in xml")
        return mapOf(nodeName to createMap(root))
    }

    fun createMap(node: Node): Data {
        val map = HashMap<String, Any?>()
        val nodeList = node.childNodes
        var previousLabel: String? = null
        var childList: MutableList<Any?>? = null
        for (i in 0 until nodeList.length) {
            val currentNode = nodeList.item(i)

            if (isNullValue(currentNode)) {
                map[currentNode.nodeName] = null
            } else if (currentNode.firstChild?.nodeType == Node.TEXT_NODE &&
                    !currentNode.firstChild?.textContent.isNullOrBlank()) {
                map[currentNode.nodeName] = currentNode.textContent.trim()
            } else if (currentNode.nodeType == Node.ELEMENT_NODE) {
                if (previousLabel == currentNode.nodeName) {
                    if (childList == null) {
                        childList = mutableListOf(map[currentNode.nodeName])
                        map[currentNode.nodeName] = childList
                    }
                    childList.add(createMap(currentNode))
                } else {
                    map[currentNode.nodeName] = createMap(currentNode)
                    previousLabel = currentNode.nodeName
                    childList = null
                }
            }
        }
        return HashData(map)
    }

    fun isNullValue(node: Node) =
            (node.nodeType == Node.ELEMENT_NODE) &&
                    (node.attributes.getNamedItemNS("http://www.w3.org/2001/XMLSchema-instance", "nil") != null) ||
                    (node.nodeType == Node.ELEMENT_NODE) &&
                    (node.attributes.getNamedItem("nil") != null)

    @JvmStatic
    fun fromXmlToContext(dataTrace: DataTrace, inputStream: InputStream): Context {
        val map = createMapFromXml(inputStream)
        val data = HashData(map)
        return Context(data, dataTrace)
    }
}