package pw.switchcraft.goodies.enderstorage

import com.google.gson.Gson
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.util.DyeColor
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

  fun toNbt(): NbtCompound {
    val nbt = NbtCompound()
    if (owner != null) nbt.putUuid("owner", owner)
    if (ownerName != null) nbt.putString("ownerName", ownerName)
    nbt.putByte("left", left.id.toByte())
    nbt.putByte("middle", middle.id.toByte())
    nbt.putByte("right", right.id.toByte())
    return nbt
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

    fun fromKey(key: String) = gson.fromJson(key, Frequency::class.java)
  }
}
