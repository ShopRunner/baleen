package com.shoprunner.baleen.generator

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringCoercibleToOccurrencesType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.UnionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BaseGeneratorTest {

    fun assertEquals(expected: BaleenType, actual: BaleenType) {
        assertEquals(expected.name(), actual.name())
    }

    val dogType = "Dog".describeAs {
        attr("name", StringType())
        attr("numLegs", AllowsNull(IntType()))
        attr("relatives", AllowsNull(MapType(StringType(), StringType())))
    }

    val packType = "Pack".describeAs {
        attr("leader", dogType)
        attr("dogs", OccurrencesType(dogType))
        attr(
            "ensignas",
            UnionType(
                StringCoercibleToOccurrencesType(OccurrencesType(StringType())),
                OccurrencesType(StringType())
            )
        )
    }

    @Test
    fun `test CoercibleType#toSubType handles FROM option`() {
        with(StringGenerator) {
            val coercedFrom = StringCoercibleToLong(LongType()).toSubType(CoercibleHandlerOption.FROM)
            assertEquals(StringType(), coercedFrom)
        }
    }

    @Test
    fun `test CoercibleType#toSubType handles TO option`() {
        with(StringGenerator) {
            val coercedTo = StringCoercibleToLong(LongType()).toSubType(CoercibleHandlerOption.TO)
            assertEquals(LongType(), coercedTo)
        }
    }

    @Test
    fun `test Iterable(AttributeDescription)#getDataDescriptions`() {
        with(StringGenerator) {
            val descriptions = packType.attrs.getDataDescriptions(setOf(packType), StringOptions())
            assertEquals(setOf(packType, dogType), descriptions)
        }
    }

    @Test
    fun `test #defaultTypeMapper maps all defaults`() {
        val expected = "Pack(" +
            "leader=Dog(name=string, numLegs=AllowsNull(int), relatives=AllowsNull(Map(string, string))), " +
            "dogs=Occurrences(Dog(name=string, numLegs=AllowsNull(int), relatives=AllowsNull(Map(string, string)))), " +
            "ensignas=Union(CoercibleFrom(string), Occurrences(string)))"
        val output = StringGenerator.defaultTypeMapper(packType, StringOptions())
        assertEquals(expected, output)
    }

    @Test
    fun `test #recursiveTypeMapper maps overrides if passed in `() {
        fun customMapper(baleenType: BaleenType, options: StringOptions): String =
            when (baleenType) {
                is StringCoercibleToOccurrencesType -> "CustomOverride"
                else -> StringGenerator.recursiveTypeMapper(::customMapper, baleenType, options)
            }

        val expected = "Pack(" +
            "leader=Dog(name=string, numLegs=AllowsNull(int), relatives=AllowsNull(Map(string, string))), " +
            "dogs=Occurrences(Dog(name=string, numLegs=AllowsNull(int), relatives=AllowsNull(Map(string, string)))), " +
            "ensignas=Union(CustomOverride, Occurrences(string)))"
        val output = customMapper(packType, StringOptions())
        assertEquals(expected, output)
    }
}
