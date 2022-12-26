package io.sc3.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import io.sc3.goodies.ScGoodies.ModId
import java.util.concurrent.CompletableFuture

class ItemTagProvider(
  out: FabricDataOutput,
  future: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<Item>(out, RegistryKeys.ITEM, future) {
  override fun configure(arg: RegistryWrapper.WrapperLookup) {
    getOrCreateTagBuilder(CONCRETE)
      .add(*DyeColor.values().map { Identifier("${it.getName()}_concrete") }.toTypedArray())
  }

  companion object {
    val CONCRETE: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, ModId("concrete"))
  }
}
