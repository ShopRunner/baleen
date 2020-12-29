package com.shoprunner.baleen.poet

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.ErrorsAreWarnings
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongCoercibleToInstant
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToBoolean
import com.shoprunner.baleen.types.StringCoercibleToFloat
import com.shoprunner.baleen.types.StringCoercibleToInstant
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringCoercibleToOccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToTimestamp
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.Tagged
import com.shoprunner.baleen.types.TimestampMillisType
import com.shoprunner.baleen.types.UnionType
import com.shoprunner.baleen.types.withAttributeValue
import com.shoprunner.baleen.types.withConstantValue
import com.shoprunner.baleen.types.withValue
import com.squareup.kotlinpoet.CodeBlock
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.time.format.DateTimeFormatter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BaleenPoetTest {

    @Test
    fun `test write BooleanType`() {
        val type = BooleanType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.BooleanType
                
                public valboolean: BaleenType = BooleanType()
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write DoubleType`() {
        val type = DoubleType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.DoubleType
                
                public valdouble: BaleenType = DoubleType(min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write DoubleType with max and min`() {
        val type = DoubleType(min = 0.0, max = 10.0)
        val spec = type.toFileSpec(name = "DoubleMaxMin")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.DoubleType
                
                public valDoubleMaxMin: BaleenType = DoubleType(min = 0.0, max = 10.0)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write FloatType`() {
        val type = FloatType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.FloatType
                
                public valfloat: BaleenType = FloatType(min = Float.NEGATIVE_INFINITY, max = Float.POSITIVE_INFINITY)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write FloatType with max and min`() {
        val type = FloatType(min = 0.0f, max = 10.0f)
        val spec = type.toFileSpec(name = "FloatMaxMin")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.FloatType
                
                public valFloatMaxMin: BaleenType = FloatType(min = 0.0f, max = 10.0f)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write IntType`() {
        val type = IntType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.IntType
                
                public valint: BaleenType = IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write IntType with max and min`() {
        val type = IntType(min = 0, max = 10)
        val spec = type.toFileSpec(name = "IntMaxMin")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.IntType
                
                public valIntMaxMin: BaleenType = IntType(min = 0, max = 10)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write LongType`() {
        val type = LongType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.LongType
                
                public vallong: BaleenType = LongType(min = Long.MIN_VALUE, max = Long.MAX_VALUE)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write LongType with max and min`() {
        val type = LongType(min = 0, max = 10)
        val spec = type.toFileSpec(name = "LongMaxMin")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.LongType
                
                public valLongMaxMin: BaleenType = LongType(min = 0L, max = 10L)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write IntegerType`() {
        val type = IntegerType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.IntegerType
                
                public valinteger: BaleenType = IntegerType(min = null, max = null)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write IntegerType with max and min`() {
        val type = IntegerType(min = 0.toBigInteger(), max = 10.toBigInteger())
        val spec = type.toFileSpec(name = "IntegerMaxMin")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.IntegerType
                
                public valIntegerMaxMin: BaleenType = IntegerType(min = "0".toBigInteger(), max = "10".toBigInteger())
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write NumericType`() {
        val type = NumericType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.NumericType
                
                public valnumber: BaleenType = NumericType(min = null, max = null)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write NumericType with max and min`() {
        val type = NumericType(min = 0.toBigDecimal(), max = 10.toBigDecimal())
        val spec = type.toFileSpec(name = "NumberMaxMin")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.NumericType
                
                public valNumberMaxMin: BaleenType = NumericType(min = "0".toBigDecimal(), max = "10".toBigDecimal())
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write EnumType`() {
        val type = EnumType("myEnum", "A", "B", "C")
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.EnumType
                
                public val `enum`: BaleenType = EnumType("myEnum", listOf("A", "B", "C"))
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringConstantType`() {
        val type = StringConstantType("Hello World")
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.StringConstantType
                
                public valstringConstant: BaleenType = StringConstantType("Hello World")
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringType`() {
        val type = StringType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.StringType
                
                public valstring: BaleenType = StringType(min = 0, max = Int.MAX_VALUE)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringType with max and min`() {
        val type = StringType(min = 10, max = 20)
        val spec = type.toFileSpec(name = "StringMaxMin")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.StringType
                
                public valStringMaxMin: BaleenType = StringType(min = 10, max = 20)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write InstantType`() {
        val type = InstantType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.InstantType
                import java.time.Instant
                
                public valinstant: BaleenType = InstantType(before = Instant.MAX, after = Instant.MIN)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write InstantType with before and after`() {
        val type = InstantType(before = Instant.parse("2020-02-02T02:02:02Z"), after = Instant.parse("2001-01-01T01:01:01Z"))
        val spec = type.toFileSpec(name = "InstantBeforeAfter")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.InstantType
                import java.time.Instant
                
                public valInstantBeforeAfter: BaleenType = InstantType(before = Instant.parse("2020-02-02T02:02:02Z"), 
                                                                 after = Instant.parse("2001-01-01T01:01:01Z"))
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write TimestampMillisType`() {
        val type = TimestampMillisType()
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.TimestampMillisType
                
                public valtimestampMillis: BaleenType = TimestampMillisType()
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write BaleenType with package and filename set`() {
        val type = StringType()
        val spec = type.toFileSpec("com.shoprunner.baleen.poet.test", "MyString")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                package com.shoprunner.baleen.poet.test
                
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.StringType
                
                public valMyString: BaleenType = StringType(min = 0, max = Int.MAX_VALUE)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write AllowsNull`() {
        val type = AllowsNull(BooleanType())
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.AllowsNull
                import com.shoprunner.baleen.types.BooleanType
                
                public valnullOrBoolean: BaleenType = AllowsNull(BooleanType())
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write ErrorsAreWarnings`() {
        val type = ErrorsAreWarnings(BooleanType())
        val spec = type.toFileSpec(name = "ErrorsAreWarnings")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.BooleanType
                import com.shoprunner.baleen.types.ErrorsAreWarnings

                public valErrorsAreWarnings: BaleenType = ErrorsAreWarnings(BooleanType())
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write Tagged`() {
        val type = Tagged(
            BooleanType(),
            "constantTag" to withConstantValue("value"),
            "valueTag" to withValue(),
            "attributeTag" to withAttributeValue("attr"),
            "customTag" to { _ -> "custom" }
        )
        val spec = type.toFileSpec(name = "Tagged")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.BooleanType
                import com.shoprunner.baleen.types.Tagged
                import com.shoprunner.baleen.types.withAttributeValue
                import com.shoprunner.baleen.types.withConstantValue
                import com.shoprunner.baleen.types.withValue

                public valTagged: BaleenType = Tagged(BooleanType(), 
                    mapOf(
                        "constantTag" to withConstantValue("value"),
                        "valueTag" to withValue(),
                        "attributeTag" to withAttributeValue("attr")
                    )
                )
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write OccurrencesType`() {
        val type = OccurrencesType(StringType())
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.OccurrencesType
                import com.shoprunner.baleen.types.StringType
                
                public valmultipleOccurrencesOfString: BaleenType = OccurrencesType(StringType(min = 0, max = Int.MAX_VALUE))
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write MapType`() {
        val type = MapType(StringType(), IntType())
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.IntType
                import com.shoprunner.baleen.types.MapType
                import com.shoprunner.baleen.types.StringType
                
                public valmapOfOccurrencesOfStringToInt: BaleenType = MapType(
                    StringType(min = 0, max = Int.MAX_VALUE), 
                    IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE)
                )
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write UnionType`() {
        val type = UnionType(StringType(), IntType())
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.IntType
                import com.shoprunner.baleen.types.StringType
                import com.shoprunner.baleen.types.UnionType
                
                public valunionOfStringInt: BaleenType = UnionType(
                    StringType(min = 0, max = Int.MAX_VALUE), 
                    IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE)
                )
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write data description`() {
        val type = "Dog".describeAs {
            "name".type(StringType())
            "numLegs".type(AllowsNull(IntType()))
        }

        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.AllowsNull
            import com.shoprunner.baleen.types.IntType
            import com.shoprunner.baleen.types.StringType
            
            public valDog: DataDescription = describe("Dog", "", "") {
                  it.attr(
                    name = "name",
                    type = StringType(min = 0, max = Int.MAX_VALUE)
                  )
                  it.attr(
                    name = "numLegs",
                    type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
                  )
            
                }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write data description in specific namespace`() {
        val type = "Dog".describeAs("com.shoprunner.baleen.poet.test") {
            "name".type(StringType())
            "numLegs".type(AllowsNull(IntType()))
        }

        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
            package com.shoprunner.baleen.poet.test
                
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.AllowsNull
            import com.shoprunner.baleen.types.IntType
            import com.shoprunner.baleen.types.StringType
            
            public valDog: DataDescription = describe("Dog", "com.shoprunner.baleen.poet.test", "") {
                  it.attr(
                    name = "name",
                    type = StringType(min = 0, max = Int.MAX_VALUE)
                  )
                  it.attr(
                    name = "numLegs",
                    type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
                  )
            
                }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write data description with comments`() {
        val type = "Dog".describeAs(markdownDescription = "This is a Dog") {
            "name".type(StringType(), markdownDescription = "The Dog's name")
            "numLegs".type(AllowsNull(IntType()), markdownDescription = "The number of legs the Dog has")
        }

        val spec = type.toFileSpec(name = "DogWithComments")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.AllowsNull
                import com.shoprunner.baleen.types.IntType
                import com.shoprunner.baleen.types.StringType
                
                /**
                 * This is a Dog
                 */
                public valDogWithComments: DataDescription = describe("Dog", "", "This is a Dog") {
                      it.attr(
                        name = "name",
                        type = StringType(min = 0, max = Int.MAX_VALUE),
                        markdownDescription = "The Dog's name"
                      )
                      it.attr(
                        name = "numLegs",
                        type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE)),
                        markdownDescription = "The number of legs the Dog has"
                      )
                
                    }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write data description with aliases`() {
        val type = "Dog".describeAs {
            "name".type(StringType(), aliases = arrayOf("dogName"))
            "numLegs".type(AllowsNull(IntType()), aliases = arrayOf("nLegs", "numberOfLegs"))
        }

        val spec = type.toFileSpec(name = "DogWithAliases")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.AllowsNull
                import com.shoprunner.baleen.types.IntType
                import com.shoprunner.baleen.types.StringType
                
                public valDogWithAliases: DataDescription = describe("Dog", "", "") {
                      it.attr(
                        name = "name",
                        type = StringType(min = 0, max = Int.MAX_VALUE),
                        aliases = arrayOf("dogName")
                      )
                      it.attr(
                        name = "numLegs",
                        type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE)),
                        aliases = arrayOf("nLegs", "numberOfLegs")
                      )
                
                    }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write data description with defaults`() {
        val type = "Dog".describeAs {
            "name".type(StringType(), default = "Fido")
            "numLegs".type(AllowsNull(IntType()), default = null)
            "owners".type(OccurrencesType(StringType()), default = emptyList<String>())
            "walkers".type(MapType(StringType(), StringType()), default = emptyMap<String, String>())
            "badge".type(EnumType("Badges", TestEnum.values()), default = TestEnum.One)
        }

        val spec = type.toFileSpec(name = "DogWithDefaults")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.poet.TestEnum
                import com.shoprunner.baleen.types.AllowsNull
                import com.shoprunner.baleen.types.EnumType
                import com.shoprunner.baleen.types.IntType
                import com.shoprunner.baleen.types.MapType
                import com.shoprunner.baleen.types.OccurrencesType
                import com.shoprunner.baleen.types.StringType
                
                public valDogWithDefaults: DataDescription = describe("Dog", "", "") {
                      it.attr(
                        name = "name",
                        type = StringType(min = 0, max = Int.MAX_VALUE),
                        default = "Fido"
                      )
                      it.attr(
                        name = "numLegs",
                        type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE)),
                        default = null
                      )
                      it.attr(
                        name = "owners",
                        type = OccurrencesType(StringType(min = 0, max = Int.MAX_VALUE)),
                        default = emptyList<Any?>()
                      )
                      it.attr(
                        name = "walkers",
                        type = MapType(StringType(min = 0, max = Int.MAX_VALUE), StringType(min = 0, max =
                            Int.MAX_VALUE)),
                        default = emptyMap<Any?, Any?>()
                      )
                      it.attr(
                        name = "badge",
                        type = EnumType("Badges", listOf("One")),
                        default = TestEnum.One
                      )
                    }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write data description with required attributes`() {
        val type = "Dog".describeAs {
            "name".type(StringType(), required = true)
            "numLegs".type(AllowsNull(IntType()), required = true)
        }

        val spec = type.toFileSpec(name = "DogWithRequiredAttrs")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.AllowsNull
                import com.shoprunner.baleen.types.IntType
                import com.shoprunner.baleen.types.StringType
                
                public valDogWithRequiredAttrs: DataDescription = describe("Dog", "", "") {
                      it.attr(
                        name = "name",
                        type = StringType(min = 0, max = Int.MAX_VALUE),
                        required = true
                      )
                      it.attr(
                        name = "numLegs",
                        type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE)),
                        required = true
                      )
                
                    }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write data description with attribute asWarnings`() {
        val type = "Dog".describeAs {
            "name".type(StringType()).asWarnings()
            "numLegs".type(AllowsNull(IntType()))
        }

        val spec = type.toFileSpec(name = "DogsWithAttributeWarnings")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.AllowsNull
            import com.shoprunner.baleen.types.IntType
            import com.shoprunner.baleen.types.StringType
            
            public valDogsWithAttributeWarnings: DataDescription = describe("Dog", "", "") {
                  it.attr(
                    name = "name",
                    type = StringType(min = 0, max = Int.MAX_VALUE)
                  ).asWarnings()
                  it.attr(
                    name = "numLegs",
                    type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
                  )
            
                }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write data description with attribute tags`() {
        val type = "Dog".describeAs {
            "name".type(StringType())
            "numLegs".type(AllowsNull(IntType()))
                .tag("constantTag" to withConstantValue("value"))
                .tag("valueTag" to withValue())
                .tag("attributeTag" to withAttributeValue("attr"))
                .tag("customTag" to { _ -> "custom" })
        }

        val spec = type.toFileSpec(name = "DogsWithAttributeTags")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.AllowsNull
            import com.shoprunner.baleen.types.IntType
            import com.shoprunner.baleen.types.StringType
            import com.shoprunner.baleen.types.withAttributeValue
            import com.shoprunner.baleen.types.withConstantValue
            import com.shoprunner.baleen.types.withValue
            
            public valDogsWithAttributeTags: DataDescription = describe("Dog", "", "") {
                  it.attr(
                    name = "name",
                    type = StringType(min = 0, max = Int.MAX_VALUE)
                  )
                  it.attr(
                    name = "numLegs",
                    type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
                  )
                  .tag("constantTag" to withConstantValue("value"))
                  .tag("valueTag" to withValue())
                  .tag("attributeTag" to withAttributeValue("attr"))
                }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write nested data description`() {
        val dog = "NestedDog".describeAs {
            "name".type(StringType())
            "numLegs".type(AllowsNull(IntType()))
        }

        val pack = "Pack".describeAs {
            "dogs".type(OccurrencesType(dog))
        }

        val allSpecs = pack.generateAllFileSpecs().sortedBy { it.name }

        val dogSpec = """
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.AllowsNull
            import com.shoprunner.baleen.types.IntType
            import com.shoprunner.baleen.types.StringType
            
            public valNestedDog: DataDescription = describe("NestedDog", "", "") {
                  it.attr(
                    name = "name",
                    type = StringType(min = 0, max = Int.MAX_VALUE)
                  )
                  it.attr(
                    name = "numLegs",
                    type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
                  )
            
                }
        """.trimIndent()

        val packSpec = """
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.OccurrencesType
            
            public val Pack: DataDescription = describe("Pack", "", "") {
                  it.attr(
                    name = "dogs",
                    type = OccurrencesType(NestedDog)
                  )
            
                }
        """.trimIndent()

        assertSoftly {
            assertThat(allSpecs).isEqualToIgnoringWhitespace(
                listOf(
                    dogSpec, packSpec
                )
            )
            assertThat(allSpecs).canCompile()
        }
    }

    @Test
    fun `test write nested data description different namespaces`() {
        val dog = "NestedDog".describeAs(nameSpace = "com.shoprunner.dogs") {
            "name".type(StringType())
            "numLegs".type(AllowsNull(IntType()))
        }

        val pack = "Pack".describeAs(nameSpace = "com.shoprunner.pack") {
            "dogs".type(OccurrencesType(dog))
        }

        val allSpecs = pack.generateAllFileSpecs().sortedBy { it.name }

        val dogSpec = """
            package com.shoprunner.dogs
            
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.AllowsNull
            import com.shoprunner.baleen.types.IntType
            import com.shoprunner.baleen.types.StringType
            
            public valNestedDog: DataDescription = describe("NestedDog", "com.shoprunner.dogs", "") {
                  it.attr(
                    name = "name",
                    type = StringType(min = 0, max = Int.MAX_VALUE)
                  )
                  it.attr(
                    name = "numLegs",
                    type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
                  )
            
                }
        """.trimIndent()

        val packSpec = """
            package com.shoprunner.pack
            
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.OccurrencesType
            import com.shoprunner.dogs.NestedDog
            
            public valPack: DataDescription = describe("Pack", "com.shoprunner.pack", "") {
                  it.attr(
                    name = "dogs",
                    type = OccurrencesType(NestedDog)
                  )
            
                }
        """.trimIndent()

        assertSoftly {
            assertThat(allSpecs).isEqualToIgnoringWhitespace(
                listOf(
                    dogSpec, packSpec
                )
            )
            assertThat(allSpecs).canCompile()
        }
    }

    @Test
    fun `test write StringCoercibleToBoolean`() {
        val type = StringCoercibleToBoolean(BooleanType())
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.BooleanType
                import com.shoprunner.baleen.types.StringCoercibleToBoolean
                
                public valstringCoercibleToBoolean: BaleenType = StringCoercibleToBoolean(BooleanType())

                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringCoercibleToFloat`() {
        val type = StringCoercibleToFloat(FloatType(min = 0.0f, max = 1.0f))
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.FloatType
                import com.shoprunner.baleen.types.StringCoercibleToFloat
                
                public valstringCoercibleToFloat: BaleenType = StringCoercibleToFloat(FloatType(min = 0.0f, max = 1.0f))

                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringCoercibleToLong`() {
        val type = StringCoercibleToLong(LongType(min = 0, max = 10))
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.LongType
                import com.shoprunner.baleen.types.StringCoercibleToLong
                
                public val stringCoercibleToLong: BaleenType = StringCoercibleToLong(LongType(min = 0L, max = 10L))

                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringCoercibleToInstant`() {
        val type = StringCoercibleToInstant(InstantType())
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.InstantType
                import com.shoprunner.baleen.types.StringCoercibleToInstant
                import java.time.Instant
                import java.time.format.DateTimeFormatter.ISO_INSTANT
                
                public valstringCoercibleToInstant: BaleenType =
                    StringCoercibleToInstant(InstantType(before = Instant.MAX, after = Instant.MIN), ISO_INSTANT)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringCoercibleToInstant with different date time pattern`() {
        val type = StringCoercibleToInstant(InstantType(), "YYYY/mm/DD/hh/MM/ss/Z")
        val spec = type.toFileSpec(name = "StringCoercibleToInstantDifferentPattern")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.InstantType
                import com.shoprunner.baleen.types.StringCoercibleToInstant
                import java.time.Instant
                import java.time.format.DateTimeFormatter.ofPattern
                
                public valStringCoercibleToInstantDifferentPattern: BaleenType =
                    StringCoercibleToInstant(InstantType(before = Instant.MAX, after = Instant.MIN),
                        ofPattern("YYYY/mm/DD/hh/MM/ss/Z"))
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringCoercibleToInstant with different format`() {
        val type = StringCoercibleToInstant(InstantType(), DateTimeFormatter.ISO_ZONED_DATE_TIME)

        assertThrows<BaleenPoetException> { type.toFileSpec(name = "StringCoercibleToInstantDifferentFormat") }
    }

    @Test
    fun `test write StringCoercibleToTimestamp`() {
        val type = StringCoercibleToTimestamp(TimestampMillisType())
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.StringCoercibleToTimestamp
                import com.shoprunner.baleen.types.TimestampMillisType
                import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
                
                public valstringCoercibleToTimestampMillis: BaleenType =
                    StringCoercibleToTimestamp(TimestampMillisType(), ISO_LOCAL_DATE_TIME)

                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringCoercibleToTimestamp with different date time pattern`() {
        val type = StringCoercibleToTimestamp(TimestampMillisType(), "yyyy/MM/dd/hh/mm/ss")
        val spec = type.toFileSpec(name = "StringCoercibleToTimestampDifferentPattern")

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                    import com.shoprunner.baleen.BaleenType
                    import com.shoprunner.baleen.types.StringCoercibleToTimestamp
                    import com.shoprunner.baleen.types.TimestampMillisType
                    import java.time.format.DateTimeFormatter.ofPattern
                    
                    public valStringCoercibleToTimestampDifferentPattern: BaleenType =
                        StringCoercibleToTimestamp(
                            TimestampMillisType(),
                            ofPattern("yyyy/MM/dd/hh/mm/ss")
                        )
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write StringCoercibleToTimestamp with different format`() {
        val type = StringCoercibleToTimestamp(TimestampMillisType(), DateTimeFormatter.ISO_ZONED_DATE_TIME)
        assertThrows<BaleenPoetException> { type.toFileSpec(name = "StringCoercibleToTimestampDifferentFormat") }
    }

    @Test
    fun `test write StringCoercibleToOccurrencesType`() {
        val type = StringCoercibleToOccurrencesType(OccurrencesType(StringType()))
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.OccurrencesType
                import com.shoprunner.baleen.types.StringCoercibleToOccurrencesType
                import com.shoprunner.baleen.types.StringType
                
                public valstringCoercibleToMultipleOccurrencesOfString: BaleenType =
                    StringCoercibleToOccurrencesType(OccurrencesType(StringType(min = 0, max = Int.MAX_VALUE)))
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test write LongCoercibleToInstant`() {
        val type = LongCoercibleToInstant(InstantType())
        val spec = type.toFileSpec()

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
                import com.shoprunner.baleen.BaleenType
                import com.shoprunner.baleen.types.InstantType
                import com.shoprunner.baleen.types.LongCoercibleToInstant
                import com.shoprunner.baleen.types.LongCoercibleToInstant.Precision.millis
                import java.time.Instant
                
                public vallongCoercibleToInstant: BaleenType = LongCoercibleToInstant(InstantType(before = Instant.MAX,
                    after = Instant.MIN), millis)
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test overriding default type mapping`() {
        val type = StringType()
        fun customOverride(builder: CodeBlock.Builder, baleenType: BaleenType): CodeBlock.Builder =
            when (baleenType) {
                is StringType -> builder.add("%T()", IntType::class)
                else -> defaultTypeMapper(builder, baleenType, ::customOverride)
            }
        val spec = type.toFileSpec(name = "StringOverrideToInt", typeMapper = ::customOverride)

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
            import com.shoprunner.baleen.BaleenType
            import com.shoprunner.baleen.types.IntType
            
            public valStringOverrideToInt: BaleenType = IntType()
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test overriding default type mapping in DataDescription`() {
        val type = "Dog".describeAs {
            "name".type(StringType())
            "numLegs".type(AllowsNull(IntType()))
        }
        fun customOverride(builder: CodeBlock.Builder, baleenType: BaleenType): CodeBlock.Builder =
            when (baleenType) {
                is StringType -> builder.add("%T()", IntType::class)
                else -> defaultTypeMapper(builder, baleenType, ::customOverride)
            }
        val spec = type.toFileSpec(name = "DogWithStringOverrideToInt", typeMapper = ::customOverride)

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.AllowsNull
            import com.shoprunner.baleen.types.IntType
            
            public valDogWithStringOverrideToInt: DataDescription = describe("Dog", "", "") {
                  it.attr(
                    name = "name",
                    type = IntType()
                  )
                  it.attr(
                    name = "numLegs",
                    type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
                  )
            
                }
                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }

    @Test
    fun `test overriding nested DataDescription type mapping in DataDescription`() {
        val dog = "Dog".describeAs {
            "name".type(StringType())
            "numLegs".type(AllowsNull(IntType()))
        }
        val pack = "Pack".describeAs {
            "dogs".type(OccurrencesType(dog))
        }

        fun customOverride(builder: CodeBlock.Builder, baleenType: BaleenType): CodeBlock.Builder =
            when {
                baleenType is DataDescription && baleenType.name == "Dog" -> builder.add("%T()", StringType::class)
                else -> defaultTypeMapper(builder, baleenType, ::customOverride)
            }
        val spec = pack.toFileSpec(name = "PackWithDogOverrideToString", typeMapper = ::customOverride)

        assertSoftly {
            assertThat(spec).isEqualToIgnoringWhitespace(
                """
            import com.shoprunner.baleen.Baleen.describe
            import com.shoprunner.baleen.DataDescription
            import com.shoprunner.baleen.types.OccurrencesType
            import com.shoprunner.baleen.types.StringType
            
            public valPackWithDogOverrideToString: DataDescription = describe("Pack", "", "") {
                  it.attr(
                    name = "dogs",
                    type = OccurrencesType(StringType())
                  )
            
                }

                """.trimIndent()
            )
            assertThat(spec).canCompile()
        }
    }
}
