package pw.switchcraft.goodies.datagen.recipes

import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.goodies.datagen.recipes.ingredients.ElytraIngredient
import pw.switchcraft.goodies.elytra.DyedElytraItem.Companion.dyedElytraItems

class DragonScaleRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.MISC
) : ShapelessRecipe(
  id, "dragonScaleElytra", category,
  ItemStack(dyedElytraItems[DyeColor.BLACK]!!), DefaultedList.copyOf(
    Ingredient.EMPTY, // Defaulted item
    ofItems(ModItems.dragonScale),
    ElytraIngredient()
  )
) {
  private val elytraIngredient = ElytraIngredient()

  // TODO: is default matches() behaviour sufficient?
  override fun craft(craftingInventory: CraftingInventory) = output
  override fun getOutput(): ItemStack = ItemStack(dyedElytraItems[DyeColor.BLACK]!!)

  override fun getRemainder(inv: CraftingInventory): DefaultedList<ItemStack> {
    val remainder = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY)

    for (i in 0 until remainder.size) {
      val stack = inv.getStack(i)
      if (elytraIngredient.test(stack)) {
        remainder[i] = stack.copy()
      }
    }

    return remainder
  }

  override fun isIgnoredInRecipeBook() = true
  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer(::DragonScaleRecipe)
  }
}
