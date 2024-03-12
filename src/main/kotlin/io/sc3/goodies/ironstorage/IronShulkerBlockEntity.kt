package io.sc3.goodies.ironstorage

import io.sc3.goodies.ironstorage.IronShulkerBlock.Companion.facing
import io.sc3.goodies.util.ChestUtil
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.block.entity.ShulkerBoxBlockEntity.AnimationStage.*
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.MovementType
import net.minecraft.entity.mob.ShulkerEntity.calculateBoundingBox
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.sound.SoundEvents.BLOCK_SHULKER_BOX_CLOSE
import net.minecraft.sound.SoundEvents.BLOCK_SHULKER_BOX_OPEN
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.DyeColor
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.*
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent
import java.util.stream.IntStream

class IronShulkerBlockEntity(
  private val variant: IronStorageVariant,
  pos: BlockPos,
  state: BlockState,
) : LootableContainerBlockEntity(variant.shulkerBlockEntityType, pos, state), SidedInventory {
  private var inv: DefaultedList<ItemStack> =
    DefaultedList.ofSize(variant.size, ItemStack.EMPTY)
  private val availableSlots: IntArray = IntStream.range(0, variant.size).toArray()

  private var viewerCount = 0
  var animationStage = CLOSED
  private var animationProgress = 0.0f
  private var prevAnimationProgress = 0.0f

  var cachedColor: DyeColor? = null
    private set

  init {
    cachedColor = IronShulkerBlock.getColor(state.block)
  }

  override fun size() = variant.size

  override fun getContainerName(): Text = translatable(cachedState.block.translationKey)

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
    IronShulkerScreenHandler(variant, syncId, playerInventory, this)

  override fun getInvStackList() = inv

  override fun setInvStackList(list: DefaultedList<ItemStack>) {
    inv = list
  }

  private fun updateAnimation(world: World, pos: BlockPos, state: BlockState) {
    prevAnimationProgress = animationProgress
    when (animationStage) {
      CLOSED -> animationProgress = 0.0f
      OPENING -> {
        animationProgress += 0.1f
        if (animationProgress >= 1.0f) {
          animationStage = OPENED
          animationProgress = 1.0f
          updateNeighborStates(world, pos, state)
        }

        pushEntities(world, pos, state)
      }
      CLOSING -> {
        animationProgress -= 0.1f
        if (animationProgress <= 0.0f) {
          animationStage = CLOSED
          animationProgress = 0.0f
          updateNeighborStates(world, pos, state)
        }
      }
      OPENED -> animationProgress = 1.0f
    }
  }

  fun getAnimationProgress(delta: Float) = MathHelper.lerp(delta, prevAnimationProgress, animationProgress)

  fun getBoundingBox(state: BlockState): Box =
    calculateBoundingBox(state.get(facing), 0.5f * getAnimationProgress(1.0f))

  override fun onSyncedBlockEvent(type: Int, data: Int): Boolean = when (type) {
    1 -> {
      viewerCount = data
      when (data) {
        0 -> {
          animationStage = CLOSING
          updateNeighborStates(world, pos, cachedState)
        }
        1 -> {
          animationStage = OPENING
          updateNeighborStates(world, pos, cachedState)
        }
      }
      true
    }
    else -> { super.onSyncedBlockEvent(type, data) }
  }

  private fun updateNeighborStates(world: World?, pos: BlockPos, state: BlockState) {
    state.updateNeighbors(world ?: return, pos, Block.NOTIFY_ALL)
  }

  private fun pushEntities(world: World, pos: BlockPos, state: BlockState) {
    if (state.block !is IronShulkerBlock) return

    val direction = state.get(facing)
    val box = calculateBoundingBox(direction, prevAnimationProgress, animationProgress).offset(pos)

    val list = world.getOtherEntities(null, box)
    if (list.isEmpty()) return

    list
      .filter { it.pistonBehavior != PistonBehavior.IGNORE }
      .forEach {
        it.move(
          MovementType.SHULKER_BOX,
          Vec3d(
            (box.xLength + 0.01) * direction.offsetX.toDouble(),
            (box.yLength + 0.01) * direction.offsetY.toDouble(),
            (box.zLength + 0.01) * direction.offsetZ.toDouble()
          )
        )
      }
  }

  override fun onOpen(player: PlayerEntity) {
    val world = world ?: return
    if (player.isSpectator) return

    viewerCount = (viewerCount + 1).coerceAtLeast(1)

    world.addSyncedBlockEvent(pos, cachedState.block, 1, viewerCount)

    // Play the sound for the first player to open the shulker box
    if (viewerCount == 1) {
      world.emitGameEvent(player, GameEvent.CONTAINER_OPEN, pos)
      ChestUtil.playSound(world, pos, BLOCK_SHULKER_BOX_OPEN)
    }
  }

  override fun onClose(player: PlayerEntity) {
    val world = world ?: return
    if (player.isSpectator) return

    viewerCount = (viewerCount - 1).coerceAtLeast(0)

    world.addSyncedBlockEvent(pos, cachedState.block, 1, viewerCount)

    if (viewerCount <= 0) {
      world.emitGameEvent(player, GameEvent.CONTAINER_CLOSE, pos)
      ChestUtil.playSound(world, pos, BLOCK_SHULKER_BOX_CLOSE)
    }
  }

  override fun getAvailableSlots(side: Direction) = availableSlots

  override fun isValid(slot: Int, stack: ItemStack) = stack.item.canBeNested()

  override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean =
    Block.getBlockFromItem(stack.item) !is IronShulkerBlock

  override fun canExtract(slot: Int, stack: ItemStack, dir: Direction) = true

  fun suffocates() = animationStage == CLOSED

  companion object {
    fun tick(world: World, pos: BlockPos, state: BlockState, be: IronShulkerBlockEntity) {
      be.updateAnimation(world, pos, state)
    }

    fun scheduledTick(world: World, pos: BlockPos) {
      val be = world.getBlockEntity(pos) as? IronShulkerBlockEntity ?: return
      if (!be.removed) {
        be.updateAnimation(world, pos, world.getBlockState(pos))
      }
    }
  }
}
