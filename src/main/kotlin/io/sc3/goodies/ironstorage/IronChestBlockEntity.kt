package io.sc3.goodies.ironstorage

import io.sc3.goodies.util.ChestUtil
import net.minecraft.block.BlockState
import net.minecraft.block.entity.ChestLidAnimator
import net.minecraft.block.entity.LidOpenable
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.block.entity.ViewerCountManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundEvents.BLOCK_CHEST_CLOSE
import net.minecraft.sound.SoundEvents.BLOCK_CHEST_OPEN
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class IronChestBlockEntity(
  private val variant: IronStorageVariant,
  pos: BlockPos,
  state: BlockState,
) : LootableContainerBlockEntity(variant.chestBlockEntityType, pos, state), LidOpenable {
  private var inv: DefaultedList<ItemStack> =
    DefaultedList.ofSize(variant.size, ItemStack.EMPTY)

  private val lidAnimator = ChestLidAnimator()
  val stateManager = object : ViewerCountManager() {
    override fun onContainerOpen(world: World, pos: BlockPos, state: BlockState) {
      ChestUtil.playSound(world, pos, BLOCK_CHEST_OPEN)
    }

    override fun onContainerClose(world: World, pos: BlockPos, state: BlockState) {
      ChestUtil.playSound(world, pos, BLOCK_CHEST_CLOSE)
    }

    override fun onViewerCountUpdate(world: World, pos: BlockPos, state: BlockState, oldViewerCount: Int,
                                     newViewerCount: Int) {
      onInvOpenOrClose(world, pos, state, newViewerCount)
    }

    override fun isPlayerViewing(player: PlayerEntity): Boolean {
      val screen = player.currentScreenHandler as? IronChestScreenHandler ?: return false
      return screen.inv == this@IronChestBlockEntity
    }
  }

  override fun size() = variant.size

  override fun getContainerName(): Text =
    translatable("block.sc-goodies.${variant.chestId}")

  override fun readNbt(nbt: NbtCompound) {
    super.readNbt(nbt)
    inv = DefaultedList.ofSize(variant.size, ItemStack.EMPTY)
    if (!deserializeLootTable(nbt)) {
      Inventories.readNbt(nbt, inv)
    }
  }

  override fun writeNbt(nbt: NbtCompound) {
    super.writeNbt(nbt)
    if (!serializeLootTable(nbt)) {
      Inventories.writeNbt(nbt, inv)
    }
  }

  override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory) =
    IronChestScreenHandler(variant, syncId, playerInventory, this)

  override fun getInvStackList() = inv

  override fun setInvStackList(list: DefaultedList<ItemStack>) {
    inv = list
  }

  override fun getAnimationProgress(tickDelta: Float) =
    lidAnimator.getProgress(tickDelta)

  private fun onInvOpenOrClose(world: World, pos: BlockPos, state: BlockState, newViewerCount: Int) {
    val block = state.block
    world.addSyncedBlockEvent(pos, block, 1, newViewerCount)
  }

  override fun onSyncedBlockEvent(type: Int, data: Int): Boolean = if (type == 1) {
    lidAnimator.setOpen(data > 0)
    true
  } else {
    super.onSyncedBlockEvent(type, data)
  }

  override fun onOpen(player: PlayerEntity) {
    if (!removed && !player.isSpectator) {
      stateManager.openContainer(player, world, pos, cachedState)
    }
  }

  override fun onClose(player: PlayerEntity) {
    if (!removed && !player.isSpectator) {
      stateManager.closeContainer(player, world, pos, cachedState)
    }
  }

  companion object {
    fun clientTick(world: World, pos: BlockPos, state: BlockState, be: IronChestBlockEntity) {
      be.lidAnimator.step()
    }

    fun scheduledTick(world: World, pos: BlockPos) {
      val be = world.getBlockEntity(pos) as? IronChestBlockEntity ?: return
      if (!be.removed) {
        be.stateManager.updateViewerCount(be.world, be.pos, be.cachedState)
      }
    }
  }
}
