package io.sc3.goodies.ironstorage

import io.sc3.goodies.ScGoodies
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.util.BaseBlockWithEntity
import net.fabricmc.fabric.api.util.NbtType.LIST
import net.fabricmc.fabric.api.util.NbtType.STRING
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ShulkerBoxBlockEntity.AnimationStage.CLOSED
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.mob.ShulkerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters.BLOCK_ENTITY
import net.minecraft.screen.ScreenHandler
import net.minecraft.stat.Stat
import net.minecraft.stat.Stats
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.*
import net.minecraft.util.Formatting.ITALIC
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class IronShulkerBlock(
  settings: Settings,
  val variant: IronStorageVariant,
  val color: DyeColor? = null
) : BaseBlockWithEntity(settings) {
  private val openStat: Stat<Identifier> by lazy {
    Stats.CUSTOM.getOrCreateStat(Stats.OPEN_SHULKER_BOX)
  }

  init {
    defaultState = stateManager.defaultState
      .with(facing, Direction.NORTH)
  }

  override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
    builder.add(facing)
  }

  override fun getPlacementState(ctx: ItemPlacementContext): BlockState = defaultState
    .with(facing, ctx.side)

  override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
    IronShulkerBlockEntity(variant, pos, state)

  override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
    if (stack.hasCustomName()) {
      val be = world.getBlockEntity(pos) as? IronShulkerBlockEntity ?: return
      be.customName = stack.name
    }
  }

  override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState,
                               moved: Boolean) {
    if (state.isOf(newState.block)) return

    if (world.getBlockEntity(pos) is IronShulkerBlockEntity) {
      world.updateComparators(pos, this)
    }

    super.onStateReplaced(state, world, pos, newState, moved)
  }

  override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
    val be = world.getBlockEntity(pos) as? IronShulkerBlockEntity ?: run {
      super.onBreak(world, pos, state, player)
      return
    }

    if (!world.isClient && player.isCreative && !be.isEmpty) {
      val stack = getItemStack(variant, color)
      be.setStackNbt(stack)
      if (be.hasCustomName()) stack.setCustomName(be.customName)

      val itemEntity = ItemEntity(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, stack)
      itemEntity.setToDefaultPickupDelay()
      world.spawnEntity(itemEntity)
    } else {
      be.checkLootInteraction(player)
    }

    super.onBreak(world, pos, state, player)
  }

  override fun getDroppedStacks(state: BlockState, builder: LootContextParameterSet.Builder): MutableList<ItemStack> {
    val be = builder.getOptional(BLOCK_ENTITY) as? IronShulkerBlockEntity ?:
      return super.getDroppedStacks(state, builder)

    builder.addDynamicDrop(contents) { consumer ->
      for (i in 0 until be.size()) {
        consumer.accept(be.getStack(i))
      }
    }

    return super.getDroppedStacks(state, builder)
  }

  override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState): ItemStack {
    val stack = super.getPickStack(world, pos, state)
    world.getBlockEntity(pos, variant.shulkerBlockEntityType).ifPresent { it.setStackNbt(stack) }
    return stack
  }

  override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand,
                     hit: BlockHitResult): ActionResult {
    // The early-return behaviors here are slightly different to IronChestBlock, but this attempts to be closer to the
    // way vanilla shulker boxes behave, rather than consistent with the rest of this codebase.
    if (world.isClient) return ActionResult.SUCCESS
    if (player.isSpectator) return ActionResult.CONSUME

    val be = world.getBlockEntity(pos) as? IronShulkerBlockEntity ?: return ActionResult.PASS
    if (!canOpen(state, world, pos, be)) return ActionResult.CONSUME

    player.openHandledScreen(be)
    player.incrementStat(openStat)
    return ActionResult.CONSUME
  }

  override fun <T : BlockEntity> getTicker(
    world: World,
    state: BlockState,
    type: BlockEntityType<T>
  ): BlockEntityTicker<T>? {
    return checkType(type, variant.shulkerBlockEntityType, IronShulkerBlockEntity.Companion::tick)
  }

  override fun hasComparatorOutput(state: BlockState) = true

  override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos): Int {
    val be = world.getBlockEntity(pos) ?: return 0
    return ScreenHandler.calculateComparatorOutput(be)
  }

  override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, ctx: ShapeContext): VoxelShape {
    val be = world.getBlockEntity(pos) as? IronShulkerBlockEntity ?: return VoxelShapes.fullCube()
    return VoxelShapes.cuboid(be.getBoundingBox(state))
  }

  override fun getRenderType(state: BlockState) = BlockRenderType.ENTITYBLOCK_ANIMATED

  override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
    state.rotate(mirror.getRotation(state.get(facing)))

  override fun rotate(state: BlockState, rotation: BlockRotation): BlockState =
    state.with(facing, rotation.rotate(state.get(facing)))

  override fun appendTooltip(stack: ItemStack, world: BlockView?, tooltip: MutableList<Text>, options: TooltipContext) {
    // Don't call super, we don't want the default .desc implementation
    tooltip.add(translatable("block.${ScGoodies.modId}.storage.desc", variant.size)
      .formatted(Formatting.GRAY))

    val nbt = BlockItem.getBlockEntityNbt(stack) ?: return

    if (nbt.contains("LootTable", STRING)) {
      tooltip.add(Text.literal("???????"))
    }

    if (nbt.contains("Items", LIST)) {
      val inv = DefaultedList.ofSize(this.variant.size, ItemStack.EMPTY)
      Inventories.readNbt(nbt, inv)

      var shownStacks = 0
      var stacks = 0

      for (itemStack in inv) {
        if (!itemStack.isEmpty) {
          stacks++

          if (shownStacks <= 4) {
            shownStacks++

            val text = itemStack.name.copy()
            text.append(" x").append(itemStack.count.toString())
            tooltip.add(text)
          }
        }
      }

      if (stacks - shownStacks > 0) {
        tooltip.add(translatable("container.shulkerBox.more", stacks - shownStacks).formatted(ITALIC))
      }
    }
  }

  companion object {
    val facing: DirectionProperty = FacingBlock.FACING
    val contents = ModId("contents")

    private fun canOpen(state: BlockState, world: World, pos: BlockPos, be: IronShulkerBlockEntity): Boolean =
      if (be.animationStage != CLOSED) {
        true
      } else {
        val box = ShulkerEntity.calculateBoundingBox(state.get(facing), 0.0f, 0.5f).offset(pos).contract(1.0e-6)
        world.isSpaceEmpty(box)
      }

    fun get(variant: IronStorageVariant, color: DyeColor? = null) = when(color) {
      null -> variant.shulkerBlock
      else -> variant.dyedShulkerBlocks[color]
    }

    fun getItemStack(variant: IronStorageVariant, color: DyeColor? = null) =
      ItemStack(get(variant, color))

    fun getColor(block: Block) = (block as? IronShulkerBlock)?.color
  }
}
