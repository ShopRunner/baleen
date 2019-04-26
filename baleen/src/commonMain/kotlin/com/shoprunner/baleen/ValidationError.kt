package com.shoprunner.baleen

data class ValidationError(val dataTrace: DataTrace, val message: String, val value: Any?) : ValidationResult()