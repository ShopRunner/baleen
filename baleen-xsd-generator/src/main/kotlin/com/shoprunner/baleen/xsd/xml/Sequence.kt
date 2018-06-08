package com.shoprunner.baleen.xsd.xml

import javax.xml.bind.annotation.XmlElement

data class Sequence(

    @get:XmlElement(name="element", namespace = "http://www.w3.org/2001/XMLSchema")
    val elements: List<Element> = mutableListOf()
)