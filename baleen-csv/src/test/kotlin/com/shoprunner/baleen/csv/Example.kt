package com.shoprunner.baleen.csv

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.csv.PrintUtil.printErrors
import com.shoprunner.baleen.csv.PrintUtil.printSuccessFailureCount
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringType
import io.reactivex.rxkotlin.toFlowable

val dogDescription = "Dog".describeAs {

    "name".type(
        StringType(),
        required = true
    )

    "license".type(
        StringType(),
        required = true
    )

    "legs".type(
        StringCoercibleToLong(LongType(min = 0, max = 4)),
        required = true
    )

    warnOnExtraAttributes()

    test { dataTrace, data ->
        val license = data["license"]
        val name = data["name"]
        if (name !is String || license !is String ||
            name.firstOrNull() == license.firstOrNull()
        ) {
            emptySequence()
        } else {
            sequenceOf(ValidationError(dataTrace, "first character of license must match name.", data))
        }
    }
}

fun main() {

    val dogFeed = FlowableUtil.fromCsvWithHeader(
        dataTrace = dataTrace("file example.csv"),
        readerSupplier = { Baleen.javaClass.getResourceAsStream("/example.csv").bufferedReader() }
    )

    val validationFeed = dogFeed
        .flatMap { dogDescription.validate(it).results.toFlowable() }
        .publish()

    printErrors(validationFeed)
    printSuccessFailureCount(validationFeed)

    println("Starting...")
    val startTime = System.nanoTime()
    validationFeed.connect()

    val duration = System.nanoTime() - startTime
    println("Took ${duration / 1000_000} ms")
}
