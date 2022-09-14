package pw.switchcraft.goodies.datagen.recipes.handlers

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.RecipeProvider.conditionsFromTag
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.SHULKER_SHELL
import pw.switchcraft.goodies.ironchest.IronChestUpgrade
import pw.switchcraft.library.recipe.RecipeHandler
import java.util.function.Consumer

object IronShulkerUpgradeRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Vanilla to Iron Shulker Upgrade
    ShapedRecipeJsonBuilder
      .create(IronChestUpgrade.VANILLA_IRON.shulkerUpgrade)
      .pattern("III")
      .pattern("ISI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('S', SHULKER_SHELL)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter)

    // Iron to Gold Shulker Upgrade
    ShapedRecipeJsonBuilder
      .create(IronChestUpgrade.IRON_GOLD.shulkerUpgrade)
      .pattern("GIG")
      .pattern("GGG")
      .pattern("GGG")
      .input('G', GOLD_INGOTS)
      .input('I', IRON_INGOTS)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter)

    // Gold to Diamond Shulker Upgrade
    ShapedRecipeJsonBuilder
      .create(IronChestUpgrade.GOLD_DIAMOND.shulkerUpgrade)
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
