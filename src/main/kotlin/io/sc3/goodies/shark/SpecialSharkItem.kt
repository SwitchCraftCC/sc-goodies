package io.sc3.goodies.shark

import io.sc3.goodies.ScGoodies.modId

class SpecialSharkItem(
  val type: SpecialSharkType,
  settings: Settings
) : BaseSharkItem(settings) {
  override fun getTranslationKey() = "item.$modId.shark_${type.type}"
}
