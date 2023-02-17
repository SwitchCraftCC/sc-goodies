package io.sc3.goodies.ironstorage

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.slot.ShulkerBoxSlot
import net.minecraft.screen.slot.Slot

class IronShulkerScreenHandler(
  variant: IronStorageVariant,
  syncId: Int,
  playerInv: PlayerInventory,
  inv: Inventory
) : IronChestScreenHandler(variant, syncId, playerInv, inv, type = variant.shulkerScreenHandlerType) {
  constructor(variant: IronStorageVariant, syncId: Int, playerInv: PlayerInventory) :
    this(variant, syncId, playerInv, SimpleInventory(variant.size))

  override fun makeSlot(index: Int, x: Int, y: Int): Slot =
    ShulkerBoxSlot(inv, index, x, y)
}
