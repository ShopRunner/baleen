package com.shoprunner.baleen.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class DataDescription(
    val name: String = "",
    val packageName: String = ""
)
