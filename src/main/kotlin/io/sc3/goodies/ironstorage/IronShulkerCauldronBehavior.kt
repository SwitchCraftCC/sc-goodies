package io.sc3.goodies.ironstorage

import net.minecraft.block.Block
import net.minecraft.block.LeveledCauldronBlock
import net.minecraft.block.cauldron.CauldronBehavior
import net.minecraft.block.cauldron.CauldronBehavior.WATER_CAULDRON_BEHAVIOR
import net.minecraft.item.ItemStack
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult

object IronShulkerCauldronBehavior {
  private val cleanShulker = CauldronBehavior { state, world, pos, player, hand, stack ->
    val block = Block.getBlockFromItem(stack.item) as? IronShulkerBlock
      ?: return@CauldronBehavior ActionResult.PASS

    if (!world.isClient) {
      val resultStack = ItemStack(block.variant.shulkerBlock)
      resultStack.nbt = stack.nbt?.copy()

      player.setStackInHand(hand, resultStack)
      player.incrementStat(Stats.CLEAN_SHULKER_BOX)
      LeveledCauldronBlock.decrementFluidLevel(state, world, pos)
    }

    ActionResult.success(world.isClient)
  }

  internal fun registerBehavior() {
    IronStorageVariant.values().forEach { variant ->
      variant.dyedShulkerItems.values.forEach {
        WATER_CAULDRON_BEHAVIOR[it] = cleanShulker
      }
    }
  }
}
