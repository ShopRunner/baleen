package com.shoprunner.baleen.avro

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.avro.BaleenGenerator.encode
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import org.apache.avro.JsonProperties
import org.apache.avro.LogicalType
import org.apache.avro.Schema
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.net.URLClassLoader
import java.util.logging.Logger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BaleenGeneratorTest {

    fun codeToString(codeBlock: CodeBlock): String {
        val strBuilder = StringBuilder()
        FileSpec.builder("", "Test")
                .addProperty(PropertySpec.builder("test", BaleenType::class)
                        .initializer(codeBlock)
                        .build())
                .build()
                .writeTo(strBuilder)
        return strBuilder.toString()
    }

    @Nested
    inner class AvroField2Baleen {

        @Test
        fun `processField converts field`() {
            val f = Schema.Field("name", Schema.create(Schema.Type.INT), "description", 0)
            val code = BaleenGenerator.processField(f)
            Assertions.assertThat(codeToString(code)).contains("p.attr(")
            Assertions.assertThat(codeToString(code)).contains("name = \"name\"")
            Assertions.assertThat(codeToString(code)).contains("type = IntType()")
            Assertions.assertThat(codeToString(code)).contains("markdownDescription = \"description\"")
            Assertions.assertThat(codeToString(code)).contains("required = true")
        }

        @Test
        fun `processField converts field with aliases`() {
            val f = Schema.Field("name", Schema.create(Schema.Type.INT), "description", 0)
            f.addAlias("aliasName")
            val code = BaleenGenerator.processField(f)
            Assertions.assertThat(codeToString(code)).contains("p.attr(")
            Assertions.assertThat(codeToString(code)).contains("name = \"name\"")
            Assertions.assertThat(codeToString(code)).contains("type = IntType()")
            Assertions.assertThat(codeToString(code)).contains("markdownDescription = \"description\"")
            Assertions.assertThat(codeToString(code)).contains("aliases = arrayOf(\"aliasName\")")
            Assertions.assertThat(codeToString(code)).contains("required = true")
        }

        @Test
        fun `processField converts optional field`() {
            val fSchema = Schema.createUnion(Schema.create(Schema.Type.NULL), Schema.create(Schema.Type.INT))
            val f = Schema.Field("name", fSchema, "description", JsonProperties.NULL_VALUE)
            val code = BaleenGenerator.processField(f)
            Assertions.assertThat(codeToString(code)).contains("p.attr(")
            Assertions.assertThat(codeToString(code)).contains("name = \"name\"")
            Assertions.assertThat(codeToString(code)).contains("type = AllowsNull(IntType())")
            Assertions.assertThat(codeToString(code)).contains("markdownDescription = \"description\"")
            Assertions.assertThat(codeToString(code)).contains("required = false")
        }

        @Test
        fun `processField converts nullable required field`() {
            val fSchema = Schema.createUnion(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL))
            val f = Schema.Field("name", fSchema, "description", 0)
            val code = BaleenGenerator.processField(f)
            Assertions.assertThat(codeToString(code)).contains("p.attr(")
            Assertions.assertThat(codeToString(code)).contains("name = \"name\"")
            Assertions.assertThat(codeToString(code)).contains("type = AllowsNull(IntType())")
            Assertions.assertThat(codeToString(code)).contains("markdownDescription = \"description\"")
            Assertions.assertThat(codeToString(code)).contains("required = true")
            Assertions.assertThat(codeToString(code)).contains("default = 0")
        }

        @Test
        fun `processField converts union fields`() {
            val fSchema = Schema.createUnion(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.LONG))
            val f = Schema.Field("name", fSchema, "description", 0)
            val code = BaleenGenerator.processField(f)
            Assertions.assertThat(codeToString(code)).contains("p.attr(")
            Assertions.assertThat(codeToString(code)).contains("name = \"name\"")
            Assertions.assertThat(codeToString(code)).contains("type = UnionType(IntType(), LongType())")
            Assertions.assertThat(codeToString(code)).contains("markdownDescription = \"description\"")
            Assertions.assertThat(codeToString(code)).contains("required = true")
        }
    }

    @Nested
    inner class AvroType2Baleen {

        @Test
        fun `avroTypeToBaleenType converts Boolean`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.create(Schema.Type.BOOLEAN))
            Assertions.assertThat(codeToString(code)).contains("BooleanType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Double`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.create(Schema.Type.DOUBLE))
            Assertions.assertThat(codeToString(code)).contains("DoubleType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Float`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.create(Schema.Type.FLOAT))
            Assertions.assertThat(codeToString(code)).contains("FloatType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Int`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.create(Schema.Type.INT))
            Assertions.assertThat(codeToString(code)).contains("IntType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Long`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.create(Schema.Type.LONG))
            Assertions.assertThat(codeToString(code)).contains("LongType()")
        }

        @Test
        fun `avroTypeToBaleenType converts String`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.create(Schema.Type.STRING))
            Assertions.assertThat(codeToString(code)).contains("StringType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Enum`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.createEnum("myEnum", "", "", listOf("a", "b", "c")))
            Assertions.assertThat(codeToString(code)).contains("EnumType(\"a\", \"b\", \"c\")")
        }

        @Test
        fun `avroTypeToBaleenType converts Array`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.createArray(Schema.create(Schema.Type.STRING)))
            Assertions.assertThat(codeToString(code)).contains("OccurrencesType(StringType())")
        }

        @Test
        fun `avroTypeToBaleenType converts Map`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.createMap(Schema.create(Schema.Type.INT)))
            Assertions.assertThat(codeToString(code)).contains("MapType(StringType(), IntType())")
        }

        @Test
        fun `avroTypeToBaleenType converts Record`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.createRecord("MyRecord", "", "com.shoprunner.data", false))
            Assertions.assertThat(codeToString(code)).contains("com.shoprunner.data.MyRecordType.description")
        }

        @Test
        fun `avroTypeToBaleenType converts Union`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.createUnion(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.LONG)))
            Assertions.assertThat(codeToString(code)).contains("UnionType(IntType(), LongType())")
        }

        @Test
        fun `avroTypeToBaleenType converts Nullable Type Union`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.createUnion(Schema.create(Schema.Type.NULL), Schema.create(Schema.Type.LONG)))
            Assertions.assertThat(codeToString(code)).contains("LongType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Int date`() {
            val timestampMilliSchema = LogicalType("date").addToSchema(Schema.create(Schema.Type.INT))
            val code = BaleenGenerator.avroTypeToBaleenType(timestampMilliSchema)
            Assertions.assertThat(codeToString(code)).contains("IntType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Int time-millis`() {
            val timestampMilliSchema = LogicalType("time-millis").addToSchema(Schema.create(Schema.Type.INT))
            val code = BaleenGenerator.avroTypeToBaleenType(timestampMilliSchema)
            Assertions.assertThat(codeToString(code)).contains("IntType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Long time-micros`() {
            val timestampMilliSchema = LogicalType("timestamp-millis").addToSchema(Schema.create(Schema.Type.LONG))
            val code = BaleenGenerator.avroTypeToBaleenType(timestampMilliSchema)
            Assertions.assertThat(codeToString(code)).contains("LongCoercibleToInstant(InstantType())")
        }

        @Test
        fun `avroTypeToBaleenType converts Long timestamp-millis`() {
            val timestampMilliSchema = LogicalType("time-micros").addToSchema(Schema.create(Schema.Type.LONG))
            val code = BaleenGenerator.avroTypeToBaleenType(timestampMilliSchema)
            Assertions.assertThat(codeToString(code)).contains("LongType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Long timestamp-micros`() {
            val timestampMilliSchema = LogicalType("timestamp-micros").addToSchema(Schema.create(Schema.Type.LONG))
            val code = BaleenGenerator.avroTypeToBaleenType(timestampMilliSchema)
            Assertions.assertThat(codeToString(code)).contains("LongType()")
        }

        @Test
        fun `avroTypeToBaleenType converts Nullable types`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.createUnion(Schema.create(Schema.Type.NULL), Schema.create(Schema.Type.LONG)))
            Assertions.assertThat(codeToString(code)).contains("AllowsNull(LongType())")
        }

        @Test
        fun `avroTypeToBaleenType converts Nullable unions`() {
            val code = BaleenGenerator.avroTypeToBaleenType(Schema.createUnion(Schema.create(Schema.Type.NULL), Schema.create(Schema.Type.INT), Schema.create(Schema.Type.LONG)))
            Assertions.assertThat(codeToString(code)).contains("AllowsNull(UnionType(IntType(), LongType()))")
        }
    }

    @Nested
    inner class CheckFileGeneration {
        val parser = Schema.Parser()
        val dogSchemaStr = """
        |{
        |   "type": "record",
        |   "namespace": "com.shoprunner.data.dogs",
        |   "name": "Dog",
        |   "doc": "It's a dog. Ruff Ruff!",
        |   "fields": [
        |        { "name": "name", "type": "string", "doc": "The name of the dog", "default": "Fido" },
        |        { "name": "legs", "type": ["null", "long", "int"], "default": null, "doc": "The number of legs" }
        |   ]
        |}
        """.trimMargin()
        val dogSchema = parser.parse(dogSchemaStr)

        val packSchemaStr = """
        |{
        |   "type": "record",
        |   "namespace": "com.shoprunner.data.dogs",
        |   "name": "Pack",
        |   "doc": "It's a Pack of Dogs. Grr Grr!",
        |   "fields": [
        |        { "name": "name", "type": "string", "doc": "The name of the pack", "aliases": [ "packName" ] },
        |        {
        |          "name": "dogs",
        |          "type": {"type": "array", "items": "com.shoprunner.data.dogs.Dog"},
        |          "doc": "The dogs in the pack"
        |        }
        |   ]
        |}
        """.trimMargin()
        val packSchema = parser.parse(packSchemaStr)

        inner class LogMessageCollector : MessageCollector {
            val logger = Logger.getLogger("LogMessageCollector")

            override fun clear() = Unit

            override fun hasErrors() = false

            override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
                when (severity) {
                    CompilerMessageSeverity.ERROR -> logger.severe("$message : $location")
                    CompilerMessageSeverity.EXCEPTION -> logger.severe("$message : $location")
                    CompilerMessageSeverity.STRONG_WARNING -> logger.warning("$message : $location")
                    CompilerMessageSeverity.WARNING -> logger.warning("$message : $location")
                    else -> logger.info("$severity: $message : $location")
                }
            }
        }

        @Test
        fun `generate code from Avro Schema that compiles`() {
            // Setup
            val dir = File("build/avro-gen-test")
            val sourceDir = File(dir, "src/main/kotlin")
            sourceDir.mkdirs()
            val classesDir = File(dir, "classes/main/kotlin")
            classesDir.mkdirs()

            // Generate Baleen Kotlin Files
            encode(dogSchema).writeTo(sourceDir)
            val dogFile = File(sourceDir, "com/shoprunner/data/dogs/DogType.kt")
            Assertions.assertThat(dogFile).exists()

            encode(packSchema).writeTo(sourceDir)
            val packFile = File(sourceDir, "com/shoprunner/data/dogs/PackType.kt")
            Assertions.assertThat(packFile).exists()

            // Needs the Environment Variable passed in in order to compile. Gradle can give us this.
            Assertions.assertThat(System.getenv("GEN_CLASSPATH")).isNotBlank()

            val compiler = K2JVMCompiler()
            val args = K2JVMCompilerArguments().apply {
                destination = classesDir.path
                freeArgs = listOf(sourceDir.path)
                classpath = System.getenv("GEN_CLASSPATH")
                noStdlib = true
            }
            compiler.exec(LogMessageCollector(), Services.EMPTY, args)

            // Check that compilation worked
            Assertions.assertThat(File(classesDir, "com/shoprunner/data/dogs/DogType.class")).exists()
            Assertions.assertThat(File(classesDir, "com/shoprunner/data/dogs/PackType.class")).exists()

            // Check if the compiled files can be loaded
            val cl = URLClassLoader(arrayOf(classesDir.toURI().toURL()))

            val dogType = cl.loadClass("com.shoprunner.data.dogs.DogType")
            val dogDescription = dogType.getDeclaredField("description").type
            Assertions.assertThat(dogDescription).isEqualTo(DataDescription::class.java)

            val packType = cl.loadClass("com.shoprunner.data.dogs.PackType")
            val packDescription = packType.getDeclaredField("description").type
            Assertions.assertThat(packDescription).isEqualTo(DataDescription::class.java)
        }
    }
}