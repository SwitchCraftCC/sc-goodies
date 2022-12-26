package io.sc3.goodies.datagen.recipes

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.DYES
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient.fromTag
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.world.World
import io.sc3.goodies.datagen.recipes.ingredients.IronShulkerIngredient
import io.sc3.goodies.ironchest.IronChestVariant.IRON
import io.sc3.goodies.ironshulker.IronShulkerItem

private val ironShulker = IronShulkerIngredient()
private val dye = fromTag(DYES)

class DyedIronShulkerRecipe(
  id: Identifier,
  category: CraftingRecipeCategory
) : BaseIronShulkerRecipe(
  id, category,
  ItemStack(IRON.shulkerBlock),
  listOf(ironShulker, dye)
) {
  override fun matches(inv: CraftingInventory, world: World): Boolean {
    var hasShulker = false
    var hasDye = false

    for (i in 0 until inv.size()) {
      val stack = inv.getStack(i)
      if (stack.isEmpty) continue

      when {
        ironShulker.test(stack) -> {
          if (hasShulker) return false
          hasShulker = true
        }
        dye.test(stack) -> {
          if (hasDye) return false
          hasDye = true
        }
        else -> return false
      }
    }

    return hasShulker && hasDye
  }

  override fun craft(inventory: CraftingInventory): ItemStack {
    val shulkerStack = shulkerItem(inventory)
    // No shulker found - disallow craft
    if (shulkerStack.isEmpty) return ItemStack.EMPTY

    val color = dyeItem(inventory) ?: return ItemStack.EMPTY
    val variant = (outputItem.item as IronShulkerItem).block.variant
    val resultBlock = variant.dyedShulkerBlocks[color]

    val result = ItemStack(resultBlock)
    result.nbt = shulkerStack.nbt?.copy()
    return result
  }

  private fun dyeItem(inv: CraftingInventory): DyeColor? {
    for (i in 0 until inv.size()) {
      val stack = inv.getStack(i)
      if (stack.isEmpty) continue

      val item = stack.item
      if (item is DyeItem) return item.color
    }

    return null
  }

  override fun fits(w: Int, h: Int) = w * h >= 2
  override fun isIgnoredInRecipeBook() = true
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer(::DyedIronShulkerRecipe)
  }
}
