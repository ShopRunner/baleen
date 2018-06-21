package com.shoprunner.baleen.xsd.xml

import javax.xml.bind.annotation.XmlAttribute

data class MinLength(
    @get:XmlAttribute
    val value: Int? = null
)