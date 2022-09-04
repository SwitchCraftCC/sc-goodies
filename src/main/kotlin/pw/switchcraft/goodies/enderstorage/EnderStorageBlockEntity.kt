package pw.switchcraft.goodies.enderstorage

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.ChestLidAnimator
import net.minecraft.block.entity.LidOpenable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import pw.switchcraft.goodies.Registration
import pw.switchcraft.goodies.Registration.ModBlockEntities
import pw.switchcraft.goodies.enderstorage.EnderStorageProvider.EnderStorageInventory

class EnderStorageBlockEntity(
  pos: BlockPos,
  state: BlockState,
) : FrequencyBlockEntity(ModBlockEntities.enderStorage, pos, state), LidOpenable, ExtendedScreenHandlerFactory {
  private val lidAnimator = ChestLidAnimator()

  val inv: EnderStorageInventory?
    get() {
      val world = world ?: return null
      return when {
        !world.isClient && world is ServerWorld -> {
          EnderStorageProvider.getInventory(world.server, frequency, this)
        }
        else -> null
      }
    }

  private var viewerCount = 0

  val ownerText: Text
    get() {
      val key = Registration.ModBlocks.enderStorage.translationKey
      return if (frequency.personal) {
        translatable("$key.owner_name", frequency.ownerName ?: "Unknown")
      } else {
        translatable("$key.public")
      }
    }

  override fun getAnimationProgress(tickDelta: Float) =
    lidAnimator.getProgress(tickDelta)

  override fun onSyncedBlockEvent(type: Int, data: Int): Boolean = if (type == 1) {
    lidAnimator.setOpen(data > 0)
    true
  } else {
    super.onSyncedBlockEvent(type, data)
  }

  fun updateViewerCount() {
    val world = world ?: return

    val invViewers = inv?.viewerCount ?: 0
    if (!world.isClient && viewerCount != invViewers) {
      viewerCount = invViewers
      world.addSyncedBlockEvent(pos, cachedState.block, 1, invViewers)
    }
  }

  override fun onFrequencyChange(oldValue: Frequency, newValue: Frequency) {
    // At this point, `inv` has not changed to the new block entity. Remove us as a viewer
    inv?.removeBlockEntity(this)
  }

  override fun onUpdate() {
    super.onUpdate()
    inv?.addBlockEntity(this)
  }

  override fun createMenu(syncId: Int, playerInv: PlayerInventory, player: PlayerEntity): ScreenHandler? =
    inv?.let { EnderStorageScreenHandler(syncId, playerInv, it) }

  override fun getDisplayName(): Text = cachedState.block.name

  override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
    buf.writeBlockPos(pos)
  }

  companion object {
    fun clientTick(world: World, pos: BlockPos, state: BlockState, be: EnderStorageBlockEntity) {
      be.lidAnimator.step()
    }

    fun scheduledTick(world: World, pos: BlockPos) {
      val be = world.getBlockEntity(pos) as? EnderStorageBlockEntity ?: return
      if (!be.removed) {
        be.updateViewerCount()
      }
    }

    internal fun initEvents() {
      ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register { be, _ ->
        if (be is EnderStorageBlockEntity) {
          be.inv?.addBlockEntity(be)
        }
      }

      ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register { be, _ ->
        if (be is EnderStorageBlockEntity) {
          be.inv?.removeBlockEntity(be)
        }
      }
    }
  }
}
