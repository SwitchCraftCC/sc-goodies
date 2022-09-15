package pw.switchcraft.goodies.util

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.world.World
import pw.switchcraft.library.Tooltips.addDescLines

open class BaseItem(
  settings: Settings
) : Item(settings) {
  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    super.appendTooltip(stack, world, tooltip, context)
    addDescLines(tooltip, getTranslationKey(stack))
  }
}
