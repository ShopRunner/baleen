package com.shoprunner.baleen.generator

import com.shoprunner.baleen.BaleenType

typealias TypeMapper<T> = (BaleenType, Options) -> T
