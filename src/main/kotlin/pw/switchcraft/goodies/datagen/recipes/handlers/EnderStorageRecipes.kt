package pw.switchcraft.goodies.datagen.recipes.handlers

import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.*
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.library.recipe.RecipeHandler
import java.util.function.Consumer

object EnderStorageRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Ender Storage
    ShapedRecipeJsonBuilder
      .create(ModItems.enderStorage)
      .pattern("BWB")
      .pattern("OCO")
      .pattern("BEB")
      .input('B', BLAZE_ROD)
      .input('W', WHITE_WOOL) // Specifically white, no others
      .input('O', OBSIDIAN)
      .input('C', CHEST)
      .input('E', ENDER_PEARL)
      .criterion("has_chest", RecipeProvider.conditionsFromItem(CHEST))
      .offerTo(exporter)
  }
}
