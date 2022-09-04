package pw.switchcraft.goodies.enderstorage

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.GenericContainerScreenHandler
import pw.switchcraft.goodies.Registration.ModScreens

private const val SIZE = 27
private const val ROWS = 3

class EnderStorageScreenHandler(
  syncId: Int,
  playerInv: PlayerInventory,
  val inv: Inventory,
) : GenericContainerScreenHandler(ModScreens.enderStorage, syncId, playerInv, inv, ROWS) {
  constructor(syncId: Int, playerInv: PlayerInventory) :
    this(syncId, playerInv, SimpleInventory(SIZE))

  // TODO: synced properties for name, frequency
}
