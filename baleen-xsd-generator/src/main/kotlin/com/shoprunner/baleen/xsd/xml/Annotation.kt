package com.shoprunner.baleen.xsd.xml

import jakarta.xml.bind.annotation.XmlElement

data class Annotation(
    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val documentation: String? = null
)
