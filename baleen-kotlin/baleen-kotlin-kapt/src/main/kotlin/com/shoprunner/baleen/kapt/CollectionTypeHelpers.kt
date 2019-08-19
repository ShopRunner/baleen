package com.shoprunner.baleen.kapt

import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

fun isIterable(typeUtils: Types, elementUtils: Elements, memberType: TypeMirror?): Boolean {
    return memberType is DeclaredType && typeUtils.isSubtype(
        memberType,
        typeUtils.getDeclaredType(
            elementUtils.getTypeElement(Iterable::class.java.canonicalName),
            typeUtils.getWildcardType(null, null)
        )
    )
}

fun isMap(typeUtils: Types, elementUtils: Elements, memberType: TypeMirror?): Boolean {
    return memberType is DeclaredType && (
            typeUtils.isSubtype(
                memberType,
                typeUtils.getDeclaredType(
                    elementUtils.getTypeElement(Map::class.java.canonicalName),
                    typeUtils.getWildcardType(null, null),
                    typeUtils.getWildcardType(null, null)
                )

            ) ||
                    typeUtils.isSubtype(
                        memberType,
                        typeUtils.getDeclaredType(
                            elementUtils.getTypeElement(java.util.Map::class.java.canonicalName),
                            typeUtils.getWildcardType(null, null),
                            typeUtils.getWildcardType(null, null)
                        )

                    )

            )
}
