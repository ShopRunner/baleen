package com.shoprunner.baleen.avro

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.LongCoercibleToInstant
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.UnionType
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.FileSpec
import org.apache.avro.Schema
import java.io.File
import java.nio.file.Path

/**
 * Given a parsed Avro Schema, generate basic Baleen descriptions.
 */
object BaleenEncoder {

    fun processSchema(schema: Schema): TypeSpec {
        val attrsCodeBlock = schema.fields.map(::processField)
                .fold(CodeBlock.builder(), { b, c -> b.add(c).add("\n") })
                .build()

        val modelCodeBlock = CodeBlock.builder()
                .beginControlFlow("%T.%L(%S, %S, %S)",
                        Baleen::class,
                        Baleen::describe.name,
                        schema.name,
                        schema.namespace,
                        schema.doc)
                .add("p ->\n")
                .indent()
                .add(attrsCodeBlock)
                .add("\n")
                .unindent()
                .endControlFlow()
                .build()
        return TypeSpec.objectBuilder(ClassName(schema.namespace, "${schema.name}Type"))
                .addKdoc(schema.doc)
                .addProperty(PropertySpec.builder("description", DataDescription::class)
                        .addModifiers(KModifier.PUBLIC)
                        .addAnnotation(JvmStatic::class)
                        .initializer(modelCodeBlock)
                        .build())
                .build()
    }

    fun processField(field: Schema.Field): CodeBlock {
        /*
        fun attr(
            name: String,
            type: BaleenType,
            markdownDescription: String = "",
            aliases: Array<String> = arrayOf(),
            required: Boolean = false
        )
         */
        val isRequired = field.schema().type != Schema.Type.UNION ||
                field.schema().types.none { it.type == Schema.Type.NULL }

        return CodeBlock.builder().apply {
            // create attribute
            add("p.%L(\n", DataDescription::attr.name)
            indent()
            // name
            add("%L = %S,\n", DataDescription::attr.parameters[1].name, field.name())
            // type
            add("%L = ", DataDescription::attr.parameters[2].name)
            add(avroTypeToBaleenType(field.schema()))
            add(",\n")
            // markdownDescription
            add("%L = %S,\n", DataDescription::attr.parameters[3].name, field.doc())
            // aliases
            if (field.aliases().isNotEmpty()) {
                add("%L = %L,\n", DataDescription::attr.parameters[4].name,
                        field.aliases().joinToString(", ", prefix = "arrayOf(\"", postfix = "\")"))
            }
            // required
            add("%L = %L\n", DataDescription::attr.parameters[5].name, isRequired)
            unindent()
            add(")")
        }.build()
    }

    fun avroTypeToBaleenType(schema: Schema): CodeBlock {
        return when (schema.type) {
            Schema.Type.ARRAY -> {
                CodeBlock.builder()
                        .add("%T(", OccurrencesType::class)
                        .add(avroTypeToBaleenType(schema.elementType))
                        .add(")")
                        .build()
            }
            Schema.Type.MAP -> {
                CodeBlock.builder()
                        .add("%T(%T(), ", MapType::class, StringType::class)
                        .add(avroTypeToBaleenType(schema.valueType))
                        .add(")")
                        .build()
            }
            Schema.Type.BOOLEAN -> CodeBlock.of("%T()", BooleanType::class)
            Schema.Type.STRING -> CodeBlock.of("%T()", StringType::class)
            Schema.Type.DOUBLE -> CodeBlock.of("%T()", DoubleType::class)
            Schema.Type.FLOAT -> CodeBlock.of("%T()", FloatType::class)
            Schema.Type.ENUM -> {
                val enumCode = CodeBlock.builder().add("%T(", EnumType::class)
                schema.enumSymbols.forEachIndexed { i, e ->
                    if (i == 0) {
                        enumCode.add(CodeBlock.of("%S", e))
                    } else {
                        enumCode.add(", ").add(CodeBlock.of("%S", e))
                    }
                }
                enumCode.add(")").build()
            }
            Schema.Type.INT -> {
                /* TODO: Handle more logical types
                if(schema.logicalType != null) {
                    when(schema.logicalType.name){
                        "date" -> CodeBlock.of("%T()", ?::class)
                        "time-millis" -> CodeBlock.of("%T()", ?::class)
                        else -> CodeBlock.of("%T()", IntType::class)
                    }
                }
                else */
                CodeBlock.of("%T()", IntType::class)
            }
            Schema.Type.LONG -> {
                if (schema.logicalType != null) {
                    when (schema.logicalType.name) {
                        "timestamp-millis" -> CodeBlock.of("%T()", LongCoercibleToInstant::class)
                    // TODO: Handle more logical types
                    //"timestamp-micros" -> CodeBlock.of("%T()", ?::class)
                    //"time-micros" -> CodeBlock.of("%T()", ?::class)
                        else -> CodeBlock.of("%T()", LongType::class)
                    }
                } else CodeBlock.of("%T()", LongType::class)
            }
            Schema.Type.RECORD -> CodeBlock.of("${schema.namespace}.${schema.name}Type.description")
            Schema.Type.UNION -> {
                val unionedTypes = schema.types.filterNot { it.type == Schema.Type.NULL }.map { avroTypeToBaleenType(it) }
                if (unionedTypes.size > 1) {
                    val builder = CodeBlock.builder().add("%T(", UnionType::class)
                    unionedTypes.forEachIndexed { i, t ->
                        if (i == 0) {
                            builder.add(t)
                        } else {
                            builder.add(", ").add(t)
                        }
                    }
                    builder.add(")").build()
                } else {
                    unionedTypes.first()
                }
            }
            else -> throw IllegalArgumentException("avro type ${schema.type} not supported")
        }
    }

    fun encode(schema: Schema): FileSpec {
        return FileSpec.builder(schema.namespace, "${schema.name}Type")
                .addType(processSchema(schema))
                .build()
    }

    infix fun Schema.encodeTo(directory: File): File {
        encode(this).writeTo(directory)
        return directory
    }

    infix fun Schema.encodeTo(directory: Path): Path {
        encode(this).writeTo(directory)
        return directory
    }

    infix fun Schema.encodeTo(out: Appendable): Appendable {
        encode(this).writeTo(out)
        return out
    }
}