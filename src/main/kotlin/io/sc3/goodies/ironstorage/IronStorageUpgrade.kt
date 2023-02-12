package io.sc3.goodies.ironstorage

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ironstorage.IronStorageVariant.*
import net.minecraft.registry.Registries

enum class IronStorageUpgrade(
  val itemName: String,
  val from: IronStorageVariant?,
  val to: IronStorageVariant,
) {
  VANILLA_IRON("vanilla_iron", from = null, to = IRON),
  IRON_GOLD("iron_gold", from = IRON, to = GOLD),
  IRON_DIAMOND("iron_diamond", from = IRON, to = DIAMOND),
  GOLD_DIAMOND("gold_diamond", from = GOLD, to = DIAMOND);

  val upgradeItem by lazy {
    Registries.ITEM.get(ModId(itemName + "_chest_upgrade")) as IronStorageUpgradeItem
  }
}
