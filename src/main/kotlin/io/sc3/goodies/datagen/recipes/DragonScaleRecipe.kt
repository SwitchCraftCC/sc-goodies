package io.sc3.goodies.datagen.recipes

import com.google.gson.JsonObject
import io.sc3.goodies.ScGoodiesItemTags
import io.sc3.library.recipe.ShapelessRecipeSpec
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

class DragonScaleRecipe(
  id: Identifier,
  group: String,
  category: CraftingRecipeCategory,
  outputStack: ItemStack,
  input: DefaultedList<Ingredient>,
) : ShapelessRecipe(id, group, category, outputStack, input) {
  override fun getRemainder(inv: RecipeInputInventory): DefaultedList<ItemStack> {
    val remainder = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY)

    for (i in 0 until remainder.size) {
      val stack = inv.getStack(i)
      if (stack.isIn(ScGoodiesItemTags.ELYTRA)) {
        remainder[i] = stack.copy()
      }
    }

    return remainder
  }

  override fun isIgnoredInRecipeBook() = true
  override fun getSerializer() = DragonScaleRecipeSerializer
}

object DragonScaleRecipeSerializer : RecipeSerializer<DragonScaleRecipe> {
  private fun make(id: Identifier, spec: ShapelessRecipeSpec) = DragonScaleRecipe(
    id, spec.group, spec.category, spec.output, spec.input
  )

  override fun read(id: Identifier, json: JsonObject) = make(id, ShapelessRecipeSpec.ofJson(json))
  override fun read(id: Identifier, buf: PacketByteBuf) = make(id, ShapelessRecipeSpec.ofPacket(buf))
  override fun write(buf: PacketByteBuf, recipe: DragonScaleRecipe) = ShapelessRecipeSpec.ofRecipe(recipe).write(buf)
}
