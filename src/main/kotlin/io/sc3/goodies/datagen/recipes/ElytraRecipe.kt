package io.sc3.goodies.datagen.recipes

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.DyeItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.StringNbtReader
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.PacketByteBuf.getMaxValidator
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.recipe.book.CraftingRecipeCategory.MISC
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.collection.DefaultedList
import io.sc3.goodies.datagen.recipes.ingredients.ElytraIngredient

class ElytraRecipe(
  id: Identifier,
  category: CraftingRecipeCategory = CraftingRecipeCategory.EQUIPMENT,
  val outputStack: ItemStack,
  val dyes: List<DyeColor>
) : ShapelessRecipe(
  id, "dyedElytra", category, outputStack, DefaultedList.copyOf(
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
  private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

  override fun read(id: Identifier, json: JsonObject): ElytraRecipe {
    val category = CraftingRecipeCategory.CODEC.byId(JsonHelper.getString(json, "category", null), MISC)

    val dyes = readDyes(JsonHelper.getArray(json, "dyes"))

    val outputObject = JsonHelper.getObject(json, "result")
    val output = ShapedRecipe.outputFromJson(outputObject)

    outputObject.get("nbt")?.let {
      try {
        val nbtJson = if (it.isJsonObject) GSON.toJson(it) else JsonHelper.asString(it, "nbt")
        output.nbt = StringNbtReader.parse(nbtJson)
      } catch (e: CommandSyntaxException) {
        throw RuntimeException("Invalid NBT entry: ", e)
      }
    }

    return ElytraRecipe(id, category, output, dyes)
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
    val category = buf.readEnumConstant(CraftingRecipeCategory::class.java)
    val dyes = buf.readCollection(getMaxValidator({ mutableListOf() }, 9)) {
      it.readEnumConstant(DyeColor::class.java)
    }
    val output = buf.readItemStack()
    return ElytraRecipe(id, category, output, dyes)
  }

  override fun write(buf: PacketByteBuf, recipe: ElytraRecipe) {
    buf.writeEnumConstant(recipe.category)
    buf.writeCollection(recipe.dyes) { b, color -> b.writeEnumConstant(color) }
    buf.writeItemStack(recipe.outputStack)
  }
}
