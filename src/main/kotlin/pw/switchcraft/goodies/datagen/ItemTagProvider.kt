package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import pw.switchcraft.goodies.ScGoodies.ModId

class ItemTagProvider(gen: FabricDataGenerator) : FabricTagProvider<Item>(gen, Registry.ITEM) {
  override fun generateTags() {
    getOrCreateTagBuilder(CONCRETE)
      .add(*DyeColor.values().map { Identifier("${it.getName()}_concrete") }.toTypedArray())
  }

  companion object {
    val CONCRETE = TagKey.of(Registry.ITEM_KEY, ModId("concrete"))
  }
}
