package io.sc3.goodies.ironstorage

import io.sc3.goodies.ScGoodies.modId
import io.sc3.goodies.util.BaseBlockWithEntity
import io.sc3.library.WaterloggableBlock
import io.sc3.library.WaterloggableBlock.Companion.waterlogged
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.server.world.ServerWorld
import net.minecraft.stat.Stat
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class IronChestBlock(
  settings: Settings,
  val variant: IronStorageVariant
) : BaseBlockWithEntity(settings), WaterloggableBlock {
  private val openStat: Stat<Identifier> by lazy {
    Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST)
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
    .with(facing, ctx.horizontalPlayerFacing.opposite)
    .with(waterlogged, placementWaterlogged(ctx))

  override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
    if (stack.hasCustomName()) {
      val be = world.getBlockEntity(pos) as? IronChestBlockEntity ?: return
      be.customName = stack.name
    }
  }

  override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
    IronChestBlockEntity(variant, pos, state)

  override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState,
                               moved: Boolean) {
    if (state.isOf(newState.block)) return

    val be = world.getBlockEntity(pos) as? IronChestBlockEntity ?: return
    ItemScatterer.spawn(world, pos, be)
    world.updateComparators(pos, this)

    super.onStateReplaced(state, world, pos, newState, moved)
  }

  override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand,
                     hit: BlockHitResult): ActionResult {
    if (world.isClient) return ActionResult.SUCCESS

    val factory = createScreenHandlerFactory(state, world, pos) ?: return ActionResult.CONSUME
    player.openHandledScreen(factory)
    player.incrementStat(openStat)
    return ActionResult.CONSUME
  }

  override fun createScreenHandlerFactory(state: BlockState, world: World,
                                          pos: BlockPos): NamedScreenHandlerFactory? {
    val be = world.getBlockEntity(pos) as? IronChestBlockEntity ?: return null
    val name = be.name
    return SimpleNamedScreenHandlerFactory({ syncId, playerInv, _ ->
      IronChestScreenHandler(variant, syncId, playerInv, be, variant.chestScreenHandlerType)
    }, name)
  }

  override fun <T : BlockEntity> getTicker(
    world: World,
    state: BlockState,
    type: BlockEntityType<T>
  ): BlockEntityTicker<T>? {
    if (!world.isClient) return null
    return checkType(type, variant.chestBlockEntityType, IronChestBlockEntity.Companion::clientTick)
  }

  override fun hasComparatorOutput(state: BlockState) = true

  override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
    val be = world.getBlockEntity(pos) ?: return 0
    return ScreenHandler.calculateComparatorOutput(be)
  }

  override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, ctx: ShapeContext): VoxelShape = 
    shape

  override fun getRenderType(state: BlockState) = BlockRenderType.ENTITYBLOCK_ANIMATED

  override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
    state.rotate(mirror.getRotation(state.get(facing)))

  override fun rotate(state: BlockState, rotation: BlockRotation): BlockState =
    state.with(facing, rotation.rotate(state.get(facing)))

  override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType) =
    false

  override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos, random: Random) {
    IronChestBlockEntity.scheduledTick(world, pos)
  }

  // Waterlogging
  override fun getFluidState(state: BlockState) = fluidState(state)
  override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, neighborState: BlockState,
                                         world: WorldAccess, pos: BlockPos, neighborPos: BlockPos): BlockState {
    neighborUpdate(state, world,  pos)
    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
  }

  override fun appendTooltip(stack: ItemStack, world: BlockView?, tooltip: MutableList<Text>, options: TooltipContext) {
    // Don't call super, we don't want the default .desc implementation
    tooltip.add(translatable("block.$modId.storage.desc", variant.size)
      .formatted(Formatting.GRAY))
  }

  companion object {
    val facing: DirectionProperty = HorizontalFacingBlock.FACING

    private val shape = createCuboidShape(1.0, 0.0, 1.0, 15.0, 14.0, 15.0)
  }
}
