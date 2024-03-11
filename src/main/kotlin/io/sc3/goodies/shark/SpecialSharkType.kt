package io.sc3.goodies.shark

import io.sc3.goodies.ScGoodies.ModId
import net.minecraft.registry.Registries
import net.minecraft.util.DyeColor
import net.minecraft.util.DyeColor.*

enum class SpecialSharkType(
  val humanName: String,
  val type: String,
  val recipeColors: List<DyeColor>
) {
  TRANS("Trans", "trans", listOf(LIGHT_BLUE, LIGHT_BLUE, PINK, PINK, WHITE));

  val item: SpecialSharkItem by lazy {
    Registries.ITEM.get(ModId("shark_$type")) as SpecialSharkItem
  }
}
