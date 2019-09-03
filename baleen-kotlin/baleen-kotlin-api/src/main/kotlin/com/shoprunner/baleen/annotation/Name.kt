package com.shoprunner.baleen.annotation

/**
 * Specify the name that the field has in the data. Overrides the field name in the class.
 * Use @Alias for additional names.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
annotation class Name(
    /**
     * The name of the field in the data
     */
    val value: String
)
