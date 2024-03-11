package io.sc3.goodies.enderstorage

import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.ScGoodies.modId
import io.sc3.goodies.util.UuidSerializer
import io.sc3.library.ext.optCompound
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.DyeColor
import net.minecraft.util.Formatting
import java.util.*

@Serializable
data class Frequency(
  @Serializable(with = UuidSerializer::class)
  val owner: UUID? = null,

  val ownerName: String? = null, // Not used for identification, just for rendering

  val left  : DyeColor = DyeColor.WHITE,
  val middle: DyeColor = DyeColor.WHITE,
  val right : DyeColor = DyeColor.WHITE
) {
  val personal
    get() = owner != null

  @delegate:Transient
  val ownerText: Text by lazy {
    val key = ModBlocks.enderStorage.translationKey
    if (personal) {
      translatable("$key.owner_name", ownerName ?: "Unknown")
    } else {
      translatable("$key.public")
    }
  }

  fun toNbt(): NbtCompound {
    val nbt = NbtCompound()
    if (owner != null) nbt.putUuid("owner", owner)
    if (ownerName != null) nbt.putString("ownerName", ownerName)
    nbt.putByte("left", left.id.toByte())
    nbt.putByte("middle", middle.id.toByte())
    nbt.putByte("right", right.id.toByte())
    return nbt
  }

  fun toPacket(buf: PacketByteBuf) {
    buf.writeNullable(owner, PacketByteBuf::writeUuid)
    buf.writeNullable(ownerName, PacketByteBuf::writeString)
    buf.writeEnumConstant(left)
    buf.writeEnumConstant(middle)
    buf.writeEnumConstant(right)
  }

  fun toKey() = json.encodeToString(this)

  fun toTextParts(vararg formatting: Formatting): Array<Text> {
    val key = "block.$modId.ender_storage.frequency"
    return arrayOf(
      translatable("$key.${left.getName()}").formatted(*formatting),
      translatable("$key.${middle.getName()}").formatted(*formatting),
      translatable("$key.${right.getName()}").formatted(*formatting),
    )
  }

  fun toText(): Text = translatable(
    "block.$modId.ender_storage.frequency",
    *toTextParts()
  )

  fun dyeColor(index: Int): DyeColor = when (index) {
    0 -> left
    1 -> middle
    2 -> right
    else -> throw IllegalArgumentException("Invalid index $index")
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Frequency

    if (owner != other.owner) return false
    if (left != other.left) return false
    if (middle != other.middle) return false
    if (right != other.right) return false

    return true
  }

  override fun hashCode(): Int {
    var result = owner?.hashCode() ?: 0
    result = 31 * result + left.hashCode()
    result = 31 * result + middle.hashCode()
    result = 31 * result + right.hashCode()
    return result
  }

  companion object {
    private val json = Json {
      ignoreUnknownKeys = true
    }

    fun fromNbt(nbt: NbtCompound, server: MinecraftServer? = null): Frequency {
      val owner = if (nbt.containsUuid("owner")) nbt.getUuid("owner") else null

      val ownerName = if (nbt.contains("ownerName")) {
        nbt.getString("ownerName")
      } else if (owner != null && server != null) {
        server.userCache?.getByUuid(owner)?.orElse(null)?.name
      } else {
        null
      }

      return Frequency(
        owner,
        ownerName,
        DyeColor.byId(nbt.getByte("left").toInt()),
        DyeColor.byId(nbt.getByte("middle").toInt()),
        DyeColor.byId(nbt.getByte("right").toInt())
      )
    }

    fun fromPacket(buf: PacketByteBuf) = Frequency(
      owner     = buf.readNullable(PacketByteBuf::readUuid),
      ownerName = buf.readNullable(PacketByteBuf::readString),
      left      = buf.readEnumConstant(DyeColor::class.java),
      middle    = buf.readEnumConstant(DyeColor::class.java),
      right     = buf.readEnumConstant(DyeColor::class.java)
    )

    fun fromKey(key: String) = json.decodeFromString<Frequency>(key)

    fun fromStack(stack: ItemStack): Frequency? {
      // Get the frequency either from the item's direct NBT, or its BlockEntity NBT tag (creative pick)
      val frequencyNbt = stack.getSubNbt("frequency")
        ?: BlockItem.getBlockEntityNbt(stack)?.optCompound("frequency")
      return fromNbt(frequencyNbt ?: return null)
    }
  }
}
