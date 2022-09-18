package pw.switchcraft.goodies.datagen.recipes

import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.goodies.itemmagnet.ItemMagnetItem

class ItemMagnetUpgradeRecipe(id: Identifier) : ShapelessRecipe(
  id, "itemMagnetUpgrade", ItemStack(ModItems.itemMagnet), DefaultedList.copyOf(
    Ingredient.EMPTY, // Defaulted item
    ofItems(ModItems.itemMagnet),
    ofItems(Items.NETHER_STAR),
    ofItems(Items.NETHERITE_INGOT)
  )
) {
  override fun craft(inv: CraftingInventory): ItemStack {
    val output = output
    for (i in 0 until inv.size()) {
      val stack: ItemStack = inv.getStack(i)
      if (stack.item !is ItemMagnetItem || stack.item !== output.item) {
        continue
      }

      val result = stack.copy()
      result.count = 1

      // Only increment the level if the magnet is not already at the max - i.e. only if the radius is now different
      val oldLevel = ItemMagnetItem.stackLevel(stack)
      val oldRadius = ItemMagnetItem.stackRadius(stack)
      val newRadius = ItemMagnetItem.radius(oldLevel + 1)
      if (oldRadius == newRadius) return ItemStack.EMPTY

      // Increment the level by updating the NBT of the result item
      val tag = result.orCreateNbt
      tag.putInt("level", oldLevel + 1)
      return result
    }

    return output.copy()
  }

  override fun isIgnoredInRecipeBook() = true
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer { ItemMagnetUpgradeRecipe(it) }
  }
}
