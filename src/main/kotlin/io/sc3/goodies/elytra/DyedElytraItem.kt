package io.sc3.goodies.elytra

import net.minecraft.registry.Registries
import net.minecraft.util.DyeColor
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ScGoodies.modId

class DyedElytraItem(
  val color: DyeColor,
  settings: Settings
) : BaseElytraItem(settings) {
  override fun getTranslationKey() = "item.$modId.elytra_${color.getName()}"

  companion object {
    val dyedElytraItems: Map<DyeColor, DyedElytraItem> by lazy {
      DyeColor.values().associateWith { color ->
        Registries.ITEM.get(ModId("elytra_${color.getName()}")) as DyedElytraItem
      }
    }
  }
}
