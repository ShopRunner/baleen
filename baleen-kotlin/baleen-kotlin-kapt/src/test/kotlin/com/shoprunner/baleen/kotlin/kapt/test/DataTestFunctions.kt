package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.annotation.DataTest
import com.shoprunner.baleen.dataTrace

@DataTest
fun assertStringLength(stringModel: StringModel, dataTrace: DataTrace = dataTrace()): Sequence<ValidationResult> {
    if (stringModel.string.length <= 100) {
        return sequenceOf(ValidationInfo(dataTrace + "attr string", "is less than 100 chars", stringModel.string))
    } else {
        return sequenceOf(ValidationError(dataTrace + "attr string", "is greater than 100 chars", stringModel.string))
    }
}

@DataTest(isExtension = true)
fun IntModel.assertMax(dataTrace: DataTrace = dataTrace()): Sequence<ValidationResult> {
    if (intNumber <= 100) {
        return sequenceOf(ValidationInfo(dataTrace + "attr intNumber", "is less than 100 chars", intNumber))
    } else {
        return sequenceOf(ValidationError(dataTrace + "attr intNumber", "is greater than 100 chars", intNumber))
    }
}

@DataTest
fun assertListNotEmpty(listModel: ListStringModel, dataTrace: DataTrace = dataTrace()): Iterable<ValidationResult> {
    if (listModel.stringList.size > 0) {
        return listOf(ValidationInfo(dataTrace + "attr stringList", "is not empty", listModel.stringList))
    } else {
        return listOf(ValidationError(dataTrace + "attr stringList", "is empty", listModel.stringList))
    }
}

@DataTest
fun assertArrayNotEmpty(arrayModel: ArrayStringModel, dataTrace: DataTrace = dataTrace()): Iterable<ValidationResult> {
    if (arrayModel.stringArray.size > 0) {
        return listOf(ValidationInfo(dataTrace + "attr stringArray", "is not empty", arrayModel.stringArray))
    } else {
        return listOf(ValidationError(dataTrace + "attr stringArray", "is empty", arrayModel.stringArray))
    }
}
