package com.shoprunner.baleen.xsd.xml

import java.math.BigDecimal
import javax.xml.bind.annotation.XmlAttribute

data class MinInclusive(
    @get:XmlAttribute
    val value: BigDecimal? = null
)