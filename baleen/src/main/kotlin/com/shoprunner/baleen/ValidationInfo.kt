package com.shoprunner.baleen

data class ValidationInfo(val dataTrace: DataTrace, val message: String, val value: Any?) : ValidationResult()