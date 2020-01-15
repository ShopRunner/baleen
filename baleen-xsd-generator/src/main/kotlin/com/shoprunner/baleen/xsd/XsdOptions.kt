package com.shoprunner.baleen.xsd

import com.shoprunner.baleen.generator.CoercibleHandlerOption
import com.shoprunner.baleen.generator.Options

object XsdOptions : Options {
    override val coercibleHandlerOption: CoercibleHandlerOption = CoercibleHandlerOption.TO
}
