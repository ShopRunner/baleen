package com.shoprunner.baleen.poet

import com.shoprunner.baleen.AttributeDescription
import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.NoDefault
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName

/**
 * Given a BaleenType, write it to a FileSpec.
 *
 * @receiver the BaleenType to write as code
 * @param packageName The package, "com.your.namespace". Defaults to "" for general BaleenTypes, and `namespace` for Data Descriptions.
 * @param name The filename to write to. Defaults to a cleaned version of BaleenType `name()`.
 * @param typeMapper A function that writes the BaleenType to the CodeBlock output.
 * @return The Kotlin FileSpec
 */
fun BaleenType.toFileSpec(packageName: String = "", name: String = this.name(), typeMapper: TypeMapper = ::defaultTypeMapper): FileSpec =
    if (this is DataDescription) {
        toFileSpec(
            packageName.takeIf { it.isNotBlank() } ?: this.nameSpace,
            name,
            typeMapper)
    } else {
        val cleanName = name.takeIf { it == this.name() }?.toCleanFileName() ?: name
        FileSpec.builder(packageName, cleanName)
            .addProperty(toPropertySpec(name, typeMapper))
            .build()
    }

/**
 * Given a DataDescription specifically, write it to a FileSpec.
 *
 * @receiver The DataDescription to write as code
 * @param packageName The package, "com.your.namespace". Defaults to "" for general BaleenTypes, and `namespace` for Data Descriptions.
 * @param fileName The filename to write to. Defaults to BaleenType `name()`.
 * @param typeMapper A function that writes the DataDescription to the CodeBlock output.
 * @return The Kotlin FileSpec
 */
fun DataDescription.toFileSpec(packageName: String = this.nameSpace, name: String = this.name, typeMapper: TypeMapper = ::defaultTypeMapper): FileSpec =
    FileSpec.builder(packageName, name)
        .addProperty(this.toPropertySpec(name, typeMapper))
        .build()

/**
 * Given a BaleenType specifically, write the PropertySpec to add an instance of the BaleenType to a file
 *
 * @receiver the BaleenType to write to the PropertySpec
 * @param typeMapper A function that writes the BaleenType to the CodeBlock output.
 * @return The Kotlin PropertySpec
 */
fun BaleenType.toPropertySpec(name: String = this.name(), typeMapper: TypeMapper = ::defaultTypeMapper): PropertySpec =
    if (this is DataDescription) {
        this.toPropertySpec(name, typeMapper)
    } else {
        val cleanName = name.takeIf { it == this.name() }?.toCleanVariableName() ?: name
        PropertySpec.builder(cleanName, BaleenType::class)
            .addModifiers(KModifier.PUBLIC)
            .initializer(typeMapper(CodeBlock.builder(), this).build())
            .build()
    }

/**
 * Given a DataDescription specifically, write the PropertySpec to add an instance of the DataDescription to a file.
 *
 * @receiver the DataDescription to write to the PropertySpec
 * @param typeMapper A function that writes the BaleenType to the CodeBlock output.
 * @return The Kotlin PropertySpec
 */
fun DataDescription.toPropertySpec(name: String = this.name, typeMapper: TypeMapper = ::defaultTypeMapper): PropertySpec =
    PropertySpec.builder(name, DataDescription::class)
        .addModifiers(KModifier.PUBLIC)
        .addDataDescription(this, typeMapper)
        .build()

/**
 * Given a DataDescription specifically, write the PropertySpec to add an instance of the property to a file
 *
 * @receiver the PropertySpec being written to
 * @param dataDescription the DataDescription we are writing.
 * @param typeMapper A function that writes the BaleenType to the CodeBlock output.
 * @return The Kotlin PropertySpec
 */
fun PropertySpec.Builder.addDataDescription(dataDescription: DataDescription, typeMapper: TypeMapper = ::defaultTypeMapper): PropertySpec.Builder =
        this.addKdoc(dataDescription.markdownDescription)
        .initializer(CodeBlock.builder()
            .beginControlFlow("%M(%S, %S, %S)",
                MemberName(Baleen::class.asClassName(), "describe"),
                dataDescription.name,
                dataDescription.nameSpace,
                dataDescription.markdownDescription)
            .addAttributeDescriptions(dataDescription.attrs, typeMapper)
            .add(CodeBlock.of("\n"))
            .endControlFlow()
            .build())

/**
 * Add all AttributeDescriptions to a CodeBlock
 *
 * @receiver The CodeBlock to add description to
 * @param attrs The AttributeDescriptions to be written as code
 * @param typeMapper A function that writes the BaleenType to the CodeBlock output
 * @return The CodeBlock receiver
 */
fun CodeBlock.Builder.addAttributeDescriptions(attrs: List<AttributeDescription>, typeMapper: TypeMapper = ::defaultTypeMapper): CodeBlock.Builder = apply {
    attrs.forEach { this.addAttributeDescription(it, typeMapper) }
}

/**
 * Add an AttributeDescription to the CodeBlock.
 *
 * @receiver The CodeBlock to add description to
 * @param attr The AttributeDescription to be written as code
 * @param typeMapper A function that writes the BaleenType to the CodeBlock output
 * @return The CodeBlock receiver
 */
fun CodeBlock.Builder.addAttributeDescription(attr: AttributeDescription, typeMapper: TypeMapper = ::defaultTypeMapper): CodeBlock.Builder = apply {
    // create attribute
    add("it.attr(\n")
    indent()
    // name
    add("name = %S,\n", attr.name)
    // type
    add("type = ")
    typeMapper(this, attr.type)
    // markdownDescription
    if (attr.markdownDescription.isNotBlank()) {
        add(",\nmarkdownDescription = %S", attr.markdownDescription)
    }
    // required
    if (attr.required) {
        add(",\nrequired = %L", attr.required)
    }
    // default
    if (attr.default != NoDefault) {
        add(",\ndefault = ")
        addDefaultValue(attr.type, attr.default)
    }
    unindent()
    add("\n)\n")
}

/**
 * Makes the filename CamelCase and without special characters
 */
fun String.toCleanFileName(): String =
    this.split("""\W+""".toRegex())
        .filter { it.isNotBlank() }
        .map { it.capitalize() }
        .joinToString("")

/**
 * Makes the variable name standard camelCase without special characters
 */
fun String.toCleanVariableName(): String = this.toCleanFileName().decapitalize()
