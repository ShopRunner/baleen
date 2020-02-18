package com.shoprunner.baleen.generator

import com.shoprunner.baleen.BaleenType

typealias TypeMapper<T, O> = (BaleenType, O) -> T
