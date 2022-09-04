package pw.switchcraft.goodies.enderstorage

import net.minecraft.nbt.NbtCompound
import net.minecraft.util.DyeColor
import java.util.*

data class Frequency(
  val owner : UUID?    = null,
  val left  : DyeColor = DyeColor.WHITE,
  val middle: DyeColor = DyeColor.WHITE,
  val right : DyeColor = DyeColor.WHITE
) {
  val personal
    get() = owner != null

  fun toNbt(): NbtCompound {
    val nbt = NbtCompound()
    if (owner != null) nbt.putUuid("owner", owner)
    nbt.putByte("left", left.id.toByte())
    nbt.putByte("middle", middle.id.toByte())
    nbt.putByte("right", right.id.toByte())
    return nbt
  }

  fun dyeColor(index: Int): DyeColor = when (index) {
    0 -> left
    1 -> middle
    2 -> right
    else -> throw IllegalArgumentException("Invalid index $index")
  }

  companion object {
    fun fromNbt(nbt: NbtCompound): Frequency = Frequency(
      if (nbt.containsUuid("owner")) nbt.getUuid("owner") else null,
      DyeColor.byId(nbt.getByte("left").toInt()),
      DyeColor.byId(nbt.getByte("middle").toInt()),
      DyeColor.byId(nbt.getByte("right").toInt())
    )
  }
}
