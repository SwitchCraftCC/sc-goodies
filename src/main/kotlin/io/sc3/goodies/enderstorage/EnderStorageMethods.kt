package pw.switchcraft.goodies.enderstorage

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.peripheral.GenericPeripheral
import dan200.computercraft.api.peripheral.PeripheralType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.DyeColor
import pw.switchcraft.goodies.ScGoodies.ModId
import kotlin.math.floor
import kotlin.math.log2

object EnderStorageMethods : GenericPeripheral {
  override fun id() = ModId("ender_storage").toString()

  override fun getType(): PeripheralType =
    PeripheralType.ofType("ender_storage")

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun setFrequency(be: EnderStorageBlockEntity, left: Int, middle: Int, right: Int) {
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

  internal fun register() {
    if (FabricLoader.getInstance().isModLoaded("computercraft")) {
      ComputerCraftAPI.registerGenericSource(EnderStorageMethods)
    }
  }
}
