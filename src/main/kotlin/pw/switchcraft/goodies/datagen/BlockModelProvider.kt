package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.*
import net.minecraft.data.client.BlockStateModelGenerator.createSingletonBlockState
import net.minecraft.util.DyeColor
import net.minecraft.util.registry.Registry
import pw.switchcraft.goodies.Registration.ModBlocks.enderStorage
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.ironchest.IronChestVariant
import java.util.*

class BlockModelProvider(generator: FabricDataGenerator) : FabricModelProvider(generator) {
  override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
    // Register block models for each iron chest variant
    IronChestVariant.values().forEach { variant ->
      registerIronChest(gen, variant)

      registerIronShulker(gen, variant) // Undyed shulker
      DyeColor.values().forEach { registerIronShulker(gen, variant, it) }
    }

    // Ender Storage
    gen.blockStateCollector.accept(createSingletonBlockState(enderStorage, ModId("block/ender_storage")))
  }

  override fun generateItemModels(gen: ItemModelGenerator) {
  }

  private fun registerIronChest(gen: BlockStateModelGenerator, variant: IronChestVariant) {
    log.info("Registering iron chest model for variant=$variant")

    with (variant) {
      gen.registerSingleton(
        chestBlock,
        TextureMap()
          .put(TextureKey.TEXTURE, ModId("entity/chest/${chestId}"))
          .put(TextureKey.PARTICLE, chestParticle),
        ironChestModel
      )

      gen.registerParentedItemModel(chestBlock, ModelIds.getBlockModelId(chestBlock))
    }
  }

  private fun registerIronShulker(gen: BlockStateModelGenerator, variant: IronChestVariant, color: DyeColor? = null) {
    log.info("Registering iron shulker model for variant=$variant color=$color")

    with (variant) {
      val block = (if (color != null) dyedShulkerBlocks[color] else shulkerBlock)
        ?: throw IllegalStateException("Shulker block for variant=$this color=$color is null")

      val id = Registry.BLOCK.getId(block).path
      val texId = ModId("entity/shulker/$id")

      gen.registerSingleton(
        block,
        TextureMap()
          .put(TextureKey.TEXTURE, texId)
          .put(TextureKey.PARTICLE, texId),
        ironShulkerModel
      )

      gen.registerParentedItemModel(block, ModelIds.getBlockModelId(block))
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
