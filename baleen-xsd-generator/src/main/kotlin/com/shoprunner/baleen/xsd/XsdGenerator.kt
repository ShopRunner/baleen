package com.shoprunner.baleen.xsd

import com.shoprunner.baleen.AttributeDescription
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.NoDefault
import com.shoprunner.baleen.generator.BaseGenerator
import com.shoprunner.baleen.generator.TypeMapper
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.shoprunner.baleen.xsd.xml.Annotation
import com.shoprunner.baleen.xsd.xml.ComplexType
import com.shoprunner.baleen.xsd.xml.Element
import com.shoprunner.baleen.xsd.xml.Enumeration
import com.shoprunner.baleen.xsd.xml.MaxInclusive
import com.shoprunner.baleen.xsd.xml.MaxLength
import com.shoprunner.baleen.xsd.xml.MinInclusive
import com.shoprunner.baleen.xsd.xml.MinLength
import com.shoprunner.baleen.xsd.xml.Restriction
import com.shoprunner.baleen.xsd.xml.Schema
import com.shoprunner.baleen.xsd.xml.Sequence
import com.shoprunner.baleen.xsd.xml.SimpleType
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import java.io.PrintStream

object XsdGenerator : BaseGenerator<TypeDetails, XsdOptions> {

    fun defaultTypeMapper(baleenType: BaleenType): TypeDetails =
        super.defaultTypeMapper(baleenType, XsdOptions)

    fun recursiveTypeMapper(typeMapper: XsdTypeMapper, baleenType: BaleenType): TypeDetails =
        recursiveTypeMapper({ b, _ -> typeMapper(b) }, baleenType, XsdOptions)

    /**
     * Maps baleen type to type details that are used for XSD.
     */
    override fun defaultTypeMapper(typeMapper: TypeMapper<TypeDetails, XsdOptions>, baleenType: BaleenType, options: XsdOptions): TypeDetails =
        when (baleenType) {
            is AllowsNull<*> -> typeMapper(baleenType.type, options)
            is BooleanType -> TypeDetails("xs:boolean")
            is CoercibleType<*, *> -> typeMapper(baleenType.toSubType(options.coercibleHandlerOption), options)
            is DataDescription -> TypeDetails(baleenType.name)
            is DoubleType -> TypeDetails(
                simpleType = SimpleType(
                    Restriction(
                        base = "xs:double",
                        maxInclusive = if (baleenType.max.isFinite()) MaxInclusive(baleenType.max.toBigDecimal()) else null,
                        minInclusive = if (baleenType.min.isFinite()) MinInclusive(baleenType.min.toBigDecimal()) else null
                    )
                )
            )
            is EnumType -> TypeDetails(
                simpleType = SimpleType(
                    Restriction(
                        base = "xs:string",
                        enumeration = baleenType.enum.map { Enumeration(it) }
                    )
                )
            )
            is FloatType -> TypeDetails(
                simpleType = SimpleType(
                    Restriction(
                        base = "xs:float",
                        maxInclusive = if (baleenType.max.isFinite()) MaxInclusive(baleenType.max.toBigDecimal()) else null,
                        minInclusive = if (baleenType.min.isFinite()) MinInclusive(baleenType.min.toBigDecimal()) else null
                    )
                )
            )
            is InstantType -> TypeDetails("xs:dateTime")
            is IntType -> TypeDetails(
                simpleType = SimpleType(
                    Restriction(
                        base = "xs:int",
                        maxInclusive = MaxInclusive(baleenType.max.toBigDecimal()),
                        minInclusive = MinInclusive(baleenType.min.toBigDecimal())
                    )
                )
            )
            is IntegerType -> TypeDetails(
                simpleType = SimpleType(
                    Restriction(
                        base = "xs:int",
                        maxInclusive = baleenType.max?.let { MaxInclusive(it.toBigDecimal()) },
                        minInclusive = baleenType.min?.let { MinInclusive(it.toBigDecimal()) }
                    )
                )
            )
            is LongType -> TypeDetails(
                simpleType = SimpleType(
                    Restriction(
                        base = "xs:long",
                        maxInclusive = MaxInclusive(baleenType.max.toBigDecimal()),
                        minInclusive = MinInclusive(baleenType.min.toBigDecimal())
                    )
                )
            )
            is NumericType -> TypeDetails(
                simpleType = SimpleType(
                    Restriction(
                        base = "xs:double",
                        maxInclusive = baleenType.max?.let { MaxInclusive(it) },
                        minInclusive = baleenType.min?.let { MinInclusive(it) }
                    )
                )
            )
            is OccurrencesType -> typeMapper(baleenType.memberType, options).copy(maxOccurs = "unbounded")
            is StringType -> TypeDetails(
                simpleType = SimpleType(
                    Restriction(
                        base = "xs:string",
                        maxLength = MaxLength(baleenType.max),
                        minLength = MinLength(baleenType.min)
                    )
                )
            )
            is TimestampMillisType -> TypeDetails("xs:dateTime")
            else -> throw Exception("No mapping is defined for ${baleenType.name()} to XSD")
        }

    private fun generateType(type: DataDescription, typeMapper: XsdTypeMapper) =
        ComplexType(
            name = type.name,
            annotation = createDocumentationAnnotation(type.markdownDescription),
            sequence = Sequence(type.attrs.map { generateElement(it, typeMapper) })
        )

    private fun generateElement(attr: AttributeDescription, typeMapper: XsdTypeMapper): Element {
        val typeDetails = typeMapper(attr.type)
        return Element(
            name = attr.name,
            type = typeDetails.type,
            minOccurs = if (attr.required) null else 0,
            maxOccurs = typeDetails.maxOccurs,
            annotation = createDocumentationAnnotation(attr.markdownDescription),
            simpleType = typeDetails.simpleType,
            default = if (attr.default != NoDefault) attr.default.toString() else null
        )
    }

    private fun createDocumentationAnnotation(doc: String) =
        doc.let { if (it.isNotBlank()) Annotation(documentation = it) else null }

    /**
     * Creates an XSD from a data description.
     */
    fun DataDescription.encode(outputStream: PrintStream, typeMapper: XsdTypeMapper = ::defaultTypeMapper) {
        val jaxbContext = JAXBContext.newInstance(Schema::class.java)
        val jaxbMarshaller = jaxbContext.createMarshaller()

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

        val types = this.attrs.getDataDescriptions(mutableSetOf(this), XsdOptions)

        val complexTypes = types.map { generateType(it, typeMapper) }

        val schema = Schema(
            elements = listOf(
                Element(
                    name = this.name,
                    type = this.name,
                    annotation = createDocumentationAnnotation(this.markdownDescription)
                )
            ),
            complexTypes = complexTypes
        )

        jaxbMarshaller.marshal(schema, outputStream)
    }
}
