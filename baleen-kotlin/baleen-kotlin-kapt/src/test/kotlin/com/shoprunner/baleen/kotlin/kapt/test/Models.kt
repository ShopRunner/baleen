package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.annotation.Alias
import com.shoprunner.baleen.annotation.DataDescription
import com.shoprunner.baleen.annotation.Name
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant

/** This is a string model */
@DataDescription
data class StringModel(
    /** A string field **/
    var string: String,

    /** A nullable string field **/
    var nullableString: String?
)

@DataDescription
data class IntModel(
    /** An int number field */
    var intNumber: Int,

    /** A nullable int number field */
    var nullableIntNumber: Int?
)

@DataDescription
data class LongModel(
    /** A long number field */
    var longNumber: Long,

    /** A nullable long number field */
    var nullableLongNumber: Long?
)

@DataDescription
data class FloatModel(
    /** A float number field */
    var floatNumber: Float,

    /** A nullable float number field */
    var nullableFloatNumber: Float?
)

@DataDescription
data class DoubleModel(
    /** A double number field */
    var doubleNumber: Double,

    /** A nullable double number field */
    var nullableDoubleNumber: Double?
)

@DataDescription
data class ByteModel(
    /** A byte number field */
    var byteNumber: Byte,

    /** A nullable byte number field */
    var nullableByteNumber: Byte?
)

@DataDescription
data class ShortModel(
    /** A short number field */
    var shortNumber: Short,

    /** A nullable short number field */
    var nullableShortNumber: Short?
)

@DataDescription
data class BigIntegerModel(
    /** A BigInteger number field */
    var bigIntegerNumber: BigInteger,

    /** A nullable BigInteger number field */
    var nullableBigIntegerNumber: BigInteger?
)

@DataDescription
data class BigDecimalModel(
    /** A BigDecimal number field */
    var bigDecimalNumber: BigDecimal,

    /** A nullable BigDecimal number field */
    var nullableBigDecimalNumber: BigDecimal?
)

@DataDescription
data class BooleanModel(
    /** A double number field */
    var bool: Boolean,

    /** A nullable double number field */
    var nullableBool: Boolean?
)

@DataDescription
data class InstantModel(
    /** An instant field */
    var instant: Instant,

    /** A nullable instant field */
    var nullableInstant: Instant?
)

@DataDescription
data class NestedModel(
    /** A nested string model field */
    var nestedStringModel: StringModel,

    /** A nested int model field */
    var nestedIntModel: IntModel,

    /** A nullable nested string model field */
    var nullableNestedStringModel: StringModel?,

    /** A nullable nested int model field */
    var nullableNestedIntModel: IntModel?
)

@DataDescription
data class ListStringModel(
    /** A string list field **/
    var stringList: List<String>,

    /** A nullable string list field **/
    var nullableStringList: List<String>?
)

@DataDescription
data class ListIntModel(
    /** A int list field **/
    var intList: List<Int>,

    /** A nullable int list field **/
    var nullableIntList: List<Int>?
)

@DataDescription
data class ListNestedModel(
    /** A int list field **/
    var nested: List<NestedModel>,

    /** A nullable int list field **/
    var nullableNested: List<NestedModel>?
)

@DataDescription
data class ArrayStringModel(
    /** A string list field **/
    var stringArray: Array<String>,

    /** A nullable string list field **/
    var nullableStringArray: Array<String>?
)

@DataDescription
data class ArrayIntModel(
    /** A int array field **/
    var intArray: Array<Int>,

    /** A nullable int array field  **/
    var nullableIntArray: Array<Int>?
)

@DataDescription
data class ArrayNestedModel(
    /** A nested list field **/
    var nested: Array<NestedModel>,

    /** A nullable nested list field **/
    var nullableNested: Array<NestedModel>?
)

@DataDescription
data class SetStringModel(
    /** A string set field  **/
    var stringSet: Set<String>,

    /** A nullable string set field **/
    var nullableStringSet: Set<String>?
)

@DataDescription
data class SetIntModel(
    /** A int set field **/
    var intSet: Set<Int>,

    /** A nullable int set field**/
    var nullableIntSet: Set<Int>?
)

@DataDescription
data class SetNestedModel(
    /** A nested set field **/
    var nested: Set<NestedModel>,

    /** A nullable nested set field **/
    var nullableNested: Set<NestedModel>?
)

@DataDescription
data class IterableStringModel(
    /** A string field iterable **/
    var stringIterable: Iterable<String>,

    /** A nullable string iterable field **/
    var nullableStringIterable: Iterable<String>?
)

@DataDescription
data class IterableIntModel(
    /** A int iterable field **/
    var intIterable: Iterable<Int>,

    /** A nullable int iterable field**/
    var nullableIntIterable: Iterable<Int>?
)

@DataDescription
data class IterableNestedModel(
    /** A nested set field **/
    var nested: Iterable<NestedModel>,

    /** A nullable nested set field **/
    var nullableNested: Iterable<NestedModel>?
)

@DataDescription
data class MapStringIntModel(
    /** A int iterable field **/
    var map: Map<String, Int>,

    /** A nullable int iterable field**/
    var nullableMap: Map<String, Int>?
)

@DataDescription
data class MapNestedModel(
    /** A map field with nested value **/
    var mapNestedValues: Map<String, NestedModel>,

    /** A nullable map field with nested value **/
    var nullableMapNestedValues: Map<Int, NestedModel>?,

    /** A map field with nested keys **/
    var mapNestedKeys: Map<NestedModel, String>,

    /** A nullable map field with nested keys **/
    var nullableMapNestedKeys: Map<NestedModel, Int>?
)

/** This is a string model */
@DataDescription("ManuallyNamed", "com.shoprunner.baleen.kotlin.different")
data class StringModelWithOverriddenName(
    /** A string field **/
    var string: String,

    /** A nullable string field **/
    var nullableString: String?
)

@DataDescription
data class ModelWithDifferentFieldNames(
    @Name("field_name")
    var fieldName: String
)

@DataDescription
data class ModelWithAliases(
    @Alias("field_name")
    var fieldName: String,

    @Alias("another_name1", "another_name2")
    var anotherName: String
)
