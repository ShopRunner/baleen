package com.shoprunner.baleen

data class Validation(val context: Context, val results: Iterable<ValidationResult>) {
    fun isValid() = results.none { it is ValidationError }
}