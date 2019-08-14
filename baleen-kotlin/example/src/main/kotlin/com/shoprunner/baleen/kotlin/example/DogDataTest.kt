package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.annotation.DataTest
import com.shoprunner.baleen.dataTrace

@DataTest
fun assert3orMoreLegs(dog: Dog, dataTrace: DataTrace = dataTrace()): Sequence<ValidationResult> {
    val numLegs = dog.numLegs
    if (numLegs == null) {
        return emptySequence()
    } else if (numLegs >= 3) {
        return sequenceOf(ValidationInfo(dataTrace, "Num Legs greater or equal to 3", numLegs))
    } else {
        return sequenceOf(ValidationError(dataTrace, "Num Legs less than 3", numLegs))
    }
}
