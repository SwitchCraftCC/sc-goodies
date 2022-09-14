package pw.switchcraft.goodies.datagen.recipes.handlers

import net.minecraft.data.server.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.*
import net.minecraft.util.registry.Registry.RECIPE_SERIALIZER
import net.minecraft.util.registry.Registry.register
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.datagen.recipes.ItemMagnetUpgradeRecipe
import pw.switchcraft.library.recipe.BetterComplexRecipeJsonBuilder
import pw.switchcraft.library.recipe.RecipeHandler
import java.util.function.Consumer

object ItemMagnetRecipes : RecipeHandler {
  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, ModId("item_magnet_upgrade"), ItemMagnetUpgradeRecipe.recipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Item Magnet
    ShapedRecipeJsonBuilder
      .create(ModItems.itemMagnet)
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
