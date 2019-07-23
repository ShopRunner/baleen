package com.shoprunner.baleen.jsonschema.v4

data class SelfDescribing(
    val vendor: String,
    val name: String,
    val version: String,
    val format: String = "jsonschema"
)
