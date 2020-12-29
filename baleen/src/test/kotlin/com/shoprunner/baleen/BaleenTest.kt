package com.shoprunner.baleen

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.TestHelper.dataOf
import com.shoprunner.baleen.ValidationAssert.Companion.assertThat
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance

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
                .isEqualTo(listOf(ValidationSuccess(dataTrace(), dataOf<String>())))
            assertThat(dataDesc.validate(dataOf("name" to "Fido")).results)
                .isEqualTo(listOf(ValidationSuccess(dataTrace(), dataOf("name" to "Fido"))))
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

        assertThat(dataDesc.validate(dataOf<String>()).results)
            .isEqualTo(listOf(ValidationSuccess(dataTrace(), dataOf<String>())))
        assertThat(dataDesc.validate(dataOf("name" to "Fido")).results)
            .isEqualTo(
                listOf(
                    ValidationWarning(dataTrace(), "extra attribute \"name\"", dataOf("name" to "Fido")),
                    ValidationSuccess(dataTrace(), dataOf("name" to "Fido"))
                )
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
}
