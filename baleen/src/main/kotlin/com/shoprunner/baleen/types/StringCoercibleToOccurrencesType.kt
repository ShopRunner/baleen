package com.shoprunner.baleen.types

class StringCoercibleToOccurrencesType(occurrencesType: OccurrencesType) :
        StringCoercibleToType<OccurrencesType>(occurrencesType, { it.split(",") })
