package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.Registration
import io.sc3.goodies.ScGoodiesItemTags.ANY_IRON_STORAGE
import io.sc3.goodies.ScGoodiesItemTags.ANY_UPGRADABLE_STORAGE
import io.sc3.goodies.ironstorage.IronStorageUpgrade
import io.sc3.library.recipe.RecipeHandler
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromTag
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.tag.ItemTags.PLANKS
import net.minecraft.registry.tag.ItemTags.WOODEN_SLABS
import java.util.function.Consumer

object IronStorageUpgradeRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Wood to Iron Storage Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageUpgrade.VANILLA_IRON.upgradeItem)
      .pattern("III")
      .pattern("IPI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('P', PLANKS)
      .criterion("has_chest", conditionsFromTag(ANY_UPGRADABLE_STORAGE))
      .criterion("has_iron_chest", conditionsFromTag(ANY_IRON_STORAGE))
      .offerTo(exporter)

    // Iron to Gold Storage Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageUpgrade.IRON_GOLD.upgradeItem)
      .pattern("GGG")
      .pattern("GIG")
      .pattern("GGG")
      .input('G', GOLD_INGOTS)
      .input('I', IRON_INGOTS)
      .criterion("has_chest", conditionsFromTag(ANY_UPGRADABLE_STORAGE))
      .criterion("has_iron_chest", conditionsFromTag(ANY_IRON_STORAGE))
      .offerTo(exporter)

    // Iron to Diamond Storage Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageUpgrade.IRON_DIAMOND.upgradeItem)
      .pattern("LLL")
      .pattern("LIL")
      .pattern("DDD")
      .input('L', GLASS_BLOCKS)
      .input('I', IRON_INGOTS)
      .input('D', DIAMONDS)
      .criterion("has_chest", conditionsFromTag(ANY_UPGRADABLE_STORAGE))
      .criterion("has_iron_chest", conditionsFromTag(ANY_IRON_STORAGE))
      .offerTo(exporter)

    // Gold to Diamond Storage Upgrade
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, IronStorageUpgrade.GOLD_DIAMOND.upgradeItem)
      .pattern("LLL")
      .pattern("DGD")
      .pattern("LLL")
      .input('L', GLASS_BLOCKS)
      .input('G', GOLD_INGOTS)
      .input('D', DIAMONDS)
      .criterion("has_chest", conditionsFromTag(ANY_UPGRADABLE_STORAGE))
      .criterion("has_iron_chest", conditionsFromTag(ANY_IRON_STORAGE))
      .offerTo(exporter)

    // Barrel Hammer
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.TOOLS, Registration.ModItems.barrelHammer)
      .pattern("SSS")
      .pattern(" I ")
      .pattern(" I ")
      .input('I', IRON_INGOTS)
      .input('S', WOODEN_SLABS)
      .criterion("has_chest", conditionsFromTag(ANY_UPGRADABLE_STORAGE))
      .criterion("has_iron_chest", conditionsFromTag(ANY_IRON_STORAGE))
      .offerTo(exporter)
  }
}
