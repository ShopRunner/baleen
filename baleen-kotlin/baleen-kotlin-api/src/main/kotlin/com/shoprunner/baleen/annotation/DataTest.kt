package com.shoprunner.baleen.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class DataTest(
    val isExtension: Boolean = false
)
