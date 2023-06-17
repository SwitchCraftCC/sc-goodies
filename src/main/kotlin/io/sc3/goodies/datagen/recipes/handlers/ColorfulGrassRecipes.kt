package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.nature.ScTree
import io.sc3.library.recipe.RecipeHandler
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory.DECORATIONS
import java.util.function.Consumer

object ColorfulGrassRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    with (exporter) {
      addGrassRecipe(ModBlocks.sakuraSapling, ModItems.pinkGrass)
      addGrassRecipe(ModBlocks.mapleSapling, ModItems.autumnGrass)
      addGrassRecipe(ModBlocks.blueSapling, ModItems.blueGrass)
    }
  }

  private fun Consumer<RecipeJsonProvider>.addGrassRecipe(tree: ScTree, grass: Item) {
    ShapelessRecipeJsonBuilder
      .create(DECORATIONS, grass)
      .input(tree.leaves)
      .input(Items.GRASS_BLOCK)
      .criterion("has_grass_block", conditionsFromItem(Items.GRASS_BLOCK))
      .criterion("has_sapling", conditionsFromItem(tree.saplingItem))
      .offerTo(this)
  }
}
