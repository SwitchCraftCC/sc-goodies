package pw.switchcraft.goodies.ironchest

import net.minecraft.util.registry.Registry
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.ironchest.IronChestVariant.*

enum class IronChestUpgrade(
  val itemName: String,
  val from: IronChestVariant?,
  val to: IronChestVariant,
) {
  VANILLA_IRON("vanilla_iron", from = null, to = IRON),
  IRON_GOLD("iron_gold", from = IRON, to = GOLD),
  GOLD_DIAMOND("gold_diamond", from = GOLD, to = DIAMOND);

  val chestUpgrade by lazy {
    Registry.ITEM.get(ModId(itemName + "_chest_upgrade")) as IronChestUpgradeItem
  }

  val shulkerUpgrade by lazy {
    Registry.ITEM.get(ModId(itemName + "_shulker_upgrade")) as IronChestUpgradeItem
  }
}
