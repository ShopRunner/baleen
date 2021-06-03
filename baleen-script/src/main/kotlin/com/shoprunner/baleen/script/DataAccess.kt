package com.shoprunner.baleen.script

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.ValidationResult

typealias DataAccess = (BaleenType) -> Iterable<ValidationResult>
