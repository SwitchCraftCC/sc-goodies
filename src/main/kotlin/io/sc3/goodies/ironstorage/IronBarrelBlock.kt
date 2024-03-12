package io.sc3.goodies.ironstorage

import io.sc3.goodies.ScGoodies
import io.sc3.goodies.util.BaseBlockWithEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.world.ServerWorld
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.BlockView
import net.minecraft.world.World

class IronBarrelBlock(
  settings: Settings,
  val variant: IronStorageVariant
) : BaseBlockWithEntity(settings) {
  init {
    defaultState = stateManager.defaultState
      .with(facing, Direction.NORTH)
      .with(open, false)
  }

  override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
    builder.add(facing, open)
  }

  override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState
    .with(facing, ctx.playerLookDirection.opposite)

  override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
    if (stack.hasCustomName()) {
      val be = world.getBlockEntity(pos) as? IronBarrelBlockEntity ?: return
      be.customName = stack.name
    }
  }

  override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
    IronBarrelBlockEntity(variant, pos, state)

  override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState,
                               moved: Boolean) {
    if (state.isOf(newState.block)) return

    val be = world.getBlockEntity(pos) as? IronBarrelBlockEntity ?: return
    ItemScatterer.spawn(world, pos, be)
    world.updateComparators(pos, this)

    super.onStateReplaced(state, world, pos, newState, moved)
  }

  override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
    IronBarrelBlockEntity.scheduledTick(world, pos)
  }

  override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand,
                     hit: BlockHitResult): ActionResult {
    if (world.isClient) return ActionResult.SUCCESS

    val factory = createScreenHandlerFactory(state, world, pos) ?: return ActionResult.CONSUME
    player.openHandledScreen(factory)
    player.incrementStat(Stats.OPEN_BARREL)
    return ActionResult.CONSUME
  }

  override fun createScreenHandlerFactory(state: BlockState, world: World,
                                          pos: BlockPos): NamedScreenHandlerFactory? {
    val be = world.getBlockEntity(pos) as? IronBarrelBlockEntity ?: return null
    val name = be.name
    return SimpleNamedScreenHandlerFactory({ syncId, playerInv, _ ->
      IronChestScreenHandler(variant, syncId, playerInv, be, variant.chestScreenHandlerType)
    }, name)
  }

  override fun getRenderType(state: BlockState) = BlockRenderType.MODEL

  override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
    state.rotate(mirror.getRotation(state.get(facing)))

  override fun rotate(state: BlockState, rotation: BlockRotation): BlockState =
    state.with(facing, rotation.rotate(state.get(facing)))

  override fun hasComparatorOutput(state: BlockState) = true

  override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
    val be = world.getBlockEntity(pos) ?: return 0
    return ScreenHandler.calculateComparatorOutput(be)
  }

  override fun appendTooltip(stack: ItemStack, world: BlockView?, tooltip: MutableList<Text>, options: TooltipContext) {
    // Don't call super, we don't want the default .desc implementation
    tooltip.add(
      Text.translatable("block.${ScGoodies.modId}.storage.desc", variant.size)
      .formatted(Formatting.GRAY))
  }

  companion object {
    val facing: DirectionProperty = Properties.FACING
    val open: BooleanProperty = Properties.OPEN
  }
}
