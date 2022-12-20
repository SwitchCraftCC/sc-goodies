package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
import pw.switchcraft.goodies.Registration.ModBlocks
import java.util.concurrent.CompletableFuture

class BlockTagProvider(
  out: FabricDataOutput,
  future: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider<Block>(out, RegistryKeys.BLOCK, future) {
  override fun configure(arg: RegistryWrapper.WrapperLookup) {
    getOrCreateTagBuilder(BlockTags.DIRT)
      .add(ModBlocks.pinkGrass, ModBlocks.autumnGrass, ModBlocks.blueGrass)
    getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
      .add(ModBlocks.pinkGrass, ModBlocks.autumnGrass, ModBlocks.blueGrass)
  }
}
