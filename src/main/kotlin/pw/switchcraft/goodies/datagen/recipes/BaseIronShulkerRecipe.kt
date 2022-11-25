package pw.switchcraft.goodies.datagen.recipes

import net.minecraft.block.Block
import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import pw.switchcraft.goodies.ironshulker.IronShulkerBlock
import pw.switchcraft.goodies.ironshulker.IronShulkerItem
import pw.switchcraft.library.recipe.BetterSpecialRecipe

abstract class BaseIronShulkerRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.MISC,
  override val outputItem: ItemStack,
  override val ingredients: List<Ingredient>
) : BetterSpecialRecipe(id) {
  override fun craft(inventory: CraftingInventory): ItemStack {
    val shulkerStack = shulkerItem(inventory)
    // No shulker found - disallow craft
    if (shulkerStack.isEmpty) return ItemStack.EMPTY

    val color = shulkerColor(shulkerStack)
    val variant = (outputItem.item as IronShulkerItem).block.variant
    val resultBlock = if (color != null) variant.dyedShulkerBlocks[color] else variant.shulkerBlock

    val result = ItemStack(resultBlock)
    result.nbt = shulkerStack.nbt?.copy()
    return result
  }

  companion object {
    private fun isShulkerItem(stack: ItemStack): Boolean =
      Block.getBlockFromItem(stack.item) is ShulkerBoxBlock || stack.item is IronShulkerItem

    fun shulkerColor(stack: ItemStack): DyeColor? =
      when (val block = Block.getBlockFromItem(stack.item)) {
        is ShulkerBoxBlock -> { block.color }
        is IronShulkerBlock -> { block.color }
        else -> null
      }

    fun shulkerItem(inv: CraftingInventory): ItemStack {
      var shulkerStack = ItemStack.EMPTY

      for (i in 0 until inv.size()) {
        val stack = inv.getStack(i)
        if (isShulkerItem(stack)) {
          // Crafting with two shulkers (should never happen) - disallow craft
          if (!shulkerStack.isEmpty) return ItemStack.EMPTY
          shulkerStack = stack
        }
      }

      return shulkerStack
    }
  }
}
