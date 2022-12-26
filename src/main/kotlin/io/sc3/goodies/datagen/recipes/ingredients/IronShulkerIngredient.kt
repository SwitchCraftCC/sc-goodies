package io.sc3.goodies.datagen.recipes.ingredients

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntComparators
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeMatcher
import io.sc3.goodies.ironchest.IronChestVariant
import io.sc3.goodies.ironshulker.IronShulkerBlock
import io.sc3.goodies.ironshulker.IronShulkerItem
import java.util.stream.Stream

class IronShulkerIngredient(
  private val variantFilter: IronChestVariant? = null
) : Ingredient(Stream.empty()) {
  private var packed: IntList? = null

  override fun getMatchingStacks(): Array<ItemStack> {
    val stacks = mutableListOf<ItemStack>()
    val variants = if (variantFilter != null) {
      listOf(variantFilter)
    } else {
      IronChestVariant.values().toList()
    }

    variants.forEach { variant ->
      stacks.add(ItemStack(variant.shulkerBlock))
      variant.dyedShulkerBlocks.values.map { ItemStack(it) }.toCollection(stacks)
    }

    return stacks.toTypedArray()
  }

  override fun getMatchingItemIds(): IntList {
    return packed ?: run {
      val list = IntArrayList()
      matchingStacks.forEach { list.add(RecipeMatcher.getItemId(it)) }
      list.sort(IntComparators.NATURAL_COMPARATOR)
      list
    }.also { packed = it }
  }

  override fun test(stack: ItemStack?): Boolean {
    if (stack == null || stack.isEmpty) return false
    val item = stack.item as? IronShulkerItem ?: return false
    val block = item.block as? IronShulkerBlock ?: return false
    return if (variantFilter != null) variantFilter == block.variant else true
  }

  override fun isEmpty() = false
}
