package com.shoprunner.baleen

interface BaleenType {
    fun name(): String

    fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult>
}