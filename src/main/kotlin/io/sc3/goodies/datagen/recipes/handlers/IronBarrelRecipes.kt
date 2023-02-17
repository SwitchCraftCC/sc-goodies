package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ironstorage.IronStorageVariant
import io.sc3.library.recipe.RecipeHandler
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.BARREL
import net.minecraft.recipe.book.RecipeCategory
import java.util.function.Consumer

object IronBarrelRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Iron Barrel
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageVariant.IRON.barrelBlock)
      .pattern("III")
      .pattern("IBI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('B', BARREL)
      .criterion("has_barrel", conditionsFromItem(BARREL))
      .offerTo(exporter)

    // Gold Barrel
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageVariant.GOLD.barrelBlock)
      .pattern("III")
      .pattern("IBI")
      .pattern("III")
      .input('I', GOLD_INGOTS)
      .input('B', IronStorageVariant.IRON.barrelBlock)
      .criterion("has_barrel", conditionsFromItem(BARREL))
      .offerTo(exporter)

    // Diamond Barrel (with Iron Barrel)
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageVariant.DIAMOND.barrelBlock)
      .pattern("GGG")
      .pattern("GBG")
      .pattern("DDD")
      .input('G', GLASS_BLOCKS)
      .input('D', DIAMONDS)
      .input('B', IronStorageVariant.IRON.barrelBlock)
      .criterion("has_barrel", conditionsFromItem(BARREL))
      .offerTo(exporter, ModId("diamond_barrel_with_iron_barrel"))

    // Diamond Barrel (with Gold Barrel)
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageVariant.DIAMOND.barrelBlock)
      .pattern("GGG")
      .pattern("DBD")
      .pattern("GGG")
      .input('G', GLASS_BLOCKS)
      .input('D', DIAMONDS)
      .input('B', IronStorageVariant.GOLD.barrelBlock)
      .criterion("has_barrel", conditionsFromItem(BARREL))
      .offerTo(exporter, ModId("diamond_barrel_with_gold_barrel"))
  }
}
