

package com.zobaer53.zedmovies.data.network.serializer

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.ParseException

class LocalDateSerializer : KSerializer<LocalDate?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "LocalDate",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: LocalDate?) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): LocalDate? = try {
        val string = decoder.decodeString()
        if (string.isNotEmpty()) {
            string.toLocalDate()
        } else {
            null
        }
    } catch (exception: ParseException) {
        throw SerializationException(exception)
    }
}
