package com.shoprunner.baleen.xsd.xml

import jakarta.xml.bind.annotation.XmlAttribute
import java.math.BigDecimal

data class MinInclusive(
    @get:XmlAttribute
    val value: BigDecimal? = null
)
