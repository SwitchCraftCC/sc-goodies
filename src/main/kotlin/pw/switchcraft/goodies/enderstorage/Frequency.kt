package pw.switchcraft.goodies.enderstorage

import com.google.gson.Gson
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import pw.switchcraft.goodies.Registration.ModBlocks
import java.util.*

private val gson = Gson()

data class Frequency(
  val owner    : UUID?    = null,
  val ownerName: String?  = null,           // Not used for identification, just for rendering
  val left     : DyeColor = DyeColor.WHITE,
  val middle   : DyeColor = DyeColor.WHITE,
  val right    : DyeColor = DyeColor.WHITE
) {
  val personal
    get() = owner != null

  @delegate:Transient
  val ownerText: Text by lazy {
    val key = ModBlocks.enderStorage.translationKey
    if (personal) {
      Text.translatable("$key.owner_name", ownerName ?: "Unknown")
    } else {
      Text.translatable("$key.public")
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

  fun toKey() = gson.toJson(this)

  fun dyeColor(index: Int): DyeColor = when (index) {
    0 -> left
    1 -> middle
    2 -> right
    else -> throw IllegalArgumentException("Invalid index $index")
  }

  companion object {
    fun fromNbt(nbt: NbtCompound, server: MinecraftServer? = null): Frequency {
      val owner = if (nbt.containsUuid("owner")) nbt.getUuid("owner") else null

      val ownerName = if (nbt.contains("ownerName")) {
        nbt.getString("ownerName")
      } else if (owner != null && server != null) {
        server.userCache.getByUuid(owner).orElse(null)?.name
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

    fun fromPacket(buf: PacketByteBuf): Frequency = Frequency(
      owner = buf.readNullable(PacketByteBuf::readUuid),
      ownerName = buf.readNullable(PacketByteBuf::readString),
      left = buf.readEnumConstant(DyeColor::class.java),
      middle = buf.readEnumConstant(DyeColor::class.java),
      right = buf.readEnumConstant(DyeColor::class.java)
    )

    fun fromKey(key: String) = gson.fromJson(key, Frequency::class.java)
  }
}
