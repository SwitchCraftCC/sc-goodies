package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ScGoodiesItemTags
import io.sc3.goodies.datagen.recipes.DyedIronShulkerRecipe
import io.sc3.goodies.datagen.recipes.IronShulkerRecipeSerializer
import io.sc3.library.recipe.RecipeHandler
import io.sc3.library.recipe.offerTo
import io.sc3.library.recipe.specialRecipe
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromTag
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Items.DIAMOND
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries.RECIPE_SERIALIZER
import net.minecraft.registry.Registry.register
import java.util.function.Consumer
import io.sc3.goodies.ironstorage.IronStorageVariant.DIAMOND as DIAMOND_VARIANT
import io.sc3.goodies.ironstorage.IronStorageVariant.GOLD as GOLD_VARIANT
import io.sc3.goodies.ironstorage.IronStorageVariant.IRON as IRON_VARIANT

object IronShulkerRecipes : RecipeHandler {
  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, ModId("iron_shulker"), IronShulkerRecipeSerializer)
    // Dyeing recipe
    register(RECIPE_SERIALIZER, ModId("dyed_iron_shulker"), DyedIronShulkerRecipe.recipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, IRON_VARIANT.shulkerBlock)
      .pattern("III")
      .pattern("ISI")
      .pattern("III")
      .input('I', IRON_INGOTS)
      .input('S', SHULKER_BOXES)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter, IronShulkerRecipeSerializer)

    ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, GOLD_VARIANT.shulkerBlock)
      .pattern("GGG")
      .pattern("GSG")
      .pattern("GGG")
      .input('G', GOLD_INGOTS)
      .input('S', ScGoodiesItemTags.IRON_SHULKER_BOX)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter, IronShulkerRecipeSerializer)

    ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, DIAMOND_VARIANT.shulkerBlock)
      .pattern("GGG")
      .pattern("GSG")
      .pattern("DDD")
      .input('G', GLASS_BLOCKS)
      .input('D', DIAMOND)
      .input('S', ScGoodiesItemTags.IRON_SHULKER_BOX)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter, IronShulkerRecipeSerializer, ModId("diamond_shulker_with_iron_shulker"))

    ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, DIAMOND_VARIANT.shulkerBlock)
      .pattern("GGG")
      .pattern("DSD")
      .pattern("GGG")
      .input('G', GLASS_BLOCKS)
      .input('D', DIAMOND)
      .input('S', ScGoodiesItemTags.GOLD_SHULKER_BOX)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter, IronShulkerRecipeSerializer, ModId("diamond_shulker_with_gold_shulker"))

    // Dyeing recipe
    specialRecipe(exporter, DyedIronShulkerRecipe.recipeSerializer)
  }
}
