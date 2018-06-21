package com.shoprunner.baleen.xsd.xml

import javax.xml.bind.annotation.XmlElement

data class SimpleType(
    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val restriction: Restriction? = null
)