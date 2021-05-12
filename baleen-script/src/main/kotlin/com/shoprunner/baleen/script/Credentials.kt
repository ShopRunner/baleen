package com.shoprunner.baleen.script

data class Credentials(
    val url: String,
    val user: String,
    val password: String? = null,
    val dataSourceProperties: Map<String, String> = emptyMap()
)