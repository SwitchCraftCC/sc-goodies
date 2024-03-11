package io.sc3.goodies.enderstorage

import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.GenericPeripheral
import dan200.computercraft.api.peripheral.PeripheralType
import io.sc3.goodies.ScGoodies.ModId
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.util.DyeColor
import kotlin.math.floor
import kotlin.math.log2

object EnderStorageMethods : GenericPeripheral {
  override fun id() = ModId("ender_storage").toString()

  override fun getType(): PeripheralType =
    PeripheralType.ofType("ender_storage")

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getFrequency(be: EnderStorageBlockEntity): MethodResult {
    val freq = be.frequency
    return MethodResult.of(freq.left.toInt(), freq.middle.toInt(), freq.right.toInt())
  }

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getOwner(be: EnderStorageBlockEntity): MethodResult {
    val freq = be.frequency
    return MethodResult.of(freq.owner?.toString(), freq.ownerName)
  }

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun isPersonal(be: EnderStorageBlockEntity): Boolean =
    be.frequency.personal

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun areComputerChangesEnabled(be: EnderStorageBlockEntity): Boolean =
    !be.frequency.personal || be.computerChangesEnabled

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun setFrequency(be: EnderStorageBlockEntity, left: Int, middle: Int, right: Int) {
    if (be.frequency.personal && !be.computerChangesEnabled) {
      throw LuaException("Computer changes are disabled")
    }

    be.frequency = be.frequency.copy(
      left   = left.toColor(),
      middle = middle.toColor(),
      right  = right.toColor()
    )
  }

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getName(be: EnderStorageBlockEntity): String? =
    be.frequencyState.name

  @LuaFunction(mainThread = true)
  @JvmStatic
  fun getDescription(be: EnderStorageBlockEntity): String? =
    be.frequencyState.description

  private fun Int.toColor(): DyeColor =
    DyeColor.byId(floor(log2(toDouble())).toInt())

  private fun DyeColor.toInt(): Int =
    1 shl id

  internal fun register() {
    if (FabricLoader.getInstance().isModLoaded("computercraft")) {
      ComputerCraftAPI.registerGenericSource(EnderStorageMethods)
    }
  }
}
