package io.sc3.goodies.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

class UuidSerializer : KSerializer<UUID> {
  override val descriptor = String.serializer().descriptor
  override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())
  override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())
}
