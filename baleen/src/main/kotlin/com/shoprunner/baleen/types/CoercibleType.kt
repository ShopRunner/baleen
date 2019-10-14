package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType

abstract class CoercibleType<in FROM : BaleenType, out TO : BaleenType>(val type: TO) : BaleenType
