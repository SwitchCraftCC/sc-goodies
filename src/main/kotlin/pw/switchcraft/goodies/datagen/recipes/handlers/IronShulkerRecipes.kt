package pw.switchcraft.goodies.datagen.recipes.handlers

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.*
import net.minecraft.data.server.RecipeProvider.conditionsFromTag
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.ItemStack
import net.minecraft.item.Items.DIAMOND
import net.minecraft.recipe.Ingredient.fromTag
import net.minecraft.recipe.Ingredient.ofItems
import net.minecraft.recipe.SpecialRecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry.RECIPE_SERIALIZER
import net.minecraft.util.registry.Registry.register
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.datagen.recipes.BaseIronShulkerRecipe
import pw.switchcraft.goodies.datagen.recipes.BetterComplexRecipeJsonBuilder
import pw.switchcraft.goodies.datagen.recipes.DyedIronShulkerRecipe
import pw.switchcraft.goodies.datagen.recipes.ingredients.IronShulkerIngredient
import pw.switchcraft.goodies.datagen.recipes.RecipeGenerator.Companion.specialRecipe
import java.util.function.Consumer
import pw.switchcraft.goodies.ironchest.IronChestVariant.DIAMOND as DIAMOND_VARIANT
import pw.switchcraft.goodies.ironchest.IronChestVariant.GOLD as GOLD_VARIANT
import pw.switchcraft.goodies.ironchest.IronChestVariant.IRON as IRON_VARIANT

object IronShulkerRecipes : RecipeHandler {
  private val iron = fromTag(IRON_INGOTS)
  private val gold = fromTag(GOLD_INGOTS)
  private val diamond = ofItems(DIAMOND)
  private val glass = fromTag(GLASS_BLOCKS)
  private val vanillaShulkers = fromTag(SHULKER_BOXES)

  override fun registerSerializers() {
    register(RECIPE_SERIALIZER, ModId("iron_shulker"), ironShulkerRecipeSerializer)
    register(RECIPE_SERIALIZER, ModId("gold_shulker"), goldShulkerRecipeSerializer)
    register(RECIPE_SERIALIZER, ModId("diamond_shulker_with_iron_shulker"), diamondShulkerIronRecipeSerializer)
    register(RECIPE_SERIALIZER, ModId("diamond_shulker_with_gold_shulker"), diamondShulkerGoldRecipeSerializer)

    // Dyeing recipe
    register(RECIPE_SERIALIZER, ModId("dyed_iron_shulker"), DyedIronShulkerRecipe.recipeSerializer)
  }

  override fun generateRecipes(exporter: Consumer<RecipeJsonProvider>) {
    BetterComplexRecipeJsonBuilder(IRON_VARIANT.shulkerBlock, ironShulkerRecipeSerializer)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter)
    BetterComplexRecipeJsonBuilder(GOLD_VARIANT.shulkerBlock, goldShulkerRecipeSerializer)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter)
    BetterComplexRecipeJsonBuilder(DIAMOND_VARIANT.shulkerBlock, diamondShulkerIronRecipeSerializer)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter, ModId("diamond_shulker_with_iron_shulker"))
    BetterComplexRecipeJsonBuilder(DIAMOND_VARIANT.shulkerBlock, diamondShulkerGoldRecipeSerializer)
      .criterion("has_shulker_box", conditionsFromTag(SHULKER_BOXES))
      .offerTo(exporter, ModId("diamond_shulker_with_gold_shulker"))

    // Dyeing recipe
    specialRecipe(exporter, DyedIronShulkerRecipe.recipeSerializer)
  }

  private val ironShulkerRecipeSerializer = SpecialRecipeSerializer { IronShulkerRecipe(it) }
  class IronShulkerRecipe(id: Identifier) : BaseIronShulkerRecipe(
    id,
    ItemStack(IRON_VARIANT.shulkerBlock),
    listOf(
      iron, iron, iron,
      iron, vanillaShulkers, iron,
      iron, iron, iron
    )
  ) {
    override fun getSerializer() = ironShulkerRecipeSerializer
  }

  private val goldShulkerRecipeSerializer = SpecialRecipeSerializer { GoldShulkerRecipe(it) }
  class GoldShulkerRecipe(id: Identifier) : BaseIronShulkerRecipe(
    id,
    ItemStack(GOLD_VARIANT.shulkerBlock),
    listOf(
      gold, gold, gold,
      gold, IronShulkerIngredient(IRON_VARIANT), gold,
      gold, gold, gold
    )
  ) {
    override fun getSerializer() = goldShulkerRecipeSerializer
  }

  private val diamondShulkerIronRecipeSerializer = SpecialRecipeSerializer { DiamondShulkerIronRecipe(it) }
  class DiamondShulkerIronRecipe(id: Identifier) : BaseIronShulkerRecipe(
    id,
    ItemStack(DIAMOND_VARIANT.shulkerBlock),
    listOf(
      glass, glass, glass,
      glass, IronShulkerIngredient(IRON_VARIANT), glass,
      diamond, diamond, diamond
    )
  ) {
    override fun getSerializer() = diamondShulkerIronRecipeSerializer
  }

  private val diamondShulkerGoldRecipeSerializer = SpecialRecipeSerializer { DiamondShulkerGoldRecipe(it) }
  class DiamondShulkerGoldRecipe(id: Identifier) : BaseIronShulkerRecipe(
    id,
    ItemStack(DIAMOND_VARIANT.shulkerBlock),
    listOf(
      glass, glass, glass,
      diamond, IronShulkerIngredient(GOLD_VARIANT), diamond,
      glass, glass, glass
    )
  ) {
    override fun getSerializer() = diamondShulkerGoldRecipeSerializer
  }
}
