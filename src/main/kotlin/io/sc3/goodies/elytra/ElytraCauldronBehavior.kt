package io.sc3.goodies.elytra

import net.minecraft.block.LeveledCauldronBlock
import net.minecraft.block.cauldron.CauldronBehavior
import net.minecraft.block.cauldron.CauldronBehavior.WATER_CAULDRON_BEHAVIOR
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.stat.Stats
import net.minecraft.util.ActionResult

object ElytraCauldronBehavior {
  private val cleanElytra = CauldronBehavior { state, world, pos, player, hand, stack ->
    if (!world.isClient) {
      val resultStack = ItemStack(Items.ELYTRA)
      resultStack.nbt = stack.nbt?.copy() // Will copy the damage across

      player.setStackInHand(hand, resultStack)
      player.incrementStat(Stats.CLEAN_ARMOR)
      LeveledCauldronBlock.decrementFluidLevel(state, world, pos)
    }

    ActionResult.success(world.isClient)
  }

  internal fun registerBehavior() {
    (DyedElytraItem.dyedElytraItems.values + SpecialElytraType.values().map { it.item }).forEach {
      WATER_CAULDRON_BEHAVIOR[it] = cleanElytra
    }
  }
}
