package com.shoprunner.baleen.types

class StringCoercibleToBoolean(booleanType: BooleanType = BooleanType()) :
        StringCoercibleToType<BooleanType>(booleanType, { it.toBoolean() })
