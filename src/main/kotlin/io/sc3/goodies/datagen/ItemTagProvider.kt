package io.sc3.goodies.datagen

import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.ScGoodiesItemTags
import io.sc3.goodies.ScGoodiesItemTags.CONCRETE
import io.sc3.goodies.elytra.DyedElytraItem
import io.sc3.goodies.elytra.SpecialElytraType
import io.sc3.goodies.ironstorage.IronStorageVariant
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class ItemTagProvider(
  out: FabricDataOutput,
  future: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<Item>(out, RegistryKeys.ITEM, future) {
  override fun configure(arg: RegistryWrapper.WrapperLookup) {
    getOrCreateTagBuilder(CONCRETE)
      .add(*DyeColor.values().map { Identifier("${it.getName()}_concrete") }.toTypedArray())

    getOrCreateTagBuilder(ScGoodiesItemTags.ELYTRA)
      .add(Items.ELYTRA)
      .apply {
        DyedElytraItem.dyedElytraItems.values.forEach { add(it) }
        SpecialElytraType.values().forEach { add(it.item) }
      }

    addShulkers(ScGoodiesItemTags.IRON_SHULKER_BOX, IronStorageVariant.IRON)
    addShulkers(ScGoodiesItemTags.GOLD_SHULKER_BOX, IronStorageVariant.GOLD)
    addShulkers(ScGoodiesItemTags.DIAMOND_SHULKER_BOX, IronStorageVariant.DIAMOND)
    getOrCreateTagBuilder(ScGoodiesItemTags.ANY_IRON_SHULKER_BOX)
      .addTag(ScGoodiesItemTags.IRON_SHULKER_BOX)
      .addTag(ScGoodiesItemTags.GOLD_SHULKER_BOX)
      .addTag(ScGoodiesItemTags.DIAMOND_SHULKER_BOX)

    getOrCreateTagBuilder(ScGoodiesItemTags.ANY_UPGRADABLE_STORAGE)
      .add(Items.CHEST)
      .add(Items.SHULKER_BOX)
      .add(Items.BARREL)

    val ironStorage = getOrCreateTagBuilder(ScGoodiesItemTags.ANY_IRON_STORAGE)
      .addTag(ScGoodiesItemTags.ANY_IRON_SHULKER_BOX)

    IronStorageVariant.values().forEach { variant ->
      ironStorage
        .add(variant.chestItem)
        .add(variant.barrelItem)
    }

    getOrCreateTagBuilder(ItemTags.TOOLS)
      .add(ModItems.barrelHammer)
  }

  private fun addShulkers(tag: TagKey<Item>, variant: IronStorageVariant) {
    getOrCreateTagBuilder(tag)
      .add(variant.shulkerBlock.asItem())
      .apply { variant.dyedShulkerItems.values.forEach { add(it) } }
  }
}
