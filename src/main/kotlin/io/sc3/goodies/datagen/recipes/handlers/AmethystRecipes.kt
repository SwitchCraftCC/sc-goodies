package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.misc.AmethystExtras
import io.sc3.library.recipe.RecipeHandler
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.*
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import java.util.function.Consumer

object AmethystRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Amethyst Slabs and Stairs
    createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, AmethystExtras.slabBlock, Ingredient.ofItems(AmethystExtras.baseBlock))
      .criterion("items", conditionsFromItem(AmethystExtras.baseBlock))
      .offerTo(exporter)

    createStairsRecipe(AmethystExtras.stairsBlock, Ingredient.ofItems(AmethystExtras.baseBlock))
      .criterion("items", conditionsFromItem(AmethystExtras.baseBlock))
      .offerTo(exporter)

    offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, AmethystExtras.slabBlock, AmethystExtras.baseBlock, 2)
    offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, AmethystExtras.stairsBlock, AmethystExtras.baseBlock, 1)
  }
}
