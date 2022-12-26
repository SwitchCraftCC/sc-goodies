package io.sc3.goodies.datagen.recipes

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.RecipeJsonProvider
import io.sc3.goodies.datagen.recipes.handlers.RECIPE_HANDLERS
import java.util.function.Consumer

class RecipeGenerator(out: FabricDataOutput) : FabricRecipeProvider(out) {
  override fun generate(exporter: Consumer<RecipeJsonProvider>) {
    RECIPE_HANDLERS.forEach { it.generateRecipes(exporter) }
  }
}
