package io.sc3.goodies.datagen.recipes.handlers

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromTag
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.SHULKER_SHELL
import net.minecraft.recipe.book.RecipeCategory
import io.sc3.goodies.ironchest.IronChestUpgrade
import io.sc3.library.recipe.RecipeHandler
import net.minecraft.data.server.recipe.RecipeProvider
import net.minecraft.item.Items
import java.util.function.Consumer

object IronShulkerUpgradeRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Vanilla to Iron Shulker Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronChestUpgrade.VANILLA_IRON.shulkerUpgrade)
      .pattern("III")
      .pattern("ISI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('S', SHULKER_SHELL)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter)

    // Iron to Gold Shulker Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronChestUpgrade.IRON_GOLD.shulkerUpgrade)
      .pattern("GIG")
      .pattern("GGG")
      .pattern("GGG")
      .input('G', GOLD_INGOTS)
      .input('I', IRON_INGOTS)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter)

    // Iron to Diamond Shulker Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronChestUpgrade.IRON_DIAMOND.shulkerUpgrade)
      .pattern("LIL")
      .pattern("LLL")
      .pattern("DDD")
      .input('L', GLASS_BLOCKS)
      .input('I', IRON_INGOTS)
      .input('D', DIAMONDS)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter)

    // Gold to Diamond Shulker Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronChestUpgrade.GOLD_DIAMOND.shulkerUpgrade)
      .pattern("LDL")
      .pattern("LGL")
      .pattern("LDL")
      .input('L', GLASS_BLOCKS)
      .input('G', GOLD_INGOTS)
      .input('D', DIAMONDS)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter)
  }
}
