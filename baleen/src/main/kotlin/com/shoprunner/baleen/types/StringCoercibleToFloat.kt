package com.shoprunner.baleen.types

class StringCoercibleToFloat(floatType: FloatType) :
        StringCoercibleToType<FloatType>(floatType, { it.toFloatOrNull() })