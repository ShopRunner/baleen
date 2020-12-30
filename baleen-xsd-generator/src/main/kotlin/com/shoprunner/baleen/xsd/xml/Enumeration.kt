package com.shoprunner.baleen.xsd.xml

import jakarta.xml.bind.annotation.XmlAttribute

data class Enumeration(
    @get:XmlAttribute
    val value: String? = null
)
