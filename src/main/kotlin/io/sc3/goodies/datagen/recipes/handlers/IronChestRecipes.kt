package io.sc3.goodies.datagen.recipes.handlers

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.CHEST
import net.minecraft.recipe.book.RecipeCategory
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ironstorage.IronStorageVariant
import io.sc3.library.recipe.RecipeHandler
import java.util.function.Consumer

object IronChestRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Iron Chest
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageVariant.IRON.chestBlock)
      .pattern("III")
      .pattern("ICI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('C', CHEST) // TODO: Figure out if the CHESTS conventional tag only counts for wood chests
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Gold Chest
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageVariant.GOLD.chestBlock)
      .pattern("III")
      .pattern("ICI")
      .pattern("III")
      .input('I', GOLD_INGOTS)
      .input('C', IronStorageVariant.IRON.chestBlock)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Diamond Chest (with Iron Chest)
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageVariant.DIAMOND.chestBlock)
      .pattern("GGG")
      .pattern("GCG")
      .pattern("DDD")
      .input('G', GLASS_BLOCKS)
      .input('D', DIAMONDS)
      .input('C', IronStorageVariant.IRON.chestBlock)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter, ModId("diamond_chest_with_iron_chest"))

    // Diamond Chest (with Gold Chest)
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageVariant.DIAMOND.chestBlock)
      .pattern("GGG")
      .pattern("DCD")
      .pattern("GGG")
      .input('G', GLASS_BLOCKS)
      .input('D', DIAMONDS)
      .input('C', IronStorageVariant.GOLD.chestBlock)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter, ModId("diamond_chest_with_gold_chest"))
  }
}
