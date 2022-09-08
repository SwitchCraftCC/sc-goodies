package pw.switchcraft.goodies.enderstorage

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import pw.switchcraft.goodies.Registration.ModScreens
import pw.switchcraft.goodies.util.ChestScreenHandler

class EnderStorageScreenHandler(
  syncId: Int,
  playerInv: PlayerInventory,
  inv: Inventory,

  val pos: BlockPos,
  val frequency: Frequency
) : ChestScreenHandler(syncId, playerInv, inv, ModScreens.enderStorage, rows = 3, yStart = 35, playerYStart = 49) {
  constructor(syncId: Int, playerInv: PlayerInventory, buf: PacketByteBuf) :
    this(
      syncId,
      playerInv,
      SimpleInventory(EnderStorageProvider.INVENTORY_SIZE),
      buf.readBlockPos(),
      Frequency.fromPacket(buf)
    )

  override fun close(player: PlayerEntity) {
    super.close(player)

    val world = player.world
    if (player.world == null || player.world.isClient) return

    val be = world.getBlockEntity(pos) as? EnderStorageBlockEntity ?: return
    be.removeViewer(player)
  }
}
