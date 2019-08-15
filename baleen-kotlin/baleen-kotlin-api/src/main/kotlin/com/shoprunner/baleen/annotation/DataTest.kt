package com.shoprunner.baleen.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class DataTest(
    /**
     * If set to true then the function this annotates is a an Extension function and is called as such.
     *
     * ```kotlin
     * @DataTest(isExtension = true)
     * DataClass.assertMyTest(dataTrace = dataTrace(): Sequence<ValidationResult> { ... }
     * ```
     */
    val isExtension: Boolean = false
)
