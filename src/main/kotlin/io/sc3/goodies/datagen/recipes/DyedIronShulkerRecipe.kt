package io.sc3.goodies.datagen.recipes

import io.sc3.goodies.ScGoodiesItemTags
import io.sc3.goodies.datagen.recipes.IronShulkerRecipe.Companion.shulkerItem
import io.sc3.goodies.ironshulker.IronShulkerItem
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.DYES
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient.fromTag
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.world.World

class DyedIronShulkerRecipe(id: Identifier, category: CraftingRecipeCategory) : SpecialCraftingRecipe(id, category) {
  private val ironShulker = fromTag(ScGoodiesItemTags.ANY_IRON_SHULKER_BOX)
  private val dye = fromTag(DYES)

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
    val variant = (shulkerStack.item as IronShulkerItem).block.variant
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
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer(::DyedIronShulkerRecipe)
  }
}
