package io.sc3.goodies.ironchest

import net.minecraft.registry.Registries
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ironchest.IronChestVariant.*

enum class IronChestUpgrade(
  val itemName: String,
  val from: IronChestVariant?,
  val to: IronChestVariant,
) {
  VANILLA_IRON("vanilla_iron", from = null, to = IRON),
  IRON_GOLD("iron_gold", from = IRON, to = GOLD),
  IRON_DIAMOND("iron_diamond", from = IRON, to = DIAMOND),
  GOLD_DIAMOND("gold_diamond", from = GOLD, to = DIAMOND);

  val chestUpgrade by lazy {
    Registries.ITEM.get(ModId(itemName + "_chest_upgrade")) as IronChestUpgradeItem
  }

  val shulkerUpgrade by lazy {
    Registries.ITEM.get(ModId(itemName + "_shulker_upgrade")) as IronChestUpgradeItem
  }
}
