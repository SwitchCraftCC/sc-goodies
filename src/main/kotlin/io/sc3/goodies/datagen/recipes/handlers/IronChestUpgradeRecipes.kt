package io.sc3.goodies.datagen.recipes.handlers

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.CHEST
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.tag.ItemTags.PLANKS
import io.sc3.goodies.ironstorage.IronStorageUpgrade
import io.sc3.library.recipe.RecipeHandler
import java.util.function.Consumer

object IronChestUpgradeRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Wood to Iron Chest Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageUpgrade.VANILLA_IRON.chestUpgrade)
      .pattern("III")
      .pattern("IPI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('P', PLANKS)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Iron to Gold Chest Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageUpgrade.IRON_GOLD.chestUpgrade)
      .pattern("GGG")
      .pattern("GIG")
      .pattern("GGG")
      .input('G', GOLD_INGOTS)
      .input('I', IRON_INGOTS)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Iron to Diamond Chest Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageUpgrade.IRON_DIAMOND.chestUpgrade)
      .pattern("LLL")
      .pattern("LIL")
      .pattern("DDD")
      .input('L', GLASS_BLOCKS)
      .input('I', IRON_INGOTS)
      .input('D', DIAMONDS)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Gold to Diamond Chest Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageUpgrade.GOLD_DIAMOND.chestUpgrade)
      .pattern("LLL")
      .pattern("DGD")
      .pattern("LLL")
      .input('L', GLASS_BLOCKS)
      .input('G', GOLD_INGOTS)
      .input('D', DIAMONDS)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)
  }
}
