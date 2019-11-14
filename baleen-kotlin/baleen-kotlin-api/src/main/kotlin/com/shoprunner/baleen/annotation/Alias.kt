package com.shoprunner.baleen.annotation

/**
 * Specify additional names that the field can have in the data.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
annotation class Alias(
    /**
     * The aliases
     */
    vararg val value: String
)
