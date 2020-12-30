package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.io.StringWriter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataClassGeneratorForNestedModelsTest {

    @Test
    fun `test nested model with StringType`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(model)
        }

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Child
             */
            @DataDescription
            public data class Child(
              /**
               * Test field
               */
              public val field1: String
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: Child
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel)
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with AllowsNull`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = AllowsNull(StringType()),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(AllowsNull(model))
        }

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Child
             */
            @DataDescription
            public data class Child(
              /**
               * Test field
               */
              public val field1: String?
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: Child?
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel)
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with Occurrences`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(OccurrencesType(model))
        }

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Child
             */
            @DataDescription
            public data class Child(
              /**
               * Test field
               */
              public val field1: String
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.collections.List
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: List<Child>
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel)
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with Map Values`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(MapType(StringType(), model))
        }

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Child
             */
            @DataDescription
            public data class Child(
              /**
               * Test field
               */
              public val field1: String
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            import kotlin.collections.Map
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: Map<String, Child>
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel)
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with Map Keys`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(MapType(model, StringType()))
        }

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Child
             */
            @DataDescription
            public data class Child(
              /**
               * Test field
               */
              public val field1: String
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            import kotlin.collections.Map
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: Map<Child, String>
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel)
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with Coercible TO Handler`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(StringCoercibleToType(model) { null })
        }

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Child
             */
            @DataDescription
            public data class Child(
              /**
               * Test field
               */
              public val field1: String
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: Child
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel, Options(coercibleHandler = CoercibleHandlerOption.TO))
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with Coercible FROM Handler with simple FROM`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(StringCoercibleToType(model) { null })
        }

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: String
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel, Options(coercibleHandler = CoercibleHandlerOption.FROM))
        assertThat(dataClassSpecs).hasSize(1)

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with Coercible FROM Handler with complex FROM currently fails`() {
        open class ComplexCoercible(type: StringType) : CoercibleType<DataDescription, StringType>(type) {
            override fun name() = "complex coercible"

            override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
                emptySequence()
        }

        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(ComplexCoercible(StringType()))
        }

        val e = assertThrows<IllegalArgumentException> {
            DataClassGenerator.encode(parentModel, Options(coercibleHandler = CoercibleHandlerOption.FROM))
        }
        assertThat(e.message).startsWith("Unable to handle CoercibleType FROM type for Type")
        assertThat(e.message).endsWith("class com.shoprunner.baleen.DataDescription")
    }

    @Test
    fun `test recursive model`() {
        val model = "Recursive".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Recursively nested"
        )
        model.attr("field1", model)

        val expectedClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            
            /**
             * Test Recursively nested
             */
            @DataDescription
            public data class Recursive(
              public val field1: Recursive
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        assertThat(dataClassSpecs).hasSize(1)

        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val str = outputStream.toString()

        assertThat(str).isEqualTo(expectedClassStr)
    }

    @Test
    fun `test recursive nested model`() {
        val childModel = "RecursiveChild".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test"
        )

        val parentModel = "RecursiveParent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test"
        )

        childModel.attr("parent", parentModel)
        parentModel.attr("child", childModel)

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription

            @DataDescription
            public data class RecursiveChild(
              public val parent: RecursiveParent
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription

            @DataDescription
            public data class RecursiveParent(
              public val child: RecursiveChild
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel)
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[childModel]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with type override`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(model)
        }

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: String
            )

        """.trimIndent()

        val childOverrideToString = TypeOverride(
            isOverridable = { it.name() == model.name },
            override = { String::class }
        )
        val dataClassSpecs = DataClassGenerator.encode(parentModel, Options(typeOverrides = listOf(childOverrideToString)))
        assertThat(dataClassSpecs).hasSize(1)

        val outputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(outputStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with defaults`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field",
                default = "Hello"
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(model, default = "hmmm this works but won't compile")
        }

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Child
             */
            @DataDescription
            public data class Child(
              /**
               * Test field
               */
              public val field1: String = "Hello"
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: Child = "hmmm this works but won't compile"
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel)
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }

    @Test
    fun `test nested model with null defaults`() {
        val model = "Child".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Child"
        ) {
            "field1".type(
                type = AllowsNull(StringType()),
                markdownDescription = "Test field",
                default = null
            )
        }
        val parentModel = "Parent".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Parent"
        ) {
            "child".type(AllowsNull(model), default = null)
        }

        val expectedChildClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            import kotlin.String
            
            /**
             * Test Child
             */
            @DataDescription
            public data class Child(
              /**
               * Test field
               */
              public val field1: String? = null
            )

        """.trimIndent()

        val expectedParentClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.`annotation`.DataDescription
            
            /**
             * Test Parent
             */
            @DataDescription
            public data class Parent(
              public val child: Child? = null
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(parentModel)
        assertThat(dataClassSpecs).hasSize(2)

        val childOutputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(childOutputStream)
        val childStr = childOutputStream.toString()

        val parentOutputStream = StringWriter()
        dataClassSpecs[parentModel]!!.writeTo(parentOutputStream)
        val parentStr = parentOutputStream.toString()

        assertThat(childStr).isEqualTo(expectedChildClassStr)
        assertThat(parentStr).isEqualTo(expectedParentClassStr)
    }
}
