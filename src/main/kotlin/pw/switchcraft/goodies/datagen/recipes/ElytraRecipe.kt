package pw.switchcraft.goodies.datagen.recipes

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dan200.computercraft.shared.util.RecipeUtil
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.PacketByteBuf.getMaxValidator
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.collection.DefaultedList
import pw.switchcraft.goodies.datagen.recipes.ingredients.ElytraIngredient

class ElytraRecipe(
  id: Identifier,
  val outputStack: ItemStack,
  val dyes: List<DyeColor>
) : ShapelessRecipe(
  id, "dyedElytra", outputStack, DefaultedList.copyOf(
    Ingredient.EMPTY, // Defaulted item
    ElytraIngredient(),
    *dyes.map { ofItems(DyeItem.byColor(it)) }.toTypedArray()
  )
) {
  private val elytraIngredient = ElytraIngredient()

  override fun craft(inv: CraftingInventory): ItemStack {
    for (i in 0 until inv.size()) {
      val stack = inv.getStack(i)
      if (elytraIngredient.test(stack)) {
        // Copy the NBT from the old elytra, this will copy damage, custom name, and enchantments
        val out = output.copy()
        out.nbt = stack.nbt?.copy()
        return out
      }
    }

    return ItemStack.EMPTY
  }

  override fun getOutput(): ItemStack = outputStack

  override fun isIgnoredInRecipeBook() = false
  override fun getSerializer() = ElytraRecipeSerializer
}

object ElytraRecipeSerializer : RecipeSerializer<ElytraRecipe> {
  override fun read(id: Identifier, json: JsonObject): ElytraRecipe {
    val dyes = readDyes(JsonHelper.getArray(json, "dyes"))

    val outputObject = JsonHelper.getObject(json, "result")
    val output = ShapedRecipe.outputFromJson(outputObject)
    RecipeUtil.setNbt(output, outputObject)

    return ElytraRecipe(id, output, dyes)
  }

  private fun readDyes(arrays: JsonArray): List<DyeColor> {
    val out = mutableListOf<DyeColor>()
    for (i in 0 until arrays.size()) {
      val color = DyeColor.byName(arrays[i].asString, DyeColor.WHITE)
      out.add(color!!)
    }
    return out
  }

  override fun read(id: Identifier, buf: PacketByteBuf): ElytraRecipe {
    val dyes = buf.readCollection(getMaxValidator({ mutableListOf() }, 9)) {
      it.readEnumConstant(DyeColor::class.java)
    }
    val output = buf.readItemStack()
    return ElytraRecipe(id, output, dyes)
  }

  override fun write(buf: PacketByteBuf, recipe: ElytraRecipe) {
    buf.writeCollection(recipe.dyes) { b, color -> b.writeEnumConstant(color) }
    buf.writeItemStack(recipe.outputStack)
  }
}
