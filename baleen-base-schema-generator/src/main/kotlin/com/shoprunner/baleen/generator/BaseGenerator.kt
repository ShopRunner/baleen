package com.shoprunner.baleen.generator

import com.shoprunner.baleen.AttributeDescription
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.UnionType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

abstract class BaseGenerator<TO, OPTIONS : Options> {
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

    private fun BaleenType.recursiveGetDataDescriptions(descriptions: Set<DataDescription>, options: OPTIONS): Set<DataDescription> =
        descriptions + when (this) {
            is DataDescription ->
                this.attrs.getDataDescriptions(descriptions, options) + this

            is AllowsNull<*> ->
                this.type.recursiveGetDataDescriptions(descriptions, options)

            is CoercibleType<*, *> -> {
                val memberType = this.toSubType(options.coercibleHandlerOption)
                memberType.recursiveGetDataDescriptions(descriptions, options)
            }

            is OccurrencesType ->
                this.memberType.recursiveGetDataDescriptions(descriptions, options)

            is MapType -> {
                val keyDescriptions = this.keyType.recursiveGetDataDescriptions(descriptions, options)
                val valueDescriptions = this.valueType.recursiveGetDataDescriptions(descriptions, options)

                keyDescriptions + valueDescriptions
            }

            is UnionType ->
                this.types.flatMap {
                    it.recursiveGetDataDescriptions(descriptions, options)
                }.toSet()

            else -> emptySet()
        }

    fun Iterable<AttributeDescription>.getDataDescriptions(descriptions: Set<DataDescription>, options: OPTIONS): Set<DataDescription> =
        flatMap { it.type.recursiveGetDataDescriptions(descriptions, options) }.toSet()

    fun CoercibleType<*, *>.toSubType(coercibleHandlerOption: CoercibleHandlerOption): BaleenType =
        when (coercibleHandlerOption) {
            CoercibleHandlerOption.FROM -> createCoercibleFromType()
            CoercibleHandlerOption.TO -> this.type
        }

    fun recursiveTypeMapper(typeMapper: TypeMapper<TO, OPTIONS>, baleenType: BaleenType, options: OPTIONS): TO =
        defaultTypeMapper(typeMapper, baleenType, options)

    fun defaultTypeMapper(baleenType: BaleenType, options: OPTIONS): TO =
        recursiveTypeMapper(::defaultTypeMapper, baleenType, options)

    abstract fun defaultTypeMapper(typeMapper: TypeMapper<TO, OPTIONS>, baleenType: BaleenType, options: OPTIONS): TO
}
