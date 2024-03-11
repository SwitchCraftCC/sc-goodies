package io.sc3.goodies

import io.sc3.goodies.ScGoodies.ModId
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object ScGoodiesItemTags {
  val CONCRETE = register("concrete")

  val ELYTRA = register("elytra")
  val SHARK = register("shark")

  val ANY_IRON_SHULKER_BOX = register("iron_shulker")
  val IRON_SHULKER_BOX = register("iron_shulker/iron")
  val GOLD_SHULKER_BOX = register("iron_shulker/gold")
  val DIAMOND_SHULKER_BOX = register("iron_shulker/diamond")

  val ANY_UPGRADABLE_STORAGE = register("upgradable_storage")
  val ANY_IRON_STORAGE = register("iron_storage")

  private fun register(id: String): TagKey<Item> = TagKey.of(RegistryKeys.ITEM, ModId(id))
}
