package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import java.io.StringWriter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataClassGeneratorForSimpleModelsTest {

    @Test
    fun `test simple model with StringType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: String
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with BooleanType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = BooleanType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.Boolean
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Boolean
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with IntType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = IntType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.Int
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Int
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with IntegerType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = IntegerType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import java.math.BigInteger
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: BigInteger
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with LongType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = LongType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.Long
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Long
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with FloatType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = FloatType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.Float
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Float
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with DoubleType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = DoubleType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.Double
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Double
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with NumericType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = NumericType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import java.math.BigDecimal
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: BigDecimal
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with EnumType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = EnumType("myEnum", "Val1", "Val2"),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: String
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with InstantType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = InstantType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import java.time.Instant
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Instant
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with TimestampMillisType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = TimestampMillisType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import java.time.LocalDateTime
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: LocalDateTime
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with StringConstantType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = StringConstantType("Constant"),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: String
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with OccurrencesType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = OccurrencesType(StringType()),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            import kotlin.collections.List
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: List<String>
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with MapType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = MapType(StringType(), IntType()),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.Int
            import kotlin.String
            import kotlin.collections.Map
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Map<String, Int>
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with AllowsNull StringType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = AllowsNull(StringType()),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: String?
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with CoercibleType using From option`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = StringCoercibleToLong(LongType()),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: String
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model, Options(coercibleHandler = CoercibleHandler.FROM))
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with CoercibleType using To option`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = StringCoercibleToLong(LongType()),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.Long
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Long
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model, Options(coercibleHandler = CoercibleHandler.TO))
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with TypeOverride set in Options`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.Long
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: Long
            )

        """.trimIndent()

        val stringOverrideToLong = TypeMapOverride(
            isOverridable = { it is StringType },
            override = { Long::class }
        )
        val dataClassSpecs = DataClassGenerator.encode(model, Options(overrides = listOf(stringOverrideToLong)))
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with StringType and a default`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = StringType(),
                markdownDescription = "Test field",
                default = "Hello"
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: String = "Hello"
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }

    @Test
    fun `test simple model with AllowsNull StringType defaulting to null`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field".type(
                type = AllowsNull(StringType()),
                markdownDescription = "Test field",
                default = null
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field
               */
              val field: String? = null
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }
}
