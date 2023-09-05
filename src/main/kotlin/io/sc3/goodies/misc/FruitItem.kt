package io.sc3.goodies.misc

import net.minecraft.item.FoodComponent
import io.sc3.goodies.util.BaseItem

class FruitItem(settings: Settings) : BaseItem(settings) {
  companion object {
    val foodComponent: FoodComponent = FoodComponent.Builder()
      .hunger(3)
      .saturationModifier(0.2f)
      .build()
  }
}
