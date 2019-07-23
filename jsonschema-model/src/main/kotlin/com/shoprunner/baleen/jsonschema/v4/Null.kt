package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonSerialize

/**
 * Representation of null in Json. There is a difference between null and
 * absence of value. When representing null, use this object.
 */
@JsonSerialize(using = NullJsonSerializer::class)
object Null
