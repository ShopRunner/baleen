package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.annotation.DataTest

@DataTest
fun assertNotEmptyPack(pack: Pack, dataTrace: DataTrace = com.shoprunner.baleen.dataTrace()): Sequence<ValidationResult> {
    if (pack.dogs.isEmpty()) {
        return sequenceOf(ValidationError(dataTrace + "attr dogs", "Empty Pack", pack.dogs))
    } else {
        return sequenceOf(ValidationInfo(dataTrace + "attr dogs", "Pack is not empty", pack.dogs))
    }
}
