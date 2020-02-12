package com.shoprunner.baleen.types

import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationWarning
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.datawrappers.HashData
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ErrorsAreWarningsTest {

    @Test
    fun `test ErrorsAreWarnings wrap BaleenType`() {
        val type = ErrorsAreWarnings(StringType())
        val correctResults = type.validate(dataTrace(), "Correct")
        assertThat(correctResults).isEmpty()

        val warningResults = type.validate(dataTrace(), 1234)
        assertThat(warningResults).containsExactly(ValidationWarning(dataTrace(), "is not a string", 1234))
    }

    @Test
    fun `test BaleenType#isWarning transforms BaleenType to ErrorsAreWarning`() {
        val type = StringType().asWarnings()
        val correctResults = type.validate(dataTrace(), "Correct")
        assertThat(correctResults).isEmpty()

        val warningResults = type.validate(dataTrace(), 1234)
        assertThat(warningResults).containsExactly(ValidationWarning(dataTrace(), "is not a string", 1234))
    }

    @Test
    fun `test Validator#isWarning returns a function that transforms ValidationErrors to ValidationWarnings`() {
        fun myKeyCheck(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> =
            if (!data.containsKey("key")) {
                sequenceOf(ValidationError(dataTrace, "key not found", data))
            } else {
                emptySequence()
            }

        val myKeyWarning = ::myKeyCheck.asWarnings()

        val correctResults = myKeyWarning(dataTrace(), HashData(mapOf("key" to "value")))
        assertThat(correctResults).isEmpty()

        val badData = HashData(mapOf("nokey" to "value"))
        val warningResults = myKeyWarning(dataTrace(), badData)
        assertThat(warningResults).containsExactly(ValidationWarning(dataTrace(), "key not found", badData))
    }

    @Test
    fun `test Sequence#isWarning returns a function that transforms ValidationErrors to ValidationWarnings`() {
        val results = sequenceOf(
            ValidationInfo(dataTrace(), "good data", "hello world"),
            ValidationError(dataTrace(), "bad data", null)
        )
        val warningResults = results.asWarnings()
        assertThat(warningResults).containsExactly(
            ValidationInfo(dataTrace(), "good data", "hello world"),
            ValidationWarning(dataTrace(), "bad data", null)
        )
    }
}
