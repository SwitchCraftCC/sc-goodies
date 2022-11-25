package pw.switchcraft.goodies.elytra

import net.minecraft.util.DyeColor
import net.minecraft.util.registry.Registry
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.ScGoodies.modId

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
