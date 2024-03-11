package io.sc3.goodies.shark

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ScGoodies.modId
import io.sc3.goodies.util.niceDyeOrder
import net.minecraft.registry.Registries
import net.minecraft.util.DyeColor

class DyedSharkItem(
  val color: DyeColor,
  settings: Settings
) : BaseSharkItem(settings) {
  override fun getTranslationKey() = "item.$modId.shark_${color.getName()}"

  companion object {
    val dyedSharkItems: Map<DyeColor, DyedSharkItem> by lazy {
      niceDyeOrder.associateWith { color ->
        Registries.ITEM.get(ModId("shark_${color.getName()}")) as DyedSharkItem
      }
    }
  }
}
