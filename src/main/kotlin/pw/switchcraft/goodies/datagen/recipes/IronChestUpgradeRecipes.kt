package pw.switchcraft.goodies.datagen.recipes

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.CHEST
import net.minecraft.tag.ItemTags.PLANKS
import pw.switchcraft.goodies.ironchest.IronChestUpgrade
import java.util.function.Consumer

object IronChestUpgradeRecipes {
  internal fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Wood to Iron Chest Upgrade
    ShapedRecipeJsonBuilder
      .create(IronChestUpgrade.VANILLA_IRON.chestUpgrade)
      .pattern("III")
      .pattern("IPI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('P', PLANKS)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Iron to Gold Chest Upgrade
    ShapedRecipeJsonBuilder
      .create(IronChestUpgrade.IRON_GOLD.chestUpgrade)
      .pattern("GGG")
      .pattern("GIG")
      .pattern("GGG")
      .input('G', GOLD_INGOTS)
      .input('I', IRON_INGOTS)
      .criterion("has_chest", conditionsFromItem(CHEST))
      .offerTo(exporter)

    // Gold to Diamond Chest Upgrade
    ShapedRecipeJsonBuilder
      .create(IronChestUpgrade.GOLD_DIAMOND.chestUpgrade)
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
