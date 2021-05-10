package com.shoprunner.baleen

data class Validation(val context: Context, val results: Iterable<ValidationResult>) {
    fun isValid() = results.none { it is ValidationError }
    fun cache() = CachedValidation(context, results)
}

class CachedValidation(val context: Context, results: Iterable<ValidationResult>) {
    val results: Iterable<ValidationResult> by lazy { results.toList() }
    fun isValid() = results.none { it is ValidationError }
}
