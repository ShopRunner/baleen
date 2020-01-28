package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.UnionType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import java.io.File
import java.net.URL

/**
 * Given a JsonSchema, generate basic Baleen descriptions.
 */
object BaleenGenerator {

    private val mapper = jacksonObjectMapper()

    fun getNamespaceAndName(schema: RootJsonSchema): Pair<String, String> {
        // Try to use "self" if it exists
        return if (schema.self != null) {
            schema.self!!.vendor to schema.self!!.name
        }
        // Otherwise parse from "id" if it exists
        else if (schema.id != null) {
            val namespaceParts = schema.id!!.split(".")
            if (namespaceParts.size > 1) {
                namespaceParts.subList(0, namespaceParts.lastIndex).joinToString(".") to namespaceParts.last()
            } else {
                "" to namespaceParts.first()
            }
        }
        // Finally get it from "$ref"
        else if (schema.`$ref` != null) {
            val lastRefPart = schema.`$ref`!!.split("/").last()
            val recordPart = lastRefPart.split(":").last()
            val namespaceParts = recordPart.split(".")
            if (namespaceParts.size > 1) {
                namespaceParts.subList(0, namespaceParts.lastIndex).joinToString(".") to namespaceParts.last()
            } else {
                "" to namespaceParts.first()
            }
        } else {
            "" to "NoName"
        }
    }

    fun getNamespaceAndName(record: String): Pair<String, String> {
        val lastRefPart = record.split("/").last()
        val recordPart = lastRefPart.split(":").last()
        val namespaceParts = recordPart.split(".")
        return if (namespaceParts.size > 1) {
            namespaceParts.subList(0, namespaceParts.lastIndex).joinToString(".") to namespaceParts.last()
        } else {
            "" to namespaceParts.first()
        }
    }

    fun processSchema(namespace: String, name: String, schema: ObjectSchema): PropertySpec {
        val attrsCodeBlock = schema.properties.map { (k, v) ->
            processField(k, v, schema.required?.contains(k) ?: false)
        }
                .fold(CodeBlock.builder(), { b, c -> b.add(c).add("\n") })
                .build()

        val modelCodeBlock = CodeBlock.builder()
                .beginControlFlow("%L(%S, %S, %S)",
                        Baleen::describe.name,
                        name,
                        namespace,
                        schema.description ?: "")
                .add(attrsCodeBlock)
                .add("\n")
                .endControlFlow()
                .build()

        return PropertySpec.builder(name, DataDescription::class)
                .addKdoc(schema.description ?: "")
                .addModifiers(KModifier.PUBLIC)
                .initializer(modelCodeBlock)
                .build()
    }

    fun processField(fieldName: String, schema: JsonSchema, isRequired: Boolean): CodeBlock {
        /*
        fun attr(
            name: String,
            type: BaleenType,
            markdownDescription: String = "",
            aliases: Array<String> = arrayOf(),
            required: Boolean = false
        )
         */
        return CodeBlock.builder().apply {
            // create attribute
            add("it.%L(\n", DataDescription::attr.name)
            indent()
            // name
            add("%L = %S,\n", DataDescription::attr.parameters[1].name, fieldName)
            // type
            add("%L = ", DataDescription::attr.parameters[2].name)
            add(jsonSchemaToBaleenType(schema))
            // markdownDescription
            if (schema.description != null) {
                add(",\n%L = %S", DataDescription::attr.parameters[3].name, schema.description)
            }
            // required
            if (isRequired) {
                add(",\n%L = %L", DataDescription::attr.parameters[5].name, isRequired)
            }
            // default
            if (schema.default != null) {
                val default = schema.default
                when (default) {
                    Null -> add(",\n%L = null", DataDescription::attr.parameters[6].name)
                    is Int -> add(",\n%L = %LL", DataDescription::attr.parameters[6].name, default.toLong())
                    is String -> add(",\n%L = %S", DataDescription::attr.parameters[6].name, default)
                    else -> add(",\n%L = %L", DataDescription::attr.parameters[6].name, default)
                }
            }
            unindent()
            add(")")
        }.build()
    }

    fun jsonSchemaToBaleenType(schema: JsonSchema): CodeBlock {
        return when (schema) {
            is AnyOf -> toUnionType(schema.anyOf)
            is ArraySchema ->
                CodeBlock.builder()
                        .add("%T(", OccurrencesType::class)
                        .add(jsonSchemaToBaleenType(schema.items))
                        .add(")")
                        .build()
            is BooleanSchema -> CodeBlock.of("%T()", BooleanType::class)
            is IntegerSchema -> {
                CodeBlock.builder().apply {
                    add("%T(", IntegerType::class)
                    if (schema.minimum != null) {
                        add("min = %L.toBigInteger()", schema.minimum)
                    }
                    if (schema.maximum != null) {
                        if (schema.minimum != null) {
                            add(", ")
                        }
                        add("max = %L.toBigInteger()", schema.maximum)
                    }
                    add(")")
                }.build()
            }
            is MapSchema -> {
                CodeBlock.builder()
                        .add("%T(%T(), ", MapType::class, StringType::class)
                        .add(jsonSchemaToBaleenType(schema.additionalProperties))
                        .add(")")
                        .build()
            }
            is NumberSchema -> {
                CodeBlock.builder().apply {
                    add("%T(", NumericType::class)
                    if (schema.minimum != null) {
                        add("\n")
                        indent()
                        add("min = %L.toBigDecimal()", schema.minimum)
                        if (schema.maximum != null) {
                            add(",")
                        }
                        unindent()
                    }
                    if (schema.maximum != null) {
                        add("\n")
                        indent()
                        add("max = %L.toBigDecimal()", schema.maximum)
                        unindent()
                    }
                    add(")")
                }.build()
            }
            is ObjectReference -> CodeBlock.of("%L", schema.`$ref`.split(":").last())
            is OneOf -> toUnionType(schema.oneOf)
            is StringSchema -> {
                if (schema.enum != null) {
                    if (schema.enum!!.size > 1) {
                        val enumName = "Enum${schema.enum!!.map { it.capitalize().first() }.joinToString("")}"
                        CodeBlock.builder()
                                .add(CodeBlock.of("%T(%S, listOf(", EnumType::class, enumName))
                                .add(schema.enum!!.map { CodeBlock.of("%S", it) }.reduceRight { a, b -> a.toBuilder().add(", ").add(b).build() })
                                .add(CodeBlock.of("))"))
                                .build()
                    } else if (schema.enum!!.size == 1) {
                        CodeBlock.of("%T(%S)", StringConstantType::class, schema.enum!!.first())
                    } else {
                        throw Exception("Enum should have at least 1 value")
                    }
                } else if (schema.format != null) {
                    when (schema.format) {
                        StringFormats.`date-time` -> CodeBlock.of("%T()", InstantType::class)
                        else -> CodeBlock.of("%T()", StringType::class)
                    }
                } else if ((schema.minLength == null || schema.minLength == 0) && (schema.maxLength == null || schema.maxLength == Int.MAX_VALUE)) {
                    CodeBlock.of("%T()", StringType::class)
                } else {
                    CodeBlock.of("%T(${schema.minLength ?: 0}, ${schema.maxLength
                            ?: Int.MAX_VALUE})", StringType::class)
                }
            }
            else -> throw IllegalArgumentException("json type ${schema::class.simpleName} not supported")
        }
    }

    private fun toUnionType(schemas: List<JsonSchema>): CodeBlock {
        val unionedTypes = schemas.filterNot { it is NullSchema }.map { jsonSchemaToBaleenType(it) }
        val baleenTypeCode = if (unionedTypes.size > 1) {
            CodeBlock.builder()
                    .add("%T(", UnionType::class)
                    .add(schemas.map { jsonSchemaToBaleenType(it) }.reduceRight { a, b -> a.toBuilder().add(", ").add(b).build() })
                    .add(")")
                    .build()
        } else {
            unionedTypes.first()
        }

        val isNullable = schemas.any { it is NullSchema }
        return if (isNullable) {
            CodeBlock.builder()
                    .add("%T(", AllowsNull::class)
                    .add(baleenTypeCode)
                    .add(")")
                    .build()
        } else {
            baleenTypeCode
        }
    }

    fun encode(namespace: String, name: String, schema: ObjectSchema): FileSpec {
        return FileSpec.builder(namespace, name)
                .addImport(Baleen::class, Baleen::describe.name)
                .addProperty(processSchema(namespace, name, schema))
                .build()
    }

    fun encode(schema: RootJsonSchema): List<FileSpec> {
        return if (schema.definitions != null) {
            schema.definitions!!.map { (record, objectSchema) ->
                val (namespace, name) = getNamespaceAndName(record)
                encode(namespace, name, objectSchema)
            }
        } else if (schema.type == JsonType.`object`) {
            val (namespace, name) = getNamespaceAndName(schema)
            val objectSchema = ObjectSchema(
                    schema.required,
                    schema.additionalProperties,
                    schema.properties ?: emptyMap())
            listOf(encode(namespace, name, objectSchema))
        } else {
            emptyList()
        }
    }

    fun String.parseJsonSchema(): RootJsonSchema {
        return mapper.readValue(this, RootJsonSchema::class.java)
    }

    fun File.parseJsonSchema(): RootJsonSchema {
        return mapper.readValue(this, RootJsonSchema::class.java)
    }

    fun URL.parseJsonSchema(): RootJsonSchema {
        return mapper.readValue(this, RootJsonSchema::class.java)
    }
}
