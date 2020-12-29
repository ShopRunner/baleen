package com.shoprunner.baleen.xsd.xml

import jakarta.xml.bind.annotation.XmlAttribute

data class MaxLength(
    @get:XmlAttribute
    val value: Int? = null
)
