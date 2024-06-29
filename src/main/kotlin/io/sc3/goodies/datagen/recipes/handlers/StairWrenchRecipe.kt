package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.Registration
import io.sc3.library.recipe.RecipeHandler
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.IRON_INGOTS
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromTag
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.tag.ItemTags
import net.minecraft.registry.tag.ItemTags.STAIRS
import java.util.function.Consumer

object StairWrenchRecipe: RecipeHandler { // Having a single file for a recipe feels like a sin
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Stair Wrench
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.TOOLS, Registration.ModItems.stairWrench)
      .pattern(" IS")
      .pattern(" II")
      .pattern("I  ")
      .input('I', IRON_INGOTS)
      .input('S', ItemTags.WOODEN_STAIRS)
      .criterion("has_stairs", conditionsFromTag(STAIRS))
      .offerTo(exporter)
  }
}
