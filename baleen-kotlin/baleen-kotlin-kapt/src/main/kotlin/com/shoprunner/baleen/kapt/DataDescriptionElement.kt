package com.shoprunner.baleen.kapt

import javax.lang.model.element.TypeElement

data class DataDescriptionElement(
    val typeElement: TypeElement,
    val packageName: String,
    val name: String
)
