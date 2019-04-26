package com.shoprunner.baleen

interface BaleenType {
    val name: String

    fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult>
}