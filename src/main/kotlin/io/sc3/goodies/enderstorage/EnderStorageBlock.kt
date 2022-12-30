package io.sc3.goodies.enderstorage

import io.sc3.goodies.Registration.ModBlockEntities
import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.enderstorage.EnderStorageBlock.HitShape.HitShapeType.BUTTON
import io.sc3.goodies.enderstorage.EnderStorageBlock.HitShape.HitShapeType.LATCH
import io.sc3.goodies.util.BaseBlockWithEntity
import io.sc3.library.Tooltips.addDescLines
import io.sc3.library.WaterloggableBlock
import io.sc3.library.WaterloggableBlock.Companion.waterlogged
import io.sc3.library.ext.rotateTowards
import io.sc3.library.ext.toDiv16
import io.sc3.library.ext.toDiv16VoxelShape
import net.fabricmc.fabric.api.util.NbtType.COMPOUND
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items.DIAMOND
import net.minecraft.item.Items.EMERALD
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.stat.Stat
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.*
import net.minecraft.util.Formatting.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

// Raycasts hit the very edge of the shape so the .contains() check will fail without a slight expansion. This is half
// of a voxel.
private const val BOX_EXPAND = 1.0 / 32.0

class EnderStorageBlock(
  settings: Settings
) : BaseBlockWithEntity(settings), WaterloggableBlock {
  private val openStat: Stat<Identifier> by lazy {
    Stats.CUSTOM.getOrCreateStat(Stats.OPEN_ENDERCHEST)
  }

  private val tooltipExtra by lazy {
    if (computercraftLoaded) {
      listOf(translatable("$translationKey.desc.computercraft").formatted(GRAY))
    } else {
      emptyList()
    }
  }

  init {
    defaultState = stateManager.defaultState
      .with(facing, Direction.NORTH)
      .with(waterlogged, false)
  }

  override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
    builder.add(facing, waterlogged)
  }

  override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState
    .with(facing, ctx.playerFacing.opposite)
    .with(waterlogged, placementWaterlogged(ctx))

  override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
    val be = world.getBlockEntity(pos) as? EnderStorageBlockEntity ?: return
    Frequency.fromStack(stack)?.let { be.frequency = it }
  }

  override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
    EnderStorageBlockEntity(pos, state)

  override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
    val be = world.getBlockEntity(pos) as? EnderStorageBlockEntity ?: run {
      super.onBreak(world, pos, state, player)
      return
    }

    if (!world.isClient && !player.isCreative) {
      val stack = ItemStack(ModItems.enderStorage)
      be.setStackNbt(stack)

      val itemEntity = ItemEntity(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, stack)
      itemEntity.setToDefaultPickupDelay()
      world.spawnEntity(itemEntity)
    }

    super.onBreak(world, pos, state, player)
  }

  override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState): ItemStack {
    val stack = super.getPickStack(world, pos, state)
    world.getBlockEntity(pos, ModBlockEntities.enderStorage).ifPresent { it.setStackNbt(stack) }
    return stack
  }

  override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand,
                     hit: BlockHitResult): ActionResult {
    if (world.isClient) return ActionResult.SUCCESS

    val be = world.getBlockEntity(pos) as? EnderStorageBlockEntity ?: return ActionResult.FAIL
    val frequency = be.frequency

    val stack = player.getStackInHand(hand)
    val hitShape = checkHitShape(state, pos, hit)

    when (hitShape?.type) {
      LATCH -> {
        if (player.isSneaking && frequency.personal) {
          // Shift+use a personal chest to retrieve the diamond
          // Don't remove the frequency if we're not the owner of this chest, and we're not in creative mode/op:
          if (!world.canPlayerModifyAt(player, pos) || !checkOwner(player, frequency)) {
            return ActionResult.FAIL
          }

          // Return the diamond to the inventory. FAIL if there is no room to return the item
          if (!player.isCreative && !player.inventory.insertStack(ItemStack(DIAMOND))) {
            return ActionResult.FAIL
          }

          be.frequency = frequency.copy(owner = null)
          be.computerChangesEnabled = false
          return ActionResult.SUCCESS
        } else if (!stack.isEmpty && stack.isOf(DIAMOND)) {
          if (!world.canPlayerModifyAt(player, pos)) {
            return ActionResult.FAIL
          }

          // Use a diamond to make the chest personal
          if (!frequency.personal) {
            be.frequency = frequency.copy(owner = player.gameProfile.id, ownerName = player.gameProfile.name)

            // Consume the diamond if the player isn't in creative
            if (!player.isCreative) {
              stack.decrement(1)
            }

            return ActionResult.SUCCESS
          }
        } else if (computercraftLoaded && !stack.isEmpty && stack.isOf(EMERALD) && frequency.personal) {
          // If this is a protected chest, and we are the owner, allow the use of an emerald to toggle peripheral
          // access. Do not consume the emerald. Allow admins to toggle the access even if they're not the owner.
          if (!world.canPlayerModifyAt(player, pos) || !checkOwner(player, frequency)) {
            return ActionResult.FAIL
          }

          val enabled = !be.computerChangesEnabled
          be.computerChangesEnabled = enabled

          val base = "$translationKey.computer_changes.${if (enabled) "allowed" else "denied"}"
          val enabledText = translatable("$base.colored").formatted(if (enabled) GREEN else RED)
          player.sendMessage(translatable(base, enabledText).formatted(YELLOW))
          return ActionResult.SUCCESS
        }
      }

      BUTTON -> {
        val buttonId = hitShape.buttonId ?: return ActionResult.FAIL
        val color = (stack.item as? DyeItem)?.color
        if (color != null) {
          // Don't set the frequency if we're not the owner of this chest and not in creative mode/op
          if (!world.canPlayerModifyAt(player, pos) || !checkOwner(player, frequency)) {
            return ActionResult.FAIL
          }

          val newFrequency = when (buttonId) {
            0 -> frequency.copy(left = color)
            1 -> frequency.copy(middle = color)
            2 -> frequency.copy(right = color)
            else -> frequency
          }

          // Don't waste dyes if the frequency is the same (still show the animation)
          if (frequency == newFrequency) {
            return ActionResult.SUCCESS
          }

          be.frequency = newFrequency

          // Consume the dye if the player isn't in creative
          if (!player.isCreative) {
            stack.decrement(1)
          }

          return ActionResult.SUCCESS
        }
      }

      else -> {}
    }

    // No special interaction occurred - open the chest
    val factory = createScreenHandlerFactory(state, world, pos) ?: return ActionResult.CONSUME
    player.openHandledScreen(factory)
    player.incrementStat(openStat)
    return ActionResult.CONSUME
  }

  private fun checkOwner(player: PlayerEntity, frequency: Frequency): Boolean =
    if (frequency.personal && !player.isCreativeLevelTwoOp && frequency.owner != player.uuid) {
      player.sendMessage(translatable("$translationKey.not_owner").formatted(RED))
      false
    } else {
      true
    }

  private fun checkHitShape(state: BlockState, blockPos: BlockPos, hit: BlockHitResult): HitShape? {
    val facing = state.get(facing)
    val hitPos = hit.pos.subtract(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())

    val latch = latchBoxes[facing]?.toDiv16()?.expand(BOX_EXPAND) ?: return null
    if (latch.contains(hitPos)) return HitShape(LATCH)

    for (i in 0 until 3) {
      val button = buttonBoxes[i]?.get(facing)?.toDiv16()?.expand(BOX_EXPAND) ?: return null
      if (button.contains(hitPos)) return HitShape(BUTTON, i)
    }

    return null
  }

  override fun <T : BlockEntity> getTicker(
    world: World,
    state: BlockState,
    type: BlockEntityType<T>
  ): BlockEntityTicker<T>? {
    if (!world.isClient) return null
    return checkType(type, ModBlockEntities.enderStorage, EnderStorageBlockEntity.Companion::clientTick)
  }

  override fun hasComparatorOutput(state: BlockState) = true

  override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
    val be = world.getBlockEntity(pos) as? EnderStorageBlockEntity ?: return 0
    val inv = be.inv ?: return 0
    return ScreenHandler.calculateComparatorOutput(inv)
  }

  override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, ctx: ShapeContext) = chestShape

  override fun getRaycastShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
    val facing = state.get(facing)
    return shapes[facing] ?: super.getRaycastShape(state, world, pos)
  }

  override fun getRenderType(state: BlockState) = BlockRenderType.ENTITYBLOCK_ANIMATED

  override fun mirror(state: BlockState, mirror: BlockMirror) =
    state.rotate(mirror.getRotation(state.get(facing)))

  override fun rotate(state: BlockState, rotation: BlockRotation) =
    state.with(facing, rotation.rotate(state.get(facing)))

  override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType) =
    false

  override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
    EnderStorageBlockEntity.scheduledTick(world, pos)
  }

  // Waterlogging
  override fun getFluidState(state: BlockState) = fluidState(state)
  override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState,
                                         world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
    neighborUpdate(state, world,  pos)
    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
  }

  override fun appendTooltip(stack: ItemStack, world: BlockView?, tooltip: MutableList<Text>, options: TooltipContext) {
    // Add the NBT data prior to the description lines
    val nbt = BlockItem.getBlockEntityNbt(stack)
    if (nbt != null) {
      if (nbt.contains("frequency", COMPOUND)) {
        val frequency = Frequency.fromNbt(nbt.getCompound("frequency"))

        // Frequency: White, White, White
        tooltip.add(frequency.toText())

        // Public, or Owner: <name>
        if (frequency.personal) {
          tooltip.add(translatable("$translationKey.owner_name", frequency.ownerName ?: "Unknown"))
        } else {
          tooltip.add(translatable("$translationKey.public"))
        }
      }
    }

    // Description lines
    addDescLines(tooltip, translationKey, extraLines = tooltipExtra)
  }

  data class HitShape(val type: HitShapeType, val buttonId: Int? = null) {
    enum class HitShapeType {
      LATCH,
      BUTTON
    }
  }

  companion object {
    val facing: DirectionProperty = HorizontalFacingBlock.FACING

    private val chestShape = createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)
    val latchBoxes = Direction.values().associateWith {
      Box(7.0, 7.0, 15.0, 9.0, 11.0, 16.0).rotateTowards(it.opposite) // Opposite to match the renderer
    }
    val buttonBoxes = (0 until 3).associateWith { i ->
      Direction.values().associateWith {
        val x = 4.0 + (i * 3.0)
        Box(x, 14.0, 6.0, x + 2.0, 15.0, 10.0).rotateTowards(it.opposite)
      }
    }

    private val shapes = Direction.values().associateWith {
      val subShapes = mutableListOf<VoxelShape>()
      subShapes.add(latchBoxes[it]!!.toDiv16VoxelShape())
      (0 until 3)
        .map { i -> buttonBoxes[i]!![it]!!.toDiv16VoxelShape() }
        .toCollection(subShapes)

      VoxelShapes.union(chestShape, *subShapes.toTypedArray())
    }

    private val computercraftLoaded by lazy { FabricLoader.getInstance().isModLoaded("computercraft") }
  }
}
