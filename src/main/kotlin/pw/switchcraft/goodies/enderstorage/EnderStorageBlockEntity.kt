package pw.switchcraft.goodies.enderstorage

import net.minecraft.block.BlockState
import net.minecraft.block.entity.ChestLidAnimator
import net.minecraft.block.entity.LidOpenable
import net.minecraft.block.entity.ViewerCountManager
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.SoundEvents.BLOCK_ENDER_CHEST_CLOSE
import net.minecraft.sound.SoundEvents.BLOCK_ENDER_CHEST_OPEN
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import pw.switchcraft.goodies.Registration.ModBlockEntities
import pw.switchcraft.goodies.util.ChestUtil

class EnderStorageBlockEntity(
  pos: BlockPos,
  state: BlockState,
) : FrequencyBlockEntity(ModBlockEntities.enderStorage, pos, state), LidOpenable {
  private val lidAnimator = ChestLidAnimator()
  val stateManager = object : ViewerCountManager() {
    override fun onContainerOpen(world: World, pos: BlockPos, state: BlockState) {
      ChestUtil.playSound(world, pos, BLOCK_ENDER_CHEST_OPEN)
    }

    override fun onContainerClose(world: World, pos: BlockPos, state: BlockState) {
      ChestUtil.playSound(world, pos, BLOCK_ENDER_CHEST_CLOSE)
    }

    override fun onViewerCountUpdate(world: World, pos: BlockPos, state: BlockState, oldViewerCount: Int,
                                     newViewerCount: Int) {
      onInvOpenOrClose(world, pos, state, newViewerCount)
    }

    override fun isPlayerViewing(player: PlayerEntity): Boolean {
      return false // TODO
    }
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

  fun onOpen(player: PlayerEntity) {
    if (!removed && !player.isSpectator) {
      stateManager.openContainer(player, world, pos, cachedState)
    }
  }

  fun onClose(player: PlayerEntity) {
    if (!removed && !player.isSpectator) {
      stateManager.closeContainer(player, world, pos, cachedState)
    }
  }

  companion object {
    fun clientTick(world: World, pos: BlockPos, state: BlockState, be: EnderStorageBlockEntity) {
      be.lidAnimator.step()
    }

    fun scheduledTick(world: World, pos: BlockPos) {
      val be = world.getBlockEntity(pos) as? EnderStorageBlockEntity ?: return
      if (!be.removed) {
        be.stateManager.updateViewerCount(be.world, be.pos, be.cachedState)
      }
    }
  }
}
