@file:JvmName("DataClassValidationExt")

package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation

@Suppress("unused")
fun Any.dataDescription(): DataDescription =
    throw NotImplementedError("Method not generated")

@Suppress("unused", "UNUSED_PARAMETER")
fun Any.validate(
    dataDescription: DataDescription = this.dataDescription(),
    dataTrace: DataTrace = com.shoprunner.baleen.dataTrace()
): Validation =
    throw NotImplementedError("Method not generated")
