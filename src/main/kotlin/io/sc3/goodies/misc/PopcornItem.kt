package io.sc3.goodies.misc

import net.minecraft.entity.LivingEntity
import net.minecraft.item.FoodComponent
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import io.sc3.goodies.util.BaseItem

class PopcornItem(settings: Settings) : BaseItem(settings) {
  override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
    // Don't consume the item when finished eating.
    return stack
  }

  companion object {
    val foodComponent: FoodComponent = FoodComponent.Builder()
      .hunger(0)
      .saturationModifier(0.0f)
      .alwaysEdible()
      .snack()
      .build()
  }
}
