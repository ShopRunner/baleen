package com.shoprunner.baleen.poet

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.UnionType
import com.squareup.kotlinpoet.FileSpec
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

private fun findSuper(toFind: KType, clazz: KClass<*>): KType? {
    val parent = clazz.supertypes.firstOrNull { it.isSubtypeOf(toFind) }
    val projectedType = parent?.classifier?.starProjectedType
    return when {
        projectedType == null -> null
        projectedType == toFind -> parent
        else -> findSuper(toFind, parent.classifier as KClass<*>)
    }
}

private fun CoercibleType<*, *>.createCoercibleFromType(): BaleenType {
    val superCoercibleType = findSuper(CoercibleType::class.starProjectedType, this::class)
    val fromClass = superCoercibleType?.arguments?.firstOrNull()?.type?.classifier

    return if (fromClass == null) {
        throw IllegalArgumentException(
            "Unable to handle CoercibleType FROM type for Type '${this::class}': null"
        )
    } else {
        try {
            (fromClass as KClass<*>).createInstance() as BaleenType
        } catch (e: Exception) {
            throw IllegalArgumentException(
                "Unable to handle CoercibleType FROM type for Type '${this::class}': $fromClass",
                e
            )
        }
    }
}

private fun BaleenType.recursiveGetDataDescriptions(descriptions: Set<DataDescription>): Set<DataDescription> =
    descriptions + when (this) {
        is DataDescription ->
            this.attrs.flatMap { it.type.recursiveGetDataDescriptions(descriptions + this) }

        is AllowsNull<*> ->
            this.type.recursiveGetDataDescriptions(descriptions)

        is CoercibleType<*, *> -> {
            val fromMemberType = this.createCoercibleFromType()
            val toMemberType = this.type
            (fromMemberType.recursiveGetDataDescriptions(descriptions) + toMemberType.recursiveGetDataDescriptions(descriptions))
        }

        is OccurrencesType ->
            this.memberType.recursiveGetDataDescriptions(descriptions)

        is MapType -> {
            val keyDescriptions = this.keyType.recursiveGetDataDescriptions(descriptions)
            val valueDescriptions = this.valueType.recursiveGetDataDescriptions(descriptions)

            keyDescriptions + valueDescriptions
        }

        is UnionType ->
            this.types.flatMap {
                it.recursiveGetDataDescriptions(descriptions)
            }.toSet()

        else -> emptySet()
    }

fun BaleenType.generateAllFileSpecs(
    descriptionsAlreadyGenerated: Set<DataDescription> = emptySet(),
    typeMapper: TypeMapper = ::defaultTypeMapper
): List<FileSpec> =
    recursiveGetDataDescriptions(descriptionsAlreadyGenerated)
        .map {
            it.toFileSpec(typeMapper = typeMapper)
        }
