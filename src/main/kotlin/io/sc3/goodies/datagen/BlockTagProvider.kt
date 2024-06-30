package io.sc3.goodies.datagen

import dan200.computercraft.api.ComputerCraftTags
import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.ironstorage.IronStorageVariant
import io.sc3.goodies.misc.AmethystExtras
import io.sc3.goodies.misc.ConcreteExtras
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.BlockTags
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
    getOrCreateTagBuilder(BlockTags.SNIFFER_DIGGABLE_BLOCK)
      .add(ModBlocks.pinkGrass, ModBlocks.autumnGrass, ModBlocks.blueGrass)

    val pickaxeBlocks = mutableListOf<Block>(ModBlocks.enderStorage)
    val slabBlocks = mutableListOf<Block>()
    val stairBlocks = mutableListOf<Block>()
    val leafBlocks = mutableListOf<Block>()

    IronStorageVariant.entries.forEach {
      pickaxeBlocks.add(it.chestBlock)
      pickaxeBlocks.add(it.shulkerBlock)
      pickaxeBlocks.addAll(it.dyedShulkerBlocks.values)
      pickaxeBlocks.add(it.barrelBlock)
    }

    ConcreteExtras.colors.values.forEach {
      pickaxeBlocks.add(it.slabBlock)
      pickaxeBlocks.add(it.stairsBlock)
      slabBlocks.add(it.slabBlock)
      stairBlocks.add(it.stairsBlock)
    }

    pickaxeBlocks.add(AmethystExtras.slabBlock)
    pickaxeBlocks.add(AmethystExtras.stairsBlock)
    getOrCreateTagBuilder(BlockTags.CRYSTAL_SOUND_BLOCKS)
      .add(AmethystExtras.slabBlock, AmethystExtras.stairsBlock)

    leafBlocks.add(ModBlocks.mapleSapling.leaves)
    leafBlocks.add(ModBlocks.sakuraSapling.leaves)
    leafBlocks.add(ModBlocks.blueSapling.leaves)
    getOrCreateTagBuilder(BlockTags.SAPLINGS)
      .add(ModBlocks.mapleSapling.sapling, ModBlocks.sakuraSapling.sapling, ModBlocks.blueSapling.sapling)

    getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
      .add(*pickaxeBlocks.toTypedArray())

    getOrCreateTagBuilder(BlockTags.SLABS)
      .add(*slabBlocks.toTypedArray())
    getOrCreateTagBuilder(BlockTags.STAIRS)
      .add(*stairBlocks.toTypedArray())

    getOrCreateTagBuilder(BlockTags.COMPLETES_FIND_TREE_TUTORIAL)
      .add(*leafBlocks.toTypedArray())
    getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
      .add(*leafBlocks.toTypedArray())
    getOrCreateTagBuilder(BlockTags.LEAVES)
      .add(*leafBlocks.toTypedArray())
    getOrCreateTagBuilder(BlockTags.PARROTS_SPAWNABLE_ON)
      .add(*leafBlocks.toTypedArray())
    getOrCreateTagBuilder(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE)
      .add(*leafBlocks.toTypedArray())
    getOrCreateTagBuilder(ComputerCraftTags.Blocks.TURTLE_ALWAYS_BREAKABLE)
      .add(*leafBlocks.toTypedArray())
  }
}
