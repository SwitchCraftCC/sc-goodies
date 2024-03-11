package io.sc3.goodies.datagen.recipes.handlers

import io.sc3.goodies.shark.DyedSharkItem
import io.sc3.goodies.shark.SpecialSharkType
import io.sc3.library.recipe.RecipeHandler
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.conditionsFromTag
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.ItemTags
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import java.util.function.Consumer

object SharkRecipes : RecipeHandler {
  // TODO: is this the right way to get the wool item?
  private fun colorToWool(color: DyeColor): Item = Registries.ITEM.get(Identifier("${color.getName()}_wool"))

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // For the dyed sharks, the recipe will be as follows:
    //  X
    // XXX
    // DW
    // Where 'X' is the dyed wool colour, 'D' is pink dye, and 'W' is white wool.
    //
    // For the special sharks, the 'recipeColors' list will contain five wool (throw if it doesn't), mapping to the
    // recipe as follows:
    //  0
    // 123
    // D4
    // Where the number is the index in the ingredient list, and 'D' is pink dye.

    // Dyed sharks
    DyedSharkItem.dyedSharkItems.forEach { (color, item) ->
      ShapedRecipeJsonBuilder
        .create(RecipeCategory.COMBAT, item)
        .pattern(" X ")
        .pattern("XXX")
        .pattern("DW ")
        .input('X', colorToWool(color))
        .input('D', Items.PINK_DYE)
        .input('W', Items.WHITE_WOOL)
        .criterion("has_wool", conditionsFromTag(ItemTags.WOOL))
        .offerTo(exporter)
    }

    // Special sharks
    SpecialSharkType.values().forEach { type ->
      require(type.recipeColors.size == 5) { "Special shark recipe colors must have 5 elements" }

      ShapedRecipeJsonBuilder
        .create(RecipeCategory.COMBAT, type.item)
        .pattern(" 0 ")
        .pattern("123")
        .pattern("D4 ")
        .apply {
          type.recipeColors.forEachIndexed { index, color ->
            input(index.toString()[0], colorToWool(color))
          }
        }
        .input('D', Items.PINK_DYE)
        .criterion("has_wool", conditionsFromTag(ItemTags.WOOL))
        .offerTo(exporter)
    }
  }
}
