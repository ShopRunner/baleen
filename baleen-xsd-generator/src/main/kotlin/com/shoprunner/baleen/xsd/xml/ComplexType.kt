package com.shoprunner.baleen.xsd.xml

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

data class ComplexType(
    @get:XmlAttribute()
    val name: String? = null,

    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val annotation: Annotation? = null,

    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val sequence: Sequence? = null
)