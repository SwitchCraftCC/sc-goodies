package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.Registration.ModItems
import io.sc3.library.recipe.RecipeHandler
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.item.Items.*
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries
import java.util.function.Consumer

object IceCreamRecipes : RecipeHandler {
  private fun iceCreamRecipe(
    ingredientA: Ingredient,
    ingredientB: Ingredient,
    makes: Item,
    exporter: Consumer<RecipeJsonProvider>
  ) {
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.FOOD, makes)
      .pattern("ASB")
      .pattern("SsS")
      .pattern("mbe")
      .input('S', SNOWBALL)
      .input('s', SUGAR)
      .input('m', MILK_BUCKET)
      .input('b', BOWL)
      .input('e', EGG)
      .input('A', ingredientA)
      .input('B', ingredientB)
      .criterion("has_snowball", RecipeProvider.conditionsFromItem(SNOWBALL))
      .offerTo(exporter)

    ShapelessRecipeJsonBuilder
      .create(RecipeCategory.FOOD, makes)
      .input(ModItems.iceCreamVanilla)
      .input(ingredientA)
      .input(ingredientB)
      .criterion("has_icecream_vanilla", RecipeProvider.conditionsFromItem(ModItems.iceCreamVanilla))
      .offerTo(exporter, Registries.ITEM.getId(makes.asItem()).path + "_from_vanilla")
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Ice Cream
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.FOOD, ModItems.iceCreamVanilla)
      .pattern(" S ")
      .pattern("SsS")
      .pattern("mbe")
      .input('S', SNOWBALL)
      .input('s', SUGAR)
      .input('m', MILK_BUCKET)
      .input('b', BOWL)
      .input('e', EGG)
      .criterion("has_snowball", RecipeProvider.conditionsFromItem(SNOWBALL))
      .offerTo(exporter)

    iceCreamRecipe(
      Ingredient.ofItems(COCOA_BEANS),
      Ingredient.ofItems(COCOA_BEANS),
      ModItems.iceCreamChocolate, exporter
    )
    iceCreamRecipe(
      Ingredient.ofItems(SWEET_BERRIES),
      Ingredient.ofItems(SWEET_BERRIES),
      ModItems.iceCreamSweetBerry, exporter
    )
    iceCreamRecipe(
      Ingredient.ofItems(SPRUCE_SAPLING, SPRUCE_LEAVES),
      Ingredient.ofItems(SPRUCE_SAPLING, SPRUCE_LEAVES),
      ModItems.iceCreamSpruce, exporter
    )
    iceCreamRecipe(
      Ingredient.ofItems(MELON_SLICE),
      Ingredient.ofItems(MELON_SLICE),
      ModItems.iceCreamMelon, exporter
    )
    iceCreamRecipe(
      Ingredient.ofItems(BEETROOT),
      Ingredient.ofItems(BEETROOT),
      ModItems.iceCreamBeetroot, exporter
    )

    // Sundae Recipe
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.FOOD, ModItems.iceCreamSundae)
      .pattern(" b ")
      .pattern("bSb")
      .pattern("SvS")
      .input('v', ModItems.iceCreamVanilla)
      .input('S', GOLDEN_CARROT)
      .input('b', SWEET_BERRIES)
      .criterion("has_icecream_vanilla", RecipeProvider.conditionsFromItem(ModItems.iceCreamVanilla))
      .offerTo(exporter)

    // Neapolitan Recipe
    ShapelessRecipeJsonBuilder
      .create(RecipeCategory.FOOD, ModItems.iceCreamNeapolitan, 3)
      .input(ModItems.iceCreamVanilla)
      .input(ModItems.iceCreamChocolate)
      .input(ModItems.iceCreamSweetBerry)
      .criterion("has_icecream_vanilla", RecipeProvider.conditionsFromItem(ModItems.iceCreamVanilla))
      .criterion("has_icecream_chocolate", RecipeProvider.conditionsFromItem(ModItems.iceCreamChocolate))
      .criterion("has_icecream_sweetberry", RecipeProvider.conditionsFromItem(ModItems.iceCreamSweetBerry))
      .offerTo(exporter)
  }
}
