package io.sc3.goodies.hoverboots

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketItem
import dev.emi.trinkets.api.TrinketsApi
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.world.World
import io.sc3.goodies.ScGoodies.modId
import io.sc3.library.Tooltips.addDescLines

class HoverBootsItem(
  val color: DyeColor,
  settings: Settings
) : TrinketItem(settings) {
  override fun getTranslationKey() = "item.$modId.hover_boots"

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    super.appendTooltip(stack, world, tooltip, context)
    addDescLines(tooltip, getTranslationKey(stack))
  }

  override fun tick(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
    if (!entity.isOnGround && entity.fallDistance < 5.0f && entity.velocity.y < 0.0f) {
      entity.velocity = entity.velocity.multiply(1.0, 0.9, 1.0)
      entity.velocityDirty = true
    }
  }

  override fun onEquip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
    super.onEquip(stack, slot, entity)
    entity.stepHeight = 1.0f
  }

  override fun onUnequip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
    super.onUnequip(stack, slot, entity)
    entity.stepHeight = 0.5f
  }

  companion object {
    private fun hoverBootsEquipped(entity: LivingEntity): Boolean {
      val component = TrinketsApi.getTrinketComponent(entity).orElse(null) ?: return false
      return component.isEquipped { it.item is HoverBootsItem }
    }

    @JvmStatic
    fun onLivingJump(entity: LivingEntity) {
      if (!hoverBootsEquipped(entity)) return

      if (entity.isSprinting) {
        entity.addVelocity(entity.velocity.x * 0.5, 0.4, entity.velocity.z * 0.5)
      } else {
        entity.addVelocity(0.0, 0.4, 0.0)
      }
    }

    @JvmStatic
    fun modifyFallDistance(entity: LivingEntity, fallDistance: Float): Float {
      if (fallDistance < 3 || !hoverBootsEquipped(entity)) return fallDistance
      return fallDistance * 0.3f
    }
  }
}
