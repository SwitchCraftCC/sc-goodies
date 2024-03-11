package io.sc3.goodies.misc

import net.minecraft.entity.LivingEntity
import net.minecraft.item.FoodComponent
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import io.sc3.goodies.util.BaseItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items

class IceCreamItem(settings: Settings) : BaseItem(settings) {
  override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
    // Return Bowl
    if (user is PlayerEntity && !user.isCreative) {
      user.inventory.offerOrDrop(ItemStack(Items.BOWL))
    }
    return super.finishUsing(stack, world, user)
  }

  override fun usageTick(world: World?, user: LivingEntity?, stack: ItemStack?, remainingUseTicks: Int) {
    // if player is eating while full hunger, start doing brainfreeze!
    if (user is PlayerEntity && !user.isCreative && !user.hungerManager.isNotFull) {
      user.frozenTicks += 3
    }
    super.usageTick(world, user, stack, remainingUseTicks)
  }

  companion object {
    val foodComponent: FoodComponent = FoodComponent.Builder()
      .hunger(4)
      .saturationModifier(5.0f)
      .alwaysEdible()
      .build()
  }
}
