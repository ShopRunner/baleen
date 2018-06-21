package com.shoprunner.baleen.xsd.xml

import javax.xml.bind.annotation.XmlAttribute

data class Enumeration(
    @get:XmlAttribute
    val value: String? = null
)