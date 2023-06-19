package io.sc3.goodies.turtles

import MagnetTurtlePeripheral
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import io.sc3.goodies.Registration
import io.sc3.goodies.itemmagnet.ItemMagnetItem
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
class MagneticTurtleUpgrade(id: Identifier) : ITurtleUpgrade {
  var id: Identifier

  init {
    this.id = id
  }

  override fun getType(): TurtleUpgradeType {
    return TurtleUpgradeType.PERIPHERAL
  }

  override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral {
    return MagnetTurtlePeripheral(turtle, side)
  }

  override fun getUpgradeID(): Identifier{
    return id
  }

  override fun getUnlocalisedAdjective(): String {
    return "upgrade.sc-goodies.magnet.adjective"
  }

  override fun getCraftingItem(): ItemStack {
    return Registration.ModItems.itemMagnet.defaultStack
  }

  override fun isItemSuitable(stack: ItemStack): Boolean {
    return stack.item is ItemMagnetItem
  }
}
