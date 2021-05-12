package com.shoprunner.baleen.kapt

import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType

data class DataTestElement(
    val dataTestFunction: ExecutableElement,
    val dataClass: DeclaredType,
    val isExtension: Boolean,
    val isAssertionBased: Boolean
)
