package io.sc3.goodies.ironstorage

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.util.niceDyeOrder
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier

enum class IronStorageVariant(
  val humanName: String,
  val chestId: String,
  val shulkerId: String,
  val barrelId: String,
  val chestParticle: Identifier,
  val rows: Int,
  val columns: Int = 9
) {
  IRON("Iron", "iron_chest", "shulker_box_iron", "iron_barrel",
    Identifier("block/iron_block"), 6),
  GOLD("Gold", "gold_chest", "shulker_box_gold", "gold_barrel",
    Identifier("block/gold_block"), 9),
  DIAMOND("Diamond", "diamond_chest", "shulker_box_diamond", "diamond_barrel",
    Identifier("block/diamond_block"), 9, 12);

  val size = rows * columns
  val screenTex = ModId("textures/gui/container/iron_chest_${columns}x${rows}.png")

  // Chests
  val chestBlock by lazy { Registries.BLOCK.get(ModId(chestId)) as IronChestBlock }
  val chestItem by lazy { Registries.ITEM.get(ModId(chestId)) as BlockItem }

  val chestBlockEntityType by lazy {
    @Suppress("UNCHECKED_CAST")
    Registries.BLOCK_ENTITY_TYPE.get(ModId(chestId)) as BlockEntityType<IronChestBlockEntity>
  }

  val chestScreenHandlerType: ScreenHandlerType<IronChestScreenHandler> by lazy {
    ScreenHandlerType({ id, inv -> IronChestScreenHandler(this, id, inv) }, FeatureFlags.VANILLA_FEATURES)
  }

  // Shulkers
  val shulkerBlock by lazy { Registries.BLOCK.get(ModId(shulkerId)) as IronShulkerBlock }
  val dyedShulkerBlocks: Map<DyeColor, IronShulkerBlock> by lazy {
    niceDyeOrder.associateWith { color ->
      Registries.BLOCK.get(ModId(shulkerId + "_" + color.getName())) as IronShulkerBlock
    }
  }
  val dyedShulkerItems: Map<DyeColor, IronShulkerItem> by lazy {
    niceDyeOrder.associateWith { color ->
      Registries.ITEM.get(ModId(shulkerId + "_" + color.getName())) as IronShulkerItem
    }
  }

  val shulkerBlockEntityType by lazy {
    @Suppress("UNCHECKED_CAST")
    Registries.BLOCK_ENTITY_TYPE.get(ModId(shulkerId)) as BlockEntityType<IronShulkerBlockEntity>
  }

  val shulkerScreenHandlerType: ScreenHandlerType<IronShulkerScreenHandler> by lazy {
    ScreenHandlerType({ id, inv -> IronShulkerScreenHandler(this, id, inv) }, FeatureFlags.VANILLA_FEATURES)
  }

  // Barrels
  val barrelBlock by lazy { Registries.BLOCK.get(ModId(barrelId)) as IronBarrelBlock }
  val barrelItem by lazy { Registries.ITEM.get(ModId(barrelId)) as BlockItem }

  val barrelBlockEntityType by lazy {
    @Suppress("UNCHECKED_CAST")
    Registries.BLOCK_ENTITY_TYPE.get(ModId(barrelId)) as BlockEntityType<IronBarrelBlockEntity>
  }
}
