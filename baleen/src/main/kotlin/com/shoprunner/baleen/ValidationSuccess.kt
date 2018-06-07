package com.shoprunner.baleen

data class ValidationSuccess(val dataTrace: DataTrace, val value: Any?) : ValidationResult()