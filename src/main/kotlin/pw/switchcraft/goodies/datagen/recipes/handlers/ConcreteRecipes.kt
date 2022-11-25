package pw.switchcraft.goodies.datagen.recipes.handlers

import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.data.server.recipe.RecipeProvider.*
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.book.RecipeCategory
import pw.switchcraft.goodies.datagen.ItemTagProvider.Companion.CONCRETE
import pw.switchcraft.goodies.misc.ConcreteExtras
import pw.switchcraft.library.recipe.RecipeHandler
import java.util.function.Consumer

object ConcreteRecipes : RecipeHandler {
  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    // Concrete Slabs and Stairs
    ConcreteExtras.colors.values.forEach {
      createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, it.slabBlock, ofItems(it.baseBlock))
        .group("concrete_slabs")
        .criterion("has_concrete", conditionsFromTag(CONCRETE))
        .offerTo(exporter)

      createStairsRecipe(it.stairsBlock, ofItems(it.baseBlock))
        .group("concrete_stairs")
        .criterion("has_concrete", conditionsFromTag(CONCRETE))
        .offerTo(exporter)
    }
  }
}
