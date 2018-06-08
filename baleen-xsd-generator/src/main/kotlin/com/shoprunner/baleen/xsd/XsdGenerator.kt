package com.shoprunner.baleen.xsd

import com.shoprunner.baleen.AttributeDescription
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToFloat
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.xsd.xml.Annotation
import com.shoprunner.baleen.xsd.xml.ComplexType
import com.shoprunner.baleen.xsd.xml.Element
import com.shoprunner.baleen.xsd.xml.MaxInclusive
import com.shoprunner.baleen.xsd.xml.MaxLength
import com.shoprunner.baleen.xsd.xml.MinInclusive
import com.shoprunner.baleen.xsd.xml.MinLength
import com.shoprunner.baleen.xsd.xml.Restriction
import com.shoprunner.baleen.xsd.xml.Schema
import com.shoprunner.baleen.xsd.xml.Sequence
import com.shoprunner.baleen.xsd.xml.SimpleType
import java.io.PrintStream
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

object XsdGenerator {
    private fun getDataDescriptions(attrs: Iterable<AttributeDescription>, descriptions: MutableSet<DataDescription>): Set<DataDescription> {
        attrs.forEach { attr ->
            val type = attr.type
            when (type) {
                is DataDescription -> {
                    descriptions.add(type)
                    getDataDescriptions(type.attrs, descriptions)
                }
                is OccurrencesType -> {
                    val memberType = type.memberType
                    if (memberType is DataDescription) {
                        descriptions.add(memberType)
                        getDataDescriptions(memberType.attrs, descriptions)
                    }
                }
            }
        }

        return descriptions
    }

    /**
     * Maps baleen type to type details that are used for XSD.
     */
    fun defaultTypeMapper(baleenType: BaleenType): TypeDetails =
        when(baleenType) {
            is AllowsNull<*> -> defaultTypeMapper(baleenType.type)
            is DataDescription -> TypeDetails(baleenType.name)
            is FloatType -> TypeDetails(simpleType = SimpleType(
                                Restriction(
                                    base="xs:double",
                                    maxInclusive = if (baleenType.max.isFinite()) MaxInclusive(baleenType.max.toBigDecimal()) else null,
                                    minInclusive = if (baleenType.min.isFinite()) MinInclusive(baleenType.min.toBigDecimal()) else null)
                            ))
            is LongType -> TypeDetails(simpleType = SimpleType(
                                Restriction(
                                    base="xs:int",
                                    maxInclusive = MaxInclusive(baleenType.max.toBigDecimal()),
                                    minInclusive = MinInclusive(baleenType.min.toBigDecimal()))
                            ))
            is OccurrencesType -> defaultTypeMapper(baleenType.memberType).copy(maxOccurs = "unbounded")
            is StringCoercibleToFloat -> defaultTypeMapper(baleenType.type)
            is StringCoercibleToLong -> defaultTypeMapper(baleenType.type)
            is StringType -> TypeDetails(
                                simpleType = SimpleType(
                                        Restriction(
                                            base="xs:string",
                                            maxLength = MaxLength(baleenType.max),
                                            minLength = MinLength(baleenType.min))
                                        ))
            else -> throw Exception("Unknown type: ${baleenType.name()}")
        }

    private fun generateType(type: DataDescription, typeMapper: TypeMapper) =
            ComplexType(
                name = type.name,
                annotation = createDocumentationAnnotation(type.markdownDescription),
                sequence = Sequence(type.attrs.map{ generateElement(it, typeMapper) })
            )

    private fun generateElement(attr: AttributeDescription, typeMapper: TypeMapper): Element {
        val typeDetails = typeMapper(attr.type)
        return Element(name = attr.name,
            type = typeDetails.type,
            minOccurs = if (attr.required) null else 0,
            maxOccurs = typeDetails.maxOccurs,
            annotation = createDocumentationAnnotation(attr.markdownDescription),
            simpleType = typeDetails.simpleType)
    }

    private fun createDocumentationAnnotation(doc: String) =
        doc.let { if (it.isNotBlank()) Annotation(documentation = it) else null }


    /**
     * Creates an XSD from a data description.
     */
    fun encode(dataDescription: DataDescription, outputStream: PrintStream, typeMapper: TypeMapper = ::defaultTypeMapper) {
        val jaxbContext = JAXBContext.newInstance(Schema::class.java)
        val jaxbMarshaller = jaxbContext.createMarshaller()

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

        val types = getDataDescriptions(dataDescription.attrs, mutableSetOf(dataDescription))

        val complexTypes = types.map{generateType(it, typeMapper)}

        val schema = Schema(
            elements = listOf(
                Element(name = dataDescription.name,
                    type = dataDescription.name,
                    annotation = createDocumentationAnnotation(dataDescription.markdownDescription))),
            complexTypes = complexTypes)

        jaxbMarshaller.marshal(schema, outputStream)
    }
}