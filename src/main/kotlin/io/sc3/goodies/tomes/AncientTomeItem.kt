package io.sc3.goodies.tomes

import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper.fromNbt
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting.GRAY
import net.minecraft.world.World
import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.util.BaseItem
import net.minecraft.text.Text.translatable as trans

class AncientTomeItem(settings: Settings) : BaseItem(settings) {
  override fun isEnchantable(stack: ItemStack) = false
  override fun hasGlint(stack: ItemStack) = true

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    val ench = stackEnchantment(stack)
    if (ench != null) {
      val lvl = ench.maxLevel + 1
      val text = trans("$translationKey.level_tooltip", trans(ench.translationKey), trans("enchantment.level.$lvl"))
        .formatted(GRAY)
      tooltip.add(text)
    }

    super.appendTooltip(stack, world, tooltip, context)
  }

  companion object {
    fun stackEnchantment(stack: ItemStack) =
      EnchantedBookItem.getEnchantmentNbt(stack)?.let { fromNbt(it) }?.keys?.firstOrNull()

    fun getTomeStacks(): List<ItemStack> =
      TomeEnchantments.validEnchantments.map { ench -> ItemStack(ModItems.ancientTome)
        .also { EnchantedBookItem.addEnchantment(it, EnchantmentLevelEntry(ench, ench.maxLevel)) } }
  }
}
