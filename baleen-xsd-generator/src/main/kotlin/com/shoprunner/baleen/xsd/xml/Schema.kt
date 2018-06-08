package com.shoprunner.baleen.xsd.xml

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name="schema", namespace = "http://www.w3.org/2001/XMLSchema")
data class Schema(

    @get:XmlElement(namespace = "http://www.w3.org/2001/XMLSchema")
    val annotation: Annotation? = null,

    @get:XmlElement(name="element", namespace = "http://www.w3.org/2001/XMLSchema")
    val elements: List<Element> = mutableListOf(),

    @get:XmlElement(name="complexType", namespace = "http://www.w3.org/2001/XMLSchema")
    val complexTypes: List<ComplexType> = emptyList()
)