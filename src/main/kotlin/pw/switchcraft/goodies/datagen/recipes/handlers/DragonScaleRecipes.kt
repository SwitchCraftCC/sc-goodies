package pw.switchcraft.goodies.datagen.recipes.handlers

import net.minecraft.data.server.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry.RECIPE_SERIALIZER
import net.minecraft.util.registry.Registry.register
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.datagen.recipes.DragonScaleRecipe
import pw.switchcraft.library.recipe.BetterComplexRecipeJsonBuilder
import pw.switchcraft.library.recipe.RecipeHandler
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
