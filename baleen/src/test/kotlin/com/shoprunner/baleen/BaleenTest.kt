package com.shoprunner.baleen

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.TestHelper.dataOf
import com.shoprunner.baleen.ValidationAssert.Companion.assertThat
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance
import kotlin.contracts.ExperimentalContracts

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BaleenTest {
    @Nested
    inner class EmptyDataSpecification {
        private val dataDesc = "Empty".describeAs()

        @Test
        fun `accepts anything`() {
            assertThat(dataDesc.validate(dataOf<String>())).isValid()
            assertThat(dataDesc.validate(dataOf("name" to "Fido"))).isValid()
        }

        @Test
        fun `context matches the data`() {
            assertThat(dataDesc.validate(dataOf<String>()).context)
                .isEqualTo(Context(dataOf<String>(), dataTrace()))

            assertThat(dataDesc.validate(dataOf("name" to "Fido")).context)
                .isEqualTo(Context(dataOf("name" to "Fido"), dataTrace()))
        }

        @Test
        fun `results in success`() {
            assertThat(dataDesc.validate(dataOf<String>()).results)
                .containsExactly(ValidationSuccess(dataTrace(), dataOf<String>()))
            assertThat(dataDesc.validate(dataOf("name" to "Fido")).results)
                .containsExactly(ValidationSuccess(dataTrace(), dataOf("name" to "Fido")))
        }
    }

    @Nested
    inner class RequiredAttribute {

        private val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            )
        }

        @Test
        fun `validates when present`() {
            val data = dataOf("name" to "Fido")
            assertThat(dogDescription.validate(data)).isValid()
            assertThat(dogDescription.validate(data).results).contains(ValidationInfo(dataTrace(), "has attribute \"name\"", data))
        }

        @Test
        fun `fails validation when data missing required attribute`() {
            val data = dataOf<String>()
            assertThat(dogDescription.validate(data)).isNotValid()
            assertThat(dogDescription.validate(data).results).contains(
                ValidationError(dataTrace(), "missing required attribute \"name\"", data)
            )
        }

        @Test
        fun `validates if data is set to null`() {
            val data = dataOf("name" to null)
            assertThat(dogDescription.validate(data)).isValid()
            assertThat(dogDescription.validate(data).results).contains(ValidationInfo(dataTrace(), "has attribute \"name\"", data))
        }
    }

    @Nested
    inner class RequiredWithDefaultAttribute {

        private val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true,
                default = "Fido"
            )
        }

        @Test
        fun `validates when present`() {
            val data = dataOf("name" to "Fido")
            assertThat(dogDescription.validate(data)).isValid()
            assertThat(dogDescription.validate(data).results).contains(ValidationInfo(dataTrace(), "has attribute \"name\"", data))
        }

        @Test
        fun `validates data missing required attribute`() {
            val data = dataOf<String>()
            assertThat(dogDescription.validate(data)).isValid()
            assertThat(dogDescription.validate(data).results).contains(ValidationInfo(dataTrace(), "has attribute \"name\" defaulted to `Fido` since it wasn't set.", data))
        }

        @Test
        fun `validates if data is set to null`() {
            val data = dataOf("name" to null)
            assertThat(dogDescription.validate(data)).isValid()
            assertThat(dogDescription.validate(data).results).contains(ValidationInfo(dataTrace(), "has attribute \"name\"", data))
        }
    }

    @Nested
    inner class AttributeAsWarnings {

        private val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            ).asWarnings()
        }

        @Test
        fun `passes validation when present`() {
            val data = dataOf("name" to "Fido")
            assertThat(dogDescription.validate(data)).isValid()
            assertThat(dogDescription.validate(data).results).contains(ValidationInfo(dataTrace(), "has attribute \"name\"", data))
        }

        @Test
        fun `warns validation when data missing required attribute`() {
            val data = dataOf<String>()
            assertThat(dogDescription.validate(data)).isValid()
            assertThat(dogDescription.validate(data).results).contains(
                ValidationWarning(dataTrace(), "missing required attribute \"name\"", data)
            )
        }
    }

    private fun nestedDescTests(packWithAlpha: DataDescription, packOptionalAlpha: DataDescription): List<DynamicTest> {
        return listOf(
            dynamicTest("validates when present") {
                val dogData = dataOf("name" to "Fido")
                val packData = dataOf(
                    "alpha" to dogData
                )
                assertThat(packWithAlpha.validate(packData)).isValid()
                assertThat(packWithAlpha.validate(packData).results)
                    .contains(ValidationInfo(dataTrace(), "has attribute \"alpha\"", packData))
                    .contains(
                        ValidationInfo(
                            dataTrace("attribute \"alpha\""),
                            "has attribute \"name\"",
                            dogData
                        )
                    )

                assertThat(packOptionalAlpha.validate(packData)).isValid()
                assertThat(packOptionalAlpha.validate(packData).results)
                    .contains(ValidationInfo(dataTrace(), "has attribute \"alpha\"", packData))
                    .contains(
                        ValidationInfo(
                            dataTrace("attribute \"alpha\""),
                            "has attribute \"name\"",
                            dogData
                        )
                    )
            },

            dynamicTest("child not the right type") {
                val data = dataOf(
                    "alpha" to "Fido"
                )
                assertThat(packWithAlpha.validate(data)).isNotValid()
                assertThat(packWithAlpha.validate(data).results)
                    .contains(ValidationInfo(dataTrace(), "has attribute \"alpha\"", data))
                    .contains(
                        ValidationError(
                            dataTrace("attribute \"alpha\""),
                            "expected to be of type Data but is class java.lang.String",
                            "Fido"
                        )
                    )

                assertThat(packOptionalAlpha.validate(data)).isNotValid()
                assertThat(packOptionalAlpha.validate(data).results)
                    .contains(ValidationInfo(dataTrace(), "has attribute \"alpha\"", data))
                    .contains(
                        ValidationError(
                            dataTrace("attribute \"alpha\""),
                            "expected to be of type Data but is class java.lang.String",
                            "Fido"
                        )
                    )
            },

            dynamicTest("non-required also validates") {
                val data = dataOf<String>()
                assertThat(packWithAlpha.validate(data)).isNotValid()
                assertThat(packWithAlpha.validate(data).results)
                    .contains(
                        ValidationError(
                            dataTrace(),
                            "missing required attribute \"alpha\"",
                            data
                        )
                    )

                assertThat(packOptionalAlpha.validate(data)).isValid()
            },

            dynamicTest("fails at parent when child missing") {
                val data = dataOf<String>()
                assertThat(packWithAlpha.validate(data)).isNotValid()
                assertThat(packOptionalAlpha.validate(data)).isValid()
                // TODO data trace
            },

            dynamicTest("fails at child when child not valid") {
                val data = dataOf(
                    "alpha" to dataOf<String>()
                )
                assertThat(packWithAlpha.validate(data)).isNotValid()
                assertThat(packOptionalAlpha.validate(data)).isNotValid()
                // TODO data trace
            },
        )
    }

    @TestFactory
    fun `named nested descriptions`(): List<DynamicTest> {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = StringType(),
                required = true
            )
        }

        val packWithAlpha = "Pack".describeAs {
            "alpha".type(
                type = dogDescription,
                required = true
            )
        }

        val packOptionalAlpha = "Pack".describeAs {
            "alpha".type(
                type = dogDescription,
                required = false
            )
        }

        return nestedDescTests(packWithAlpha, packOptionalAlpha)
    }

    @TestFactory
    fun `named nested descriptions attributes`(): List<DynamicTest> {
        val dogDescription = "Dog".describeAs {
            attr(
                name = "name",
                type = StringType(),
                required = true
            )
        }

        val packWithAlpha = "Pack".describeAs {
            attr(
                name = "alpha",
                type = dogDescription,
                required = true
            )
        }

        val packOptionalAlpha = "Pack".describeAs {
            attr(
                name = "alpha",
                type = dogDescription,
                required = false
            )
        }

        return nestedDescTests(packWithAlpha, packOptionalAlpha)
    }

    @TestFactory
    fun `anonymous nested descriptions`(): List<DynamicTest> {
        val packWithAlpha = "Pack".describeAs {
            "alpha".type(
                required = true
            ) {
                "name".type(
                    type = StringType(),
                    required = true
                )
            }
        }

        val packOptionalAlpha = "Pack".describeAs {
            "alpha".type(
                required = false
            ) {
                "name".type(
                    type = StringType(),
                    required = true
                )
            }
        }

        return nestedDescTests(packWithAlpha, packOptionalAlpha)
    }

    @TestFactory
    fun `anonymous nested descriptions attr`(): List<DynamicTest> {
        val packWithAlpha = "Pack".describeAs {
            attr(
                name = "alpha",
                required = true
            ) {
                "name".type(
                    type = StringType(),
                    required = true
                )
            }
        }

        val packOptionalAlpha = "Pack".describeAs {
            attr(
                name = "alpha",
                required = false
            ) {
                "name".type(
                    type = StringType(),
                    required = true
                )
            }
        }

        return nestedDescTests(packWithAlpha, packOptionalAlpha)
    }

    @Test
    fun `warn on extra attributes`() {
        val dataDesc = "Empty".describeAs {
            warnOnExtraAttributes()
        }

        assertThat(dataDesc.validate(dataOf<String>())).isValid()
        assertThat(dataDesc.validate(dataOf("name" to "Fido"))).isValid()

        assertThat(dataDesc.validate(dataOf<String>()).results).containsExactly(
            ValidationSuccess(dataTrace(), dataOf<String>())
        )
        assertThat(dataDesc.validate(dataOf("name" to "Fido")).results)
            .containsExactly(
                ValidationWarning(dataTrace(), "extra attribute \"name\"", dataOf("name" to "Fido")),
                ValidationSuccess(dataTrace(), dataOf("name" to "Fido"))
            )
    }

    @Test
    fun `custom test`() {
        val dataDesc = "Empty".describeAs {
            test { dataTrace, value ->
                when (value["favorite number"]) {
                    42 -> emptySequence()
                    else -> sequenceOf(ValidationError(dataTrace, "Wrong, guess again", value))
                }
            }
        }

        assertThat(dataDesc.validate(dataOf<String>())).isNotValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 42))).isValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 41))).isNotValid()
    }

    @Test
    fun `validation summary`() {
        val dataDesc = "Empty".describeAs {
            test { dataTrace, value ->
                when (value["favorite number"]) {
                    42 -> emptySequence()
                    else -> sequenceOf(ValidationError(dataTrace, "Wrong, guess again", value))
                }
            }
        }

        val data = dataOf("favorite number" to 41)
        val summary = dataDesc.validate(data).createSummary().results.toList()

        assertThat(summary).containsExactlyInAnyOrder(
            ValidationSummary(
                dataTrace = dataTrace(),
                summary = "Summary",
                numInfos = 0,
                numSuccesses = 0,
                numErrors = 1,
                numWarnings = 0,
                topErrorsAndWarnings = listOf(ValidationError(dataTrace(), "Wrong, guess again", data)),
            )
        )
    }

    @Test
    fun `validation summary grouped by tag`() {
        val dataDesc = "Empty".describeAs {
            test { dataTrace, value ->
                when (value["favorite number"]) {
                    42 -> emptySequence()
                    else -> sequenceOf(
                        ValidationInfo(dataTrace.tag("test", "false"), "Wrong, guess again", value),
                        ValidationError(dataTrace.tag("test", "true"), "Wrong, guess again", value)
                    )
                }
            }
        }

        val data = dataOf("favorite number" to 41)
        val summary = dataDesc.validate(data).createSummary(groupBy = groupByTag("test")).results.toList()

        assertThat(summary).containsExactlyInAnyOrder(
            ValidationSummary(
                dataTrace = dataTrace().tag("test", "false"),
                summary = "Summary for test=false",
                numInfos = 1,
                numSuccesses = 0,
                numErrors = 0,
                numWarnings = 0,
                topErrorsAndWarnings = emptyList(),
            ),
            ValidationSummary(
                dataTrace = dataTrace().tag("test", "true"),
                summary = "Summary for test=true",
                numInfos = 0,
                numSuccesses = 0,
                numErrors = 1,
                numWarnings = 0,
                topErrorsAndWarnings = listOf(ValidationError(dataTrace().tag("test", "true"), "Wrong, guess again", data)),
            ),
        )
    }

    @Test
    fun `junit style attribute test`() {
        val dataDesc = "FavoriteNumber".describeAs {
            "favorite number".type(IntType()).describe {
                test("favorite number is 42") { data ->
                    assertEquals("favorite number == 42", data.getAsIntOrNull("favorite number"), 42)
                }
            }
        }

        assertThat(dataDesc.validate(dataOf("favorite number" to 42))).isValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 42)).results).contains(
            ValidationInfo(dataTrace().tag("test" to "favorite number is 42", "assertion" to "favorite number == 42"), "Pass: favorite number == 42", "42 == 42")
        )
        assertThat(dataDesc.validate(dataOf("favorite number" to 41))).isNotValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 41)).results).contains(
            ValidationError(dataTrace().tag("test" to "favorite number is 42", "assertion" to "favorite number == 42"), "Fail: favorite number == 42", "41 == 42")
        )
    }

    @Test
    @ExperimentalContracts
    fun `junit style attribute test - assertThat`() {
        val dataDesc = "FavoriteNumber".describeAs {
            "favorite number".type(IntType()).describe {
                test("favorite number is 42") { data ->
                    assertThat { data.getAsInt("favorite number") }.isEqualTo(42)
                }
            }
        }

        assertThat(dataDesc.validate(dataOf("favorite number" to 42))).isValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 42)).results).contains(
            ValidationInfo(dataTrace().tag("test" to "favorite number is 42", "assertion" to "assertThat<kotlin.Int>()"), "Pass: assertThat<kotlin.Int>()", "42 is a kotlin.Int"),
            ValidationInfo(dataTrace().tag("test" to "favorite number is 42", "assertion" to "is equal to 42"), "Pass: is equal to 42", "42 == 42")
        )
        assertThat(dataDesc.validate(dataOf("favorite number" to 41))).isNotValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 41)).results).contains(
            ValidationInfo(dataTrace().tag("test" to "favorite number is 42", "assertion" to "assertThat<kotlin.Int>()"), "Pass: assertThat<kotlin.Int>()", "41 is a kotlin.Int"),
            ValidationError(dataTrace().tag("test" to "favorite number is 42", "assertion" to "is equal to 42"), "Fail: is equal to 42", "41 == 42")
        )
    }

    @Test
    fun `junit style test`() {
        val dataDesc = "FavoriteNumber".describeAs {
            test("favorite number is 42") { data ->
                assertEquals("favorite number == 42", data.getAsIntOrNull("favorite number"), 42)
            }
        }

        assertThat(dataDesc.validate(dataOf("favorite number" to 42))).isValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 42)).results).contains(
            ValidationInfo(dataTrace().tag("test" to "favorite number is 42", "assertion" to "favorite number == 42"), "Pass: favorite number == 42", "42 == 42")
        )
        assertThat(dataDesc.validate(dataOf("favorite number" to 41))).isNotValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 41)).results).contains(
            ValidationError(dataTrace().tag("test" to "favorite number is 42", "assertion" to "favorite number == 42"), "Fail: favorite number == 42", "41 == 42")
        )
    }

    @Test
    @ExperimentalContracts
    fun `junit style test - assertThat`() {
        val dataDesc = "FavoriteNumber".describeAs {
            test("favorite number is 42") { data ->
                assertThat { data.getAsInt("favorite number") }.isEqualTo(42)
            }
        }

        assertThat(dataDesc.validate(dataOf("favorite number" to 42))).isValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 42)).results).contains(
            ValidationInfo(dataTrace().tag("test" to "favorite number is 42", "assertion" to "assertThat<kotlin.Int>()"), "Pass: assertThat<kotlin.Int>()", "42 is a kotlin.Int"),
            ValidationInfo(dataTrace().tag("test" to "favorite number is 42", "assertion" to "is equal to 42"), "Pass: is equal to 42", "42 == 42")
        )
        assertThat(dataDesc.validate(dataOf("favorite number" to 41))).isNotValid()
        assertThat(dataDesc.validate(dataOf("favorite number" to 41)).results).contains(
            ValidationInfo(dataTrace().tag("test" to "favorite number is 42", "assertion" to "assertThat<kotlin.Int>()"), "Pass: assertThat<kotlin.Int>()", "41 is a kotlin.Int"),
            ValidationError(dataTrace().tag("test" to "favorite number is 42", "assertion" to "is equal to 42"), "Fail: is equal to 42", "41 == 42")
        )
    }
}
