package pw.switchcraft.goodies.datagen.recipes

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.inventory.Inventory
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Consumer

class RecipeGenerator(generator: FabricDataGenerator) : FabricRecipeProvider(generator) {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    IronChestRecipes.generateRecipes(exporter)
    IronChestUpgradeRecipes.generateRecipes(exporter)
    IronShulkerRecipes.generateRecipes(exporter)
    IronShulkerUpgradeRecipes.generateRecipes(exporter)
  }

  companion object {
    fun <T : Recipe<C>, C : Inventory> specialRecipe(
      exporter: Consumer<RecipeJsonProvider>,
      serializer: SpecialRecipeSerializer<T>
    ): Identifier {
      val recipeId = Registry.RECIPE_SERIALIZER.getId(serializer)
        ?: throw IllegalStateException("Recipe serializer $serializer is not registered")
      ComplexRecipeJsonBuilder.create(serializer).offerTo(exporter, recipeId.toString())
      return recipeId
    }
  }
}
