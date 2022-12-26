package io.sc3.goodies.ironchest

import net.minecraft.block.ChestBlock
import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.block.entity.ShulkerBoxBlockEntity
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult.PASS
import net.minecraft.util.ActionResult.SUCCESS
import net.minecraft.util.DyeColor
import net.minecraft.util.Formatting.GRAY
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import io.sc3.goodies.ScGoodies.modId
import io.sc3.goodies.ironshulker.IronShulkerBlock
import io.sc3.goodies.ironshulker.IronShulkerBlockEntity
import io.sc3.goodies.mixin.ShulkerBoxBlockEntityAccessor
import io.sc3.goodies.util.BaseItem
import io.sc3.library.Tooltips.addDescLines
import io.sc3.library.WaterloggableBlock

class IronChestUpgradeItem(
  private val upgrade: IronChestUpgrade,
  private val shulker: Boolean,
  settings: Settings
) : BaseItem(settings) {
  private val from by upgrade::from
  private val to by upgrade::to

  private val tooltipExtra = listOf(translatable("block.$modId.storage.desc", to.size)
    .formatted(GRAY))

  /**
   * NB: World.canPlayerModifyAt and Player.isCreative checks are not necessary here as they are both done by
   * ServerPlayerInteractionManager.interactBlock, but if this is switched to another use method, then they must be
   * added accordingly.
   */
  override fun useOnBlock(ctx: ItemUsageContext): ActionResult {
    val world = ctx.world
    if (world.isClient) return PASS

    val pos = ctx.blockPos
    val be = world.getBlockEntity(pos) as? LootableContainerBlockEntity ?: return PASS

    val state = world.getBlockState(pos)
    val viewers: Int
    val facing: Direction
    var waterlogged = false
    var color: DyeColor? = null
    val customName = be.customName

    when {
      // Chests
      // Only allow when this is a vanilla -> iron upgrade
      !shulker && be is ChestBlockEntity && from == null -> {
        viewers = ChestBlockEntity.getPlayersLookingInChestCount(world, pos)
        facing = state.get(ChestBlock.FACING)
        waterlogged = state.get(ChestBlock.WATERLOGGED)
      }
      !shulker && be is IronChestBlockEntity && from != null -> {
        // Only allow when this is the correct iron+ upgrade
        val ironChestBlock = state.block as? IronChestBlock
        val variant = ironChestBlock?.variant
        if (variant != from) return PASS

        viewers = be.stateManager.viewerCount
        facing = state.get(IronChestBlock.facing)
        waterlogged = state.get(WaterloggableBlock.waterlogged)
      }

      // Shulkers
      // Only allow when this is a vanilla -> iron upgrade
      shulker && be is ShulkerBoxBlockEntity && from == null -> {
        val shulkerBlock = state.block as? ShulkerBoxBlock ?: return PASS

        viewers = (be as ShulkerBoxBlockEntityAccessor).viewerCount
        facing = state.get(ShulkerBoxBlock.FACING)
        color = shulkerBlock.color
      }
      shulker && be is IronShulkerBlockEntity && from != null -> {
        // Only allow when this is the correct iron+ upgrade
        val ironShulkerBlock = state.block as? IronShulkerBlock
        val variant = ironShulkerBlock?.variant
        if (ironShulkerBlock == null || variant != from) return PASS

        viewers = be.viewerCount
        facing = state.get(IronShulkerBlock.facing)
        color = ironShulkerBlock.color
      }

      else -> return PASS
    }

    // Don't upgrade if the chest is opened by any player
    if (viewers > 0) return PASS

    // Copy the items from the old inventory
    val size = be.size()
    val contents = DefaultedList.ofSize(size, ItemStack.EMPTY)
    for (i in 0 until size) {
      contents[i] = be.getStack(i)
    }

    // Remove the old inventory
    world.removeBlockEntity(pos)
    world.removeBlock(pos, false)

    // Create the new inventory
    val newBlock = if (shulker) {
      if (color != null) to.dyedShulkerBlocks[color]!!
      else to.shulkerBlock
    } else {
      to.chestBlock
    }

    val newState = if (shulker)
      newBlock.defaultState
        .with(IronShulkerBlock.facing, facing)
    else {
      newBlock.defaultState
        .with(IronChestBlock.facing, facing)
        .with(WaterloggableBlock.waterlogged, waterlogged)
    }

    world.setBlockState(pos, newState)

    val newBe = world.getBlockEntity(pos) as LootableContainerBlockEntity
    newBe.customName = customName

    // Copy the items to the new inventory
    for (i in 0 until size) {
      newBe.setStack(i, contents[i])
    }

    // Destroy the upgrade item
    ctx.stack.decrement(1)
    return SUCCESS
  }

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    // Don't call super, we don't want the default .desc implementation
    addDescLines(tooltip, getTranslationKey(stack), extraLines = tooltipExtra)
  }
}
