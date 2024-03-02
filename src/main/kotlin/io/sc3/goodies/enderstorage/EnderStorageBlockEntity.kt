package io.sc3.goodies.enderstorage

import io.sc3.goodies.Registration.ModBlockEntities
import io.sc3.goodies.enderstorage.EnderStorageProvider.EnderStorageInventory
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.block.entity.ChestLidAnimator
import net.minecraft.block.entity.LidOpenable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class EnderStorageBlockEntity(
  pos: BlockPos,
  state: BlockState,
) : FrequencyBlockEntity(ModBlockEntities.enderStorage, pos, state), LidOpenable, ExtendedScreenHandlerFactory,
  Inventory {
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
  private val viewingPlayers = mutableSetOf<PlayerEntity>()

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
    closeViewers()
    inv?.updateViewers()
  }

  override fun onUpdate() {
    super.onUpdate()
    inv?.addBlockEntity(this)
    inv?.updateViewers()
  }

  override fun onBroken() {
    super.onBroken()
    closeViewers()
    inv?.updateViewers()
    inv?.removeBlockEntity(this)
  }

  override fun createMenu(syncId: Int, playerInv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
    val inv = inv ?: return null
    viewingPlayers.add(player)
    return EnderStorageScreenHandler(syncId, playerInv, inv, pos, frequency, frequencyState)
  }

  override fun getDisplayName(): Text = cachedState.block.name

  override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
    buf.writeBlockPos(pos)
    frequency.toPacket(buf)
    frequencyState.toPacket(buf)
  }

  fun removeViewer(player: PlayerEntity) {
    viewingPlayers.remove(player)
  }

  private fun closeViewers() {
    val screensToClose = viewingPlayers.filter {
      val handler = it.currentScreenHandler as? EnderStorageScreenHandler ?: return@filter false
      it.world == world && handler.pos == pos
    }

    screensToClose.forEach {
      (it as? ServerPlayerEntity)?.closeHandledScreen()
    }

    viewingPlayers.clear()
  }

  override fun size() = inv?.size() ?: 0
  override fun isEmpty() = inv?.isEmpty ?: true

  override fun getStack(slot: Int): ItemStack =
    inv?.getStack(slot) ?: ItemStack.EMPTY

  override fun removeStack(slot: Int, amount: Int): ItemStack =
    inv?.removeStack(slot, amount) ?: ItemStack.EMPTY

  override fun removeStack(slot: Int): ItemStack =
    inv?.removeStack(slot) ?: ItemStack.EMPTY

  override fun canPlayerUse(player: PlayerEntity): Boolean =
    inv?.canPlayerUse(player) ?: false

  override fun setStack(slot: Int, stack: ItemStack) {
    inv?.setStack(slot, stack)
  }

  override fun clear() {
    inv?.clear()
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
