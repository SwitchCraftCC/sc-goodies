package io.sc3.goodies.datagen.recipes.handlers

import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.item.Items
import net.minecraft.registry.Registries.RECIPE_SERIALIZER
import net.minecraft.registry.Registry.register
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.datagen.recipes.DragonScaleRecipe
import io.sc3.library.recipe.BetterComplexRecipeJsonBuilder
import io.sc3.library.recipe.RecipeHandler
import java.util.function.Consumer

object DragonScaleRecipes : RecipeHandler {
  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, ModId("dragon_scale_elytra"), DragonScaleRecipe.recipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    BetterComplexRecipeJsonBuilder(Items.ELYTRA, DragonScaleRecipe.recipeSerializer)
      .criterion("has_elytra", conditionsFromItem(Items.ELYTRA))
      .offerTo(exporter, ModId("dragon_scale_elytra"))
  }
}
