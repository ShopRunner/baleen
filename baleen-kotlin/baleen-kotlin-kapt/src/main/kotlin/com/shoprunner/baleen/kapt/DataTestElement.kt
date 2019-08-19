package com.shoprunner.baleen.kapt

import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType

data class DataTestElement(
    val executableElement: ExecutableElement,
    val receiverType: DeclaredType,
    val isExtension: Boolean
)
