package io.sc3.goodies.elytra

import net.minecraft.registry.Registries
import net.minecraft.util.DyeColor
import net.minecraft.util.DyeColor.*
import io.sc3.goodies.ScGoodies.ModId

enum class SpecialElytraType(
  val humanName: String,
  val type: String,
  val recipeColors: List<DyeColor>
) {
  LESBIAN("Lesbian", "lesbian", listOf(RED, ORANGE, WHITE, PINK, MAGENTA)),
  NON_BINARY("Non-Binary", "non_binary", listOf(YELLOW, WHITE, PURPLE, BLACK)),
  PRIDE("Pride", "pride", listOf(RED, ORANGE, YELLOW, GREEN, BLUE, PURPLE)),
  TRANS("Trans", "trans", listOf(LIGHT_BLUE, PINK, WHITE));

  val modelTexture = ModId("textures/entity/elytra/elytra_$type.png")

  val item: SpecialElytraItem by lazy {
    Registries.ITEM.get(ModId("elytra_$type")) as SpecialElytraItem
  }
}
