package com.shoprunner.baleen.xsd.xml

import jakarta.xml.bind.annotation.XmlAttribute

data class MinLength(
    @get:XmlAttribute
    val value: Int? = null
)
