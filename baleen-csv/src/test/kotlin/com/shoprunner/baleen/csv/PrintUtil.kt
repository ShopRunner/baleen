package com.shoprunner.baleen.csv

import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationSuccess
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.subscribeBy

object PrintUtil {
    fun printErrors(feed: Flowable<ValidationResult>) {
        var first = true
        feed.filter({ it is ValidationError }).map { it as ValidationError }
                .subscribeBy(
                        onNext = {
                            if (first) { println("Errors:"); first = false }
                            println("- ${it.dataTrace} ${it.value} ${it.message}") },
                        onError = { println(it) },
                        onComplete = { println() }
                )
    }

    fun printSuccessFailureCount(feed: Flowable<ValidationResult>) {
        val successes = feed.filter({ it is ValidationSuccess }).count()
        val failures = feed.filter({ it is ValidationError }).count()
        Singles.zip(successes, failures, { successCount, failureCount ->
            """
          |$successCount Successfully Validated
          |$failureCount Failed Validation
        """.trimMargin()
        }).subscribeBy(
                onSuccess = { println(it) },
                onError = { println(it) }
        )
    }
}