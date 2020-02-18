package com.shoprunner.baleen.jsonschema.v3

import com.shoprunner.baleen.generator.CoercibleHandlerOption
import com.shoprunner.baleen.generator.Options

data class JsonSchemaOptions(
    override val coercibleHandlerOption: CoercibleHandlerOption = CoercibleHandlerOption.FROM
) : Options
