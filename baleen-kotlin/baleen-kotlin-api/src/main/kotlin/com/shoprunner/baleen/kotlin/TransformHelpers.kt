@file:JvmName("TransformHelpers")

package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.Data

inline fun <reified T> castIfData(
    data: Any?,
    transform: (Data) -> T = { _ -> throw ClassCastException("Not expecting a Data class") }
): T? =
    data?.let {
        when (it) {
            is Data -> transform(it)
            else -> it as T
        }
    }

inline fun <reified T> castElementsInList(
    item: Any?,
    transform: (Data) -> T = { _ -> throw ClassCastException("Not expecting a Data class") }
): List<T>? =
    item?.let {
        when (it) {
            is List<*> ->
                it.map {
                    if (it is Data) transform(it)
                    else it as T
                }
            is Array<*> ->
                it.map {
                    if (it is Data) transform(it)
                    else it as T
                }
            else -> null
        }
    }

inline fun <reified K, reified V> castElementsInMap(
    item: Any?,
    transformKeys: (Data) -> K = { _ -> throw ClassCastException("Not expecting a Data class") },
    transformValues: (Data) -> V = { _ -> throw ClassCastException("Not expecting a Data class") }
): Map<K, V>? =
    item?.let {
        when (it) {
            is Map<*, *> ->
                it.map { (k, v) ->
                    val key =
                        if (k is Data) transformKeys(k)
                        else it as K

                    val value =
                        if (v is Data) transformValues(v)
                        else it as V

                    key to value
                }.toMap()
            else -> null
        }
    }
