package pw.switchcraft.goodies.datagen.recipes.handlers

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.CHEST
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.ironchest.IronChestVariant
import pw.switchcraft.library.recipe.RecipeHandler
import java.util.function.Consumer

object IronChestRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Iron Chest
    ShapedRecipeJsonBuilder
      .create(IronChestVariant.IRON.chestBlock)
      .pattern("III")
      .pattern("ICI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('C', CHEST) // TODO: Figure out if the CHESTS conventional tag only counts for wood chests
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Gold Chest
    ShapedRecipeJsonBuilder
      .create(IronChestVariant.GOLD.chestBlock)
      .pattern("III")
      .pattern("ICI")
      .pattern("III")
      .input('I', GOLD_INGOTS)
      .input('C', IronChestVariant.IRON.chestBlock)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Diamond Chest (with Iron Chest)
    ShapedRecipeJsonBuilder
      .create(IronChestVariant.DIAMOND.chestBlock)
      .pattern("GGG")
      .pattern("GCG")
      .pattern("DDD")
      .input('G', GLASS_BLOCKS)
      .input('D', DIAMONDS)
      .input('C', IronChestVariant.IRON.chestBlock)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter, ModId("diamond_chest_with_iron_chest"))

    // Diamond Chest (with Gold Chest)
    ShapedRecipeJsonBuilder
      .create(IronChestVariant.DIAMOND.chestBlock)
      .pattern("GGG")
      .pattern("DCD")
      .pattern("GGG")
      .input('G', GLASS_BLOCKS)
      .input('D', DIAMONDS)
      .input('C', IronChestVariant.GOLD.chestBlock)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter, ModId("diamond_chest_with_gold_chest"))
  }
}
