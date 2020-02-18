package com.shoprunner.baleen.generator

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.UnionType

internal object StringGenerator : BaseGenerator<String, StringOptions> {
    override fun defaultTypeMapper(
        typeMapper: TypeMapper<String, StringOptions>,
        baleenType: BaleenType,
        options: StringOptions
    ): String =
        when (baleenType) {
            is AllowsNull<*> -> "AllowsNull(${typeMapper(baleenType.type, options)})"
            is CoercibleType<*, *> -> {
                val subType = baleenType.toSubType(options.coercibleHandlerOption)
                val mapped = typeMapper(subType, options)
                when (options.coercibleHandlerOption) {
                    CoercibleHandlerOption.FROM -> "CoercibleFrom($mapped)"
                    CoercibleHandlerOption.TO -> "CoercibleTo($mapped)"
                }
            }
            is OccurrencesType -> "Occurrences(${typeMapper(baleenType.memberType, options)})"
            is MapType -> {
                val key = typeMapper(baleenType.valueType, options)
                val value = typeMapper(baleenType.valueType, options)
                "Map($key, $value)"
            }
            is UnionType -> {
                baleenType.types
                    .map { typeMapper(it, options) }
                    .joinToString(", ", "Union(", ")")
            }
            is DataDescription -> {
                val name =
                    if (baleenType.nameSpace.isNotBlank()) "${baleenType.nameSpace}.${baleenType.name()}"
                    else baleenType.name
                val attrs = baleenType.attrs
                    .map { "${it.name}=${typeMapper(it.type, options)}" }
                    .joinToString(", ")
                "$name($attrs)"
            }
            else -> baleenType.name()
        }
}
