package pw.switchcraft.goodies.datagen.recipes.handlers

import net.minecraft.data.server.recipe.RecipeJsonProvider
import java.util.function.Consumer

val RECIPE_HANDLERS by lazy { listOf(
  IronChestRecipes,
  IronChestUpgradeRecipes,
  IronShulkerRecipes,
  IronShulkerUpgradeRecipes,
  EnderStorageRecipes,
  ItemMagnetRecipes
)}

interface RecipeHandler {
  fun registerSerializers() {}
  fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {}
}
