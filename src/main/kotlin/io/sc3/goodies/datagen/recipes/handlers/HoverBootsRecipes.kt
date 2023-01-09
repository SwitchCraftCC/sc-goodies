package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.datagen.ScGoodiesDatagen
import io.sc3.library.recipe.IngredientBrew
import io.sc3.library.recipe.IngredientEnchanted
import io.sc3.library.recipe.RecipeHandler
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromItem
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.DyeItem
import net.minecraft.item.Items
import net.minecraft.potion.Potions
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.util.DyeColor
import java.util.function.Consumer

object HoverBootsRecipes : RecipeHandler {
  private val log by ScGoodiesDatagen::log

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    ShapedRecipeJsonBuilder
      .create(RecipeCategory.TOOLS, ModItems.hoverBoots[DyeColor.WHITE]!!)
      .pattern("IDI")
      .pattern("fFj")
      .pattern("IFI")
      .input('I', Items.IRON_BLOCK)
      .input('D', Items.DIAMOND_BLOCK)
      .input('F', Items.FEATHER)
      .input('f', IngredientEnchanted(Enchantments.FEATHER_FALLING, 1).toVanilla())
      .input('j', IngredientBrew(StatusEffects.JUMP_BOOST, Potions.LEAPING).toVanilla())
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
