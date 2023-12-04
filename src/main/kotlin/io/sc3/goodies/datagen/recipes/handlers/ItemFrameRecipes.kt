package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.Registration.ModItems.glassItemFrame
import io.sc3.goodies.Registration.ModItems.glowGlassItemFrame
import io.sc3.library.recipe.RecipeHandler
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.Items.*
import net.minecraft.recipe.book.RecipeCategory
import java.util.function.Consumer

object ItemFrameRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Glass Item Frame
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, glassItemFrame)
      .pattern("GGG")
      .pattern("GIG")
      .pattern("GGG")
      .input('G', ConventionalItemTags.GLASS_PANES)
      .input('I', ITEM_FRAME)
      .criterion("has_leather", conditionsFromItem(LEATHER))
      .offerTo(exporter)

    // Glow Glass Item Frame
    ShapelessRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, glowGlassItemFrame)
      .input(glassItemFrame)
      .input(GLOW_INK_SAC)
      .criterion("has_item_frame", conditionsFromItem(ITEM_FRAME))
      .criterion("has_glow_ink_sac", conditionsFromItem(GLOW_INK_SAC))
      .offerTo(exporter)

    // Glow Glass Item Frame Alternate
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.DECORATIONS, glowGlassItemFrame)
      .pattern("GGG")
      .pattern("GIG")
      .pattern("GGG")
      .input('G', ConventionalItemTags.GLASS_PANES)
      .input('I', GLOW_ITEM_FRAME)
      .criterion("has_glow_item_frame", conditionsFromItem(GLOW_ITEM_FRAME))
      .offerTo(exporter, "glow_glass_item_frame_alternate")
  }
}
