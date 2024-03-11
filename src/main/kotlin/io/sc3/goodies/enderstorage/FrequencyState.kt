package io.sc3.goodies.enderstorage

import net.minecraft.SharedConstants
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf

const val MAX_NAME_LENGTH = 48
const val MAX_DESCRIPTION_LENGTH = 512

/** Additional data for the frequency, that is not part of the frequency spec itself. */
data class FrequencyState(
  var name: String? = null,
  var description: String? = null
) {
  /** Write the non-key parts of this frequency (the name and description) to the given NBT. */
  fun toNbt(nbt: NbtCompound) {
    if (name != null) nbt.putString("name", sanitiseName(name))
    if (description != null) nbt.putString("description", sanitiseDescription(description))
  }

  fun toPacket(buf: PacketByteBuf) {
    buf.writeNullable(name, PacketByteBuf::writeString)
    buf.writeNullable(description, PacketByteBuf::writeString)
  }

  companion object {
    /** Read the non-key parts of this frequency (the name and description) from the given NBT. */
    fun fromNbt(nbt: NbtCompound) = FrequencyState(
      name = nbt.getString("name")?.takeIf { isValidName(it) },
      description = nbt.getString("description")?.takeIf { isValidDescription(it) }
    )

    fun fromPacket(buf: PacketByteBuf) = FrequencyState(
      name = buf.readNullable(PacketByteBuf::readString),
      description = buf.readNullable(PacketByteBuf::readString)
    )

    fun isValidName(s: String?) = s?.length in 1..MAX_NAME_LENGTH
    fun isValidDescription(s: String?) = s?.length in 1..MAX_DESCRIPTION_LENGTH

    fun sanitiseName(s: String?) = s?.takeIf { isValidName(it) }
      ?.let { stripInvalidChars(it) }
    fun sanitiseDescription(s: String?) = s?.takeIf { isValidDescription(it) }
      ?.let { stripInvalidChars(it, true) }

    /** Strip invalid characters, but allow the section sign. If sc-networking is installed, this will also strip
     * private-use font characters, but let's allow the Krist symbol (U+E000) */
    private fun stripInvalidChars(s: String, allowNewlines: Boolean = false) =
      s.filter { SharedConstants.isValidChar(it) || it == '§' || it == '\uE000' || (allowNewlines && it == '\n') }
  }
}
