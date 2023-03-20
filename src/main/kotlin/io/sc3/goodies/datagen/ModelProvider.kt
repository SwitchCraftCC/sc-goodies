package io.sc3.goodies.datagen

import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.Registration.ModBlocks.enderStorage
import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.elytra.DyedElytraItem
import io.sc3.goodies.elytra.SpecialElytraType
import io.sc3.goodies.ironstorage.IronStorageUpgrade
import io.sc3.goodies.ironstorage.IronStorageVariant
import io.sc3.goodies.misc.AmethystExtras
import io.sc3.goodies.misc.ConcreteExtras
import io.sc3.goodies.nature.ScTree
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.data.client.*
import net.minecraft.data.client.BlockStateModelGenerator.*
import net.minecraft.data.client.ModelIds.getBlockModelId
import net.minecraft.data.client.ModelIds.getItemModelId
import net.minecraft.data.client.Models.GENERATED
import net.minecraft.data.client.Models.HANDHELD
import net.minecraft.data.client.TexturedModel.makeFactory
import net.minecraft.registry.Registries
import net.minecraft.state.property.Properties
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import java.util.*

class ModelProvider(out: FabricDataOutput) : FabricModelProvider(out) {
  override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
    // Register block models for each iron chest variant
    IronStorageVariant.values().forEach { variant ->
      registerIronChest(gen, variant)

      registerIronShulker(gen, variant) // Undyed shulker
      DyeColor.values().forEach { registerIronShulker(gen, variant, it) }

      // Barrel
      registerIronBarrel(gen, variant)
    }

    // Ender Storage
    gen.blockStateCollector.accept(createSingletonBlockState(enderStorage, ModId("block/ender_storage")))

    // Concrete Slabs and Stairs
    ConcreteExtras.colors.values.forEach {
      registerSlab(gen, it.baseBlock, it.slabBlock, it.texture)
      registerStairs(gen, it.stairsBlock, it.texture)
    }
    registerSlab(gen, AmethystExtras.baseBlock, AmethystExtras.slabBlock, AmethystExtras.texture)
    registerStairs(gen, AmethystExtras.stairsBlock, AmethystExtras.texture)

    // Nature
    registerTree(gen, ModBlocks.sakuraSapling)
    registerTree(gen, ModBlocks.mapleSapling)
    registerTree(gen, ModBlocks.blueSapling)
    registerTopSoils(gen, ModBlocks.pinkGrass, ModBlocks.autumnGrass, ModBlocks.blueGrass)
  }

  override fun generateItemModels(gen: ItemModelGenerator) {
    // Iron Storage upgrades
    IronStorageUpgrade.values().forEach { upgrade ->
      log.info("Registering item model for $upgrade storage upgrade")
      gen.register(upgrade.upgradeItem, GENERATED)
    }

    ModItems.hoverBoots.values.forEach {
      gen.register(it, GENERATED)
    }

    gen.register(ModItems.barrelHammer, HANDHELD)
    gen.register(ModItems.itemMagnet, GENERATED)
    gen.register(ModItems.dragonScale, GENERATED)
    gen.register(ModItems.popcorn, GENERATED)
    gen.register(ModItems.ancientTome, GENERATED)
    gen.register(ModItems.glassItemFrame, GENERATED)
    gen.register(ModItems.glowGlassItemFrame, GENERATED)

    // Dyed + Special Elytra
    DyedElytraItem.dyedElytraItems.values
      .forEach { gen.register(it, GENERATED) }
    SpecialElytraType.values()
      .forEach { gen.register(it.item, GENERATED) }
  }

  private fun registerIronChest(gen: BlockStateModelGenerator, variant: IronStorageVariant) {
    log.info("Registering iron chest model for variant=$variant")

    with (variant) {
      gen.registerSingleton(chestBlock,
        makeFactory({ TextureMap().put(TextureKey.PARTICLE, chestParticle) }, Models.PARTICLE))

      ironChestModel.upload(
        getItemModelId(chestBlock.asItem()),
        TextureMap()
          .put(TextureKey.TEXTURE, ModId("entity/chest/${chestId}"))
          .put(TextureKey.PARTICLE, chestParticle),
        gen.modelCollector
      )
    }
  }

  private fun registerIronShulker(gen: BlockStateModelGenerator, variant: IronStorageVariant, color: DyeColor? = null) {
    log.info("Registering iron shulker model for variant=$variant color=$color")

    with (variant) {
      val block = (if (color != null) dyedShulkerBlocks[color] else shulkerBlock)
        ?: throw IllegalStateException("Shulker block for variant=$this color=$color is null")

      val blockId = Registries.BLOCK.getId(block)

      gen.registerSingleton(block, TexturedModel.PARTICLE)

      ironShulkerModel.upload(
        getItemModelId(block.asItem()),
        TextureMap()
          .put(TextureKey.TEXTURE, ModId("entity/shulker/${blockId.path}"))
          .put(TextureKey.PARTICLE, ModId("block/${blockId.path}")),
        gen.modelCollector
      )
    }
  }

  private fun registerIronBarrel(gen: BlockStateModelGenerator, variant: IronStorageVariant) {
    log.info("Registering iron barrel model for variant=$variant")

    with (variant) {
      val openId = TextureMap.getSubId(barrelBlock, "_top_open")

      gen.blockStateCollector.accept(VariantsBlockStateSupplier.create(barrelBlock)
        .coordinate(gen.createUpDefaultFacingVariantMap())
        .coordinate(BlockStateVariantMap.create(Properties.OPEN)
          .register(false, BlockStateVariant.create().put(
            VariantSettings.MODEL,
            TexturedModel.CUBE_BOTTOM_TOP.upload(barrelBlock, gen.modelCollector)
          ))
          .register(true, BlockStateVariant.create().put(
            VariantSettings.MODEL,
            TexturedModel.CUBE_BOTTOM_TOP
              .get(barrelBlock)
              .textures { m -> m.put(TextureKey.TOP, openId) }
              .upload(barrelBlock, "_open", gen.modelCollector)
          ))))
    }
  }

  private fun registerSlab(gen: BlockStateModelGenerator, baseBlock: Block, slabBlock: Block, texture: Identifier) {
    val map = TextureMap()
      .put(TextureKey.BOTTOM, texture)
      .put(TextureKey.TOP, texture)
      .put(TextureKey.SIDE, texture)

    val bottom = Models.SLAB.upload(slabBlock, map, gen.modelCollector)
    val top = Models.SLAB_TOP.upload(slabBlock, map, gen.modelCollector)
    gen.blockStateCollector.accept(createSlabBlockState(slabBlock, bottom, top, getBlockModelId(baseBlock)))
    gen.registerParentedItemModel(slabBlock, bottom)
  }

  private fun registerStairs(gen: BlockStateModelGenerator, stairsBlock: Block, texture: Identifier) {
    val map = TextureMap()
      .put(TextureKey.BOTTOM, texture)
      .put(TextureKey.TOP, texture)
      .put(TextureKey.SIDE, texture)

    val inner = Models.INNER_STAIRS.upload(stairsBlock, map, gen.modelCollector)
    val stairs = Models.STAIRS.upload(stairsBlock, map, gen.modelCollector)
    val outer = Models.OUTER_STAIRS.upload(stairsBlock, map, gen.modelCollector)
    gen.blockStateCollector.accept(createStairsBlockState(stairsBlock, inner, stairs, outer))
    gen.registerParentedItemModel(stairsBlock, stairs)
  }

  private fun registerTree(gen: BlockStateModelGenerator, tree: ScTree) {
    gen.registerSingleton(tree.leaves, TexturedModel.LEAVES)
    gen.registerFlowerPotPlant(tree.sapling, tree.potted, TintType.NOT_TINTED)
  }

  private fun registerTopSoils(gen: BlockStateModelGenerator, vararg blocks: Block) {
    val dirt = TextureMap.getId(Blocks.DIRT)
    val textureMap = TextureMap()
      .put(TextureKey.BOTTOM, dirt)
      .inherit(TextureKey.BOTTOM, TextureKey.PARTICLE)
      .put(TextureKey.TOP, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_top"))
      .put(TextureKey.SIDE, TextureMap.getSubId(Blocks.GRASS_BLOCK, "_snow"))
    val topSoilVariant = BlockStateVariant.create().put(
      VariantSettings.MODEL,
      Models.CUBE_BOTTOM_TOP.upload(Blocks.GRASS_BLOCK, "_snow", textureMap, gen.modelCollector)
    )

    blocks.forEach {
      gen.registerTopSoil(it, TexturedModel.CUBE_BOTTOM_TOP
        .get(it)
        .textures { t -> t.put(TextureKey.BOTTOM, dirt) }
        .upload(it, gen.modelCollector), topSoilVariant)
    }
  }

  companion object {
    private val log by ScGoodiesDatagen::log

    val ironChestModel = Model(Optional.of(ModId("block/iron_chest_base")), Optional.empty(),
      TextureKey.TEXTURE,
      TextureKey.PARTICLE
    )

    val ironShulkerModel = Model(Optional.of(ModId("block/iron_shulker_base")), Optional.empty(),
      TextureKey.TEXTURE,
      TextureKey.PARTICLE
    )
  }
}
