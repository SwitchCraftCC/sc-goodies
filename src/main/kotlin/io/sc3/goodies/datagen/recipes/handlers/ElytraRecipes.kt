package io.sc3.goodies.datagen.recipes.handlers

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.CriterionMerger
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.book.CraftingRecipeCategory.EQUIPMENT
import net.minecraft.registry.Registries
import net.minecraft.registry.Registries.RECIPE_SERIALIZER
import net.minecraft.registry.Registry.register
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.datagen.recipes.ElytraRecipe
import io.sc3.goodies.datagen.recipes.ElytraRecipeSerializer
import io.sc3.goodies.elytra.DyedElytraItem
import io.sc3.goodies.elytra.SpecialElytraType
import io.sc3.library.recipe.RecipeHandler
import java.util.function.Consumer

object ElytraRecipes : RecipeHandler {
  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, ModId("elytra"), ElytraRecipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Dyed Elytra
    DyeColor.values().forEach { color ->
      val elytra = DyedElytraItem.dyedElytraItems[color]!!
      val id = itemId(elytra)
      ElytraRecipeJsonBuilder(ElytraRecipe(id, EQUIPMENT, ItemStack(elytra), listOf(color)))
        .offerTo(exporter)
    }

    // Special Elytra
    SpecialElytraType.values().forEach { type ->
      val elytra = type.item
      val id = itemId(elytra)
      ElytraRecipeJsonBuilder(ElytraRecipe(id, EQUIPMENT, ItemStack(elytra), type.recipeColors))
        .offerTo(exporter)
    }
  }
}

class ElytraRecipeJsonBuilder(val recipe: ElytraRecipe) {
  private val outputItem = recipe.outputStack.item
  private val outputId by lazy { itemId(outputItem) }

  fun offerTo(exporter: Consumer<RecipeJsonProvider>, recipeId: Identifier = outputId) {
    val advancementId = recipeId.withPrefixedPath("recipes/" + recipe.category.asString() + "/")
    val advancement = Advancement.Builder.create()
      .criterion("has_elytra", conditionsFromItem(Items.ELYTRA))
      .parent(Identifier("recipes/root"))
      .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
      .rewards(AdvancementRewards.Builder.recipe(recipeId))
      .criteriaMerger(CriterionMerger.OR)
      .toJson()

    exporter.accept(object : RecipeJsonProvider {
      override fun serialize(json: JsonObject) {
        json.addProperty("category", recipe.category.asString())

        if (recipe.group.isNotEmpty()) {
          json.addProperty("group", recipe.group)
        }

        val dyesArray = JsonArray()
        recipe.dyes.forEach { dye -> dyesArray.add(dye.getName()) }
        json.add("dyes", dyesArray)

        val outputObject = JsonObject()
        outputObject.addProperty("item", outputId.toString())
        json.add("result", outputObject)
      }

      override fun getRecipeId() = recipeId
      override fun getSerializer() = ElytraRecipeSerializer
      override fun toAdvancementJson() = advancement
      override fun getAdvancementId() = advancementId
    })
  }
}

private fun itemId(item: ItemConvertible) = Registries.ITEM.getId(item.asItem())
