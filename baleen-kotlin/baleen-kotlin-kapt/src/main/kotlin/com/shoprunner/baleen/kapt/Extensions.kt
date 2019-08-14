package com.shoprunner.baleen.kapt

import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror
import org.jetbrains.annotations.NotNull

fun <A : Annotation> Element.isAnnotationPresent(annotationType: Class<A>): Boolean =
    this.getAnnotation(annotationType) != null

fun <A : Annotation> TypeMirror.isAnnotationPresent(annotationType: Class<A>): Boolean =
    this.getAnnotation(annotationType) != null

fun Element.isNotNullField(): Boolean = this.isAnnotationPresent(NotNull::class.java)

fun TypeMirror.isNotNullField(): Boolean = this.isAnnotationPresent(NotNull::class.java)
