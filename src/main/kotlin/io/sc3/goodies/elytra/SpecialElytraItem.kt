package pw.switchcraft.goodies.elytra

import pw.switchcraft.goodies.ScGoodies.modId

class SpecialElytraItem(
  val type: SpecialElytraType,
  settings: Settings
) : BaseElytraItem(settings) {
  override fun getTranslationKey() = "item.$modId.elytra_${type.type}"
}
