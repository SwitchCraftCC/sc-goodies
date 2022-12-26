package io.sc3.goodies.elytra

import io.sc3.goodies.ScGoodies.modId

class SpecialElytraItem(
  val type: SpecialElytraType,
  settings: Settings
) : BaseElytraItem(settings) {
  override fun getTranslationKey() = "item.$modId.elytra_${type.type}"
}
