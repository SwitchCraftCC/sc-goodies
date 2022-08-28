package pw.switchcraft.goodies.shulker

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.slot.ShulkerBoxSlot
import net.minecraft.screen.slot.Slot
import pw.switchcraft.goodies.chest.IronChestScreenHandler
import pw.switchcraft.goodies.chest.IronChestVariant

class IronShulkerScreenHandler(
  variant: IronChestVariant,
  syncId: Int,
  playerInv: PlayerInventory,
  inv: Inventory
) : IronChestScreenHandler(variant, syncId, playerInv, inv, type = variant.shulkerScreenHandlerType) {
  constructor(variant: IronChestVariant, syncId: Int, playerInv: PlayerInventory) :
    this(variant, syncId, playerInv, SimpleInventory(variant.size))

  override fun makeSlot(index: Int, x: Int, y: Int): Slot =
    ShulkerBoxSlot(inv, index, x, y)
}
