package pw.switchcraft.goodies.enderstorage

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.IPeripheral
import net.minecraft.util.DyeColor
import kotlin.math.floor
import kotlin.math.log2

class EnderStoragePeripheral(val be: EnderStorageBlockEntity) : IPeripheral {
  @LuaFunction(mainThread = true)
  fun setFrequency(left: Int, middle: Int, right: Int) {
    if (be.frequency.personal && !be.computerChangesEnabled) {
      throw LuaException("Computer changes are disabled")
    }

    be.frequency = be.frequency.copy(
      left = intToColor(left),
      middle = intToColor(middle),
      right = intToColor(right)
    )
  }

  private fun intToColor(n: Int): DyeColor =
    DyeColor.byId(floor(log2(n.toDouble())).toInt())

  @Suppress("CovariantEquals")
  override fun equals(other: IPeripheral?): Boolean = this == other

  override fun getType() = "ender_storage"
}
