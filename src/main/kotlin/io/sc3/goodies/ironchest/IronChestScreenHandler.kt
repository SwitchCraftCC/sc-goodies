package io.sc3.goodies.ironchest

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ScreenHandlerType
import io.sc3.goodies.util.ChestScreenHandler

open class IronChestScreenHandler(
  val variant: IronChestVariant,
  syncId: Int,
  playerInv: PlayerInventory,
  inv: Inventory,
  type: ScreenHandlerType<out IronChestScreenHandler> = variant.chestScreenHandlerType
) : ChestScreenHandler(syncId, playerInv, inv, type, variant.rows, variant.columns) {
  constructor(variant: IronChestVariant, syncId: Int, playerInv: PlayerInventory) :
    this(variant, syncId, playerInv, SimpleInventory(variant.size))
}
