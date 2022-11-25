package pw.switchcraft.goodies.datagen.recipes.handlers

import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.item.DyeItem
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries.RECIPE_SERIALIZER
import net.minecraft.registry.Registry.register
import net.minecraft.util.DyeColor
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.datagen.ScGoodiesDatagen
import pw.switchcraft.goodies.datagen.recipes.HoverBootsRecipe
import pw.switchcraft.library.recipe.BetterComplexRecipeJsonBuilder
import pw.switchcraft.library.recipe.RecipeHandler
import java.util.function.Consumer

object HoverBootsRecipes : RecipeHandler {
  private val log by ScGoodiesDatagen::log

  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, "hover_boots", HoverBootsRecipe.recipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    BetterComplexRecipeJsonBuilder(ModItems.hoverBoots[DyeColor.WHITE]!!, HoverBootsRecipe.recipeSerializer)
      .criterion("has_diamond_boots", conditionsFromItem(Items.DIAMOND_BOOTS))
      .offerTo(exporter)

    val hoverBootsIngredient = ofItems(*ModItems.hoverBoots.values.toTypedArray())

    DyeColor.values().forEach { color ->
      log.info("Generating recipe for ${color.name} hover boots")
      ShapelessRecipeJsonBuilder
        .create(RecipeCategory.TOOLS, ModItems.hoverBoots[color]!!, 1)
        .group(ModId("dyed_hover_boots").toString())
        .input(hoverBootsIngredient)
        .input(DyeItem.byColor(color))
        .criterion("has_diamond_boots", conditionsFromItem(Items.DIAMOND_BOOTS))
        .offerTo(exporter, ModId("hover_boots_dyed_${color.getName()}"))
    }
  }
}
