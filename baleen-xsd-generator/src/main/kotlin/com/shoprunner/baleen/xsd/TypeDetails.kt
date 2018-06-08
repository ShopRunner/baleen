package com.shoprunner.baleen.xsd

import com.shoprunner.baleen.xsd.xml.SimpleType

data class TypeDetails(val type: String? = null, val maxOccurs: String? = null, val simpleType: SimpleType? = null)