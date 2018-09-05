package com.shoprunner.baleen.jsonschema.v4

class RootJsonSchema(
    val id: String?,
    val definitions: Map<String, ObjectSchema>,
    val `$ref`: String,
    val `$schema`: String,
    val self: SelfDescribing? = null
)