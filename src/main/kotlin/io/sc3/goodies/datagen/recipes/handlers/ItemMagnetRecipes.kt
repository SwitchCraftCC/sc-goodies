package io.sc3.goodies.datagen.recipes.handlers

import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.*
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries.RECIPE_SERIALIZER
import net.minecraft.registry.Registry.register
import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.datagen.recipes.ItemMagnetUpgradeRecipe
import io.sc3.library.recipe.BetterComplexRecipeJsonBuilder
import io.sc3.library.recipe.RecipeHandler
import java.util.function.Consumer

object ItemMagnetRecipes : RecipeHandler {
  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, ModId("item_magnet_upgrade"), ItemMagnetUpgradeRecipe.recipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Item Magnet
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.TOOLS, ModItems.itemMagnet)
      .pattern("I I")
      .pattern("R L")
      .pattern("RNL")
      .input('I', IRON_BLOCK)
      .input('R', REDSTONE_BLOCK)
      .input('L', LAPIS_BLOCK)
      .input('N', NETHER_STAR)
      .criterion("has_nether_star", conditionsFromItem(NETHER_STAR))
      .offerTo(exporter)

    // Item Magnet Upgrades
    BetterComplexRecipeJsonBuilder(ModItems.itemMagnet, ItemMagnetUpgradeRecipe.recipeSerializer)
      .criterion("has_item_magnet", conditionsFromItem(ModItems.itemMagnet))
      .offerTo(exporter, ModId("item_magnet_upgrade"))
  }
}
