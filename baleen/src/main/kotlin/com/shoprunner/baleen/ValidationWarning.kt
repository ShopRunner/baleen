package com.shoprunner.baleen

data class ValidationWarning(val dataTrace: DataTrace, val message: String, val value: Any?) : ValidationResult()