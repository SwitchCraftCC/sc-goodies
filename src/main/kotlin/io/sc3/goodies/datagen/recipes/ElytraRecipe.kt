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
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

class ElytraRecipe(
  id: Identifier,
  group: String,
  category: CraftingRecipeCategory,
  outputStack: ItemStack,
  val input: DefaultedList<Ingredient>
) : ShapelessRecipe(id, group, category, outputStack, input) {
  override fun craft(inv: RecipeInputInventory, manager: DynamicRegistryManager): ItemStack {
    val output = getOutput(manager)

    for (i in 0 until inv.size()) {
      val stack = inv.getStack(i)
      if (stack.isIn(ScGoodiesItemTags.ELYTRA)) {
        // Copy the NBT from the old elytra, this will copy damage, custom name, and enchantments
        val out = output.copy()
        out.nbt = stack.nbt?.copy()
        return out
      }
    }

    return ItemStack.EMPTY
  }

  override fun getSerializer() = ElytraRecipeSerializer
}

object ElytraRecipeSerializer : RecipeSerializer<ElytraRecipe> {
  private fun make(id: Identifier, spec: ShapelessRecipeSpec) = ElytraRecipe(
    id, spec.group, spec.category, spec.output, spec.input
  )

  override fun read(id: Identifier, json: JsonObject) = make(id, ShapelessRecipeSpec.ofJson(json))
  override fun read(id: Identifier, buf: PacketByteBuf) = make(id, ShapelessRecipeSpec.ofPacket(buf))
  override fun write(buf: PacketByteBuf, recipe: ElytraRecipe) = ShapelessRecipeSpec.ofRecipe(recipe).write(buf)
}
