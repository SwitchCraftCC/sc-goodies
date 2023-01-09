package io.sc3.goodies

import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object ScGoodiesItemTags {
  val ELYTRA = register("elytra")

  val ANY_IRON_SHULKER_BOX = register("iron_shulker")
  val IRON_SHULKER_BOX = register("iron_shulker/iron")
  val GOLD_SHULKER_BOX = register("iron_shulker/gold")
  val DIAMOND_SHULKER_BOX = register("iron_shulker/diamond")

  private fun register(id: String): TagKey<Item> = TagKey.of(RegistryKeys.ITEM, ScGoodies.ModId(id))
}
