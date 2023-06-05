package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.ScGoodiesItemTags.CONCRETE
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.*
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.book.RecipeCategory
import io.sc3.goodies.misc.ConcreteExtras
import io.sc3.library.recipe.RecipeHandler
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder
import java.util.function.Consumer

object ConcreteRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Concrete Slabs and Stairs
    ConcreteExtras.colors.values.forEach {
      createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, it.slabBlock, ofItems(it.baseBlock))
        .group("concrete_slabs")
        .criterion("has_concrete", conditionsFromTag(CONCRETE))
        .offerTo(exporter)

      createStairsRecipe(it.stairsBlock, ofItems(it.baseBlock))
        .group("concrete_stairs")
        .criterion("has_concrete", conditionsFromTag(CONCRETE))
        .offerTo(exporter)

      SingleItemRecipeJsonBuilder.createStonecutting(
        ofItems(it.baseBlock),
        RecipeCategory.BUILDING_BLOCKS,
        it.slabBlock,
        2
      )
        .criterion("has_concrete", conditionsFromTag(CONCRETE))
        .offerTo(exporter, it.slabBlockId.path + "_stonecutter")

      SingleItemRecipeJsonBuilder.createStonecutting(
        ofItems(it.baseBlock),
        RecipeCategory.BUILDING_BLOCKS,
        it.stairsBlock,
        1
      )
        .criterion("has_concrete", conditionsFromTag(CONCRETE))
        .offerTo(exporter, it.stairsBlockId.path + "_stonecutter")
    }
  }
}
