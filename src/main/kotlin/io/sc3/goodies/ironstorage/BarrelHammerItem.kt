package io.sc3.goodies.ironstorage

import io.sc3.goodies.util.BaseItem
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.ToolMaterials
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult.PASS

class BarrelHammerItem(settings: Settings) : BaseItem(settings) {
  override fun useOnBlock(ctx: ItemUsageContext): ActionResult {
    val world = ctx.world
    val pos = ctx.blockPos

    val be = world.getBlockEntity(pos) as? LootableContainerBlockEntity ?: return PASS

    val oldState = world.getBlockState(pos)
    val oldBlock = oldState.block
    val oldType = getUpgradeType(oldBlock) ?: return PASS
    val newBlock = getNewBlock(oldBlock) ?: return PASS

    if (!world.isClient) {
      // Don't upgrade if the chest is opened by any player
      if (oldType.getViewers(be) > 0) return PASS

      // Create the new inventory
      val newState = newBlock.getPlacementState(ItemPlacementContext(ctx)) ?: return PASS
      IronStorageUpgrade.convertContainerBlock(world, pos, be, newState)
    }

    // Damage the hammer
    val stack = ctx.stack
    val player = ctx.player

    if (player is ServerPlayerEntity) {
      Criteria.ITEM_USED_ON_BLOCK.trigger(player, pos, stack)
    }

    if (player != null) {
      world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.65f, 0.8f)
      stack.damage(1, player) { p -> p.sendToolBreakStatus(ctx.hand) }
    }

    return ActionResult.success(world.isClient)
  }

  private fun getNewBlock(oldBlock: Block): Block? = when(oldBlock) {
    // Chest -> Barrel
    Blocks.CHEST -> Blocks.BARREL
    is IronChestBlock -> oldBlock.variant.barrelBlock

    // Barrel -> Chest
    Blocks.BARREL -> Blocks.CHEST
    is IronBarrelBlock -> oldBlock.variant.chestBlock

    else -> null
  }

  private fun getUpgradeType(block: Block): IronStorageUpgradeType? = when (block) {
    Blocks.CHEST, is IronChestBlock -> IronChestUpgradeType
    Blocks.BARREL, is IronBarrelBlock -> IronBarrelUpgradeType
    else -> null
  }

  override fun canRepair(stack: ItemStack, ingredient: ItemStack) =
    ToolMaterials.IRON.repairIngredient.test(ingredient) || super.canRepair(stack, ingredient)
}
