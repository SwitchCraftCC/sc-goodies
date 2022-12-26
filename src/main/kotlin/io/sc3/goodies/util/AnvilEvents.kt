package io.sc3.goodies.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.Property
import io.sc3.library.ext.event

object AnvilEvents {
  @JvmField
  val CHANGE = event<(handler: AnvilScreenHandler, left: ItemStack, right: ItemStack, output: CraftingResultInventory,
                      name: String, baseCost: Int, player: PlayerEntity, levelCost: Property) -> Boolean> { cb ->
    { handler, left, right, output, name, baseCost, player, levelCost ->
      cb.all { it(handler, left, right, output, name, baseCost, player, levelCost) }
    }
  }
}
