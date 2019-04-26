package com.shoprunner.baleen.types

class StringCoercibleToLong(longType: LongType) :
        StringCoercibleToType<LongType>(longType, { it.toLongOrNull() })