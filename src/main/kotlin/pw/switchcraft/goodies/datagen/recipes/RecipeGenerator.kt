package pw.switchcraft.goodies.datagen.recipes

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.RecipeJsonProvider
import pw.switchcraft.goodies.datagen.recipes.handlers.RECIPE_HANDLERS
import java.util.function.Consumer

class RecipeGenerator(generator: FabricDataGenerator) : FabricRecipeProvider(generator) {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    RECIPE_HANDLERS.forEach { it.generateRecipes(exporter) }
  }
}
