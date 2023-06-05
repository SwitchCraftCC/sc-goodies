package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.misc.AmethystExtras
import io.sc3.library.recipe.RecipeHandler
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import java.util.function.Consumer

object AmethystRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Amethyst Slabs and Stairs
    RecipeProvider.createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, AmethystExtras.slabBlock, Ingredient.ofItems(AmethystExtras.baseBlock))
      .criterion("items", RecipeProvider.conditionsFromItem(AmethystExtras.baseBlock))
      .offerTo(exporter)

    RecipeProvider.createStairsRecipe(AmethystExtras.stairsBlock, Ingredient.ofItems(AmethystExtras.baseBlock))
      .criterion("items", RecipeProvider.conditionsFromItem(AmethystExtras.baseBlock))
      .offerTo(exporter)

    SingleItemRecipeJsonBuilder.createStonecutting(
      Ingredient.ofItems(AmethystExtras.baseBlock),
      RecipeCategory.BUILDING_BLOCKS,
      AmethystExtras.slabBlock,
      2
    )
      .criterion("items", RecipeProvider.conditionsFromItem(AmethystExtras.baseBlock))
      .offerTo(exporter, "amethyst_slab_stonecutter")

    SingleItemRecipeJsonBuilder.createStonecutting(
      Ingredient.ofItems(AmethystExtras.baseBlock),
      RecipeCategory.BUILDING_BLOCKS,
      AmethystExtras.stairsBlock,
      1
    )
      .criterion("items", RecipeProvider.conditionsFromItem(AmethystExtras.baseBlock))
      .offerTo(exporter, "amethyst_stairs_stonecutter")
  }
}
