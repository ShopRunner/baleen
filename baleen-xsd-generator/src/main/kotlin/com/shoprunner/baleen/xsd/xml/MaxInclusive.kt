package com.shoprunner.baleen.xsd.xml

import jakarta.xml.bind.annotation.XmlAttribute
import java.math.BigDecimal

data class MaxInclusive(
    @get:XmlAttribute
    val value: BigDecimal? = null
)
