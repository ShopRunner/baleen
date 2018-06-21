package com.shoprunner.baleen.xsd.xml

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

data class Restriction(
    @get:XmlAttribute
    val base: String? = null,

    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val maxLength: MaxLength? = null,

    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val minLength: MinLength? = null,

    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val minInclusive: MinInclusive? = null,

    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val maxInclusive: MaxInclusive? = null,

    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val enumeration: List<Enumeration> = emptyList()
)