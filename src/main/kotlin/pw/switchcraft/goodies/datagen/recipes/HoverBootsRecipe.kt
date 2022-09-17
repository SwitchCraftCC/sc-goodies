package pw.switchcraft.goodies.datagen.recipes

import net.minecraft.enchantment.Enchantments.FEATHER_FALLING
import net.minecraft.entity.effect.StatusEffects.JUMP_BOOST
import net.minecraft.item.ItemStack
import net.minecraft.item.Items.*
import net.minecraft.potion.Potions.LEAPING
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.library.recipe.BetterSpecialRecipe
import pw.switchcraft.library.recipe.IngredientBrew
import pw.switchcraft.library.recipe.IngredientEnchanted

class HoverBootsRecipe(id: Identifier) : BetterSpecialRecipe(id) {
  private val featherFalling = IngredientEnchanted(mapOf(FEATHER_FALLING to 1))
  private val jumpBoost = IngredientBrew(JUMP_BOOST, LEAPING)

  override val ingredients = listOf(
    ofItems(IRON_BLOCK), ofItems(DIAMOND_BOOTS), ofItems(IRON_BLOCK),
    featherFalling,      ofItems(FEATHER),       jumpBoost,
    ofItems(IRON_BLOCK), ofItems(FEATHER),       ofItems(IRON_BLOCK)
  )

  override val outputItem = ItemStack(ModItems.hoverBoots[DyeColor.WHITE]!!)

  override fun getSerializer() = recipeSerializer

  companion object {
    val recipeSerializer = SpecialRecipeSerializer { HoverBootsRecipe(it) }
  }
}
