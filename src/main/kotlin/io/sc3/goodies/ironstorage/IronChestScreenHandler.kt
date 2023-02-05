package io.sc3.goodies.ironstorage

import io.sc3.goodies.util.ChestScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ScreenHandlerType

open class IronChestScreenHandler(
  val variant: IronStorageVariant,
  syncId: Int,
  playerInv: PlayerInventory,
  inv: Inventory,
  type: ScreenHandlerType<out IronChestScreenHandler> = variant.chestScreenHandlerType
) : ChestScreenHandler(syncId, playerInv, inv, type, variant.rows, variant.columns) {
  constructor(variant: IronStorageVariant, syncId: Int, playerInv: PlayerInventory) :
    this(variant, syncId, playerInv, SimpleInventory(variant.size))
}
