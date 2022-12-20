package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.DynamicEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.CopyNameLootFunction
import net.minecraft.loot.function.CopyNbtLootFunction
import net.minecraft.loot.function.SetContentsLootFunction
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.util.DyeColor
import pw.switchcraft.goodies.Registration.ModBlocks
import pw.switchcraft.goodies.ironchest.IronChestVariant
import pw.switchcraft.goodies.ironshulker.IronShulkerBlock
import pw.switchcraft.goodies.ironshulker.IronShulkerBlockEntity
import pw.switchcraft.goodies.nature.ScTree

class BlockLootTableProvider(out: FabricDataOutput) : FabricBlockLootTableProvider(out) {
  private val saplingDropChance = floatArrayOf(0.05f, 0.0625f, 0.084f, 0.1f)

  override fun generate() {
    IronChestVariant.values().forEach { variant ->
      with (variant) {
        val type = shulkerBlockEntityType

        registerIronShulkerDrops(type, shulkerBlock) // Undyed shulker

        DyeColor.values().forEach { color ->
          val block = dyedShulkerBlocks[color]
            ?: throw IllegalStateException("Shulker block for variant=$this color=$color is null")
          registerIronShulkerDrops(type, block)
        }
      }
    }

    addTreeDrops(ModBlocks.sakuraSapling)
    addTreeDrops(ModBlocks.mapleSapling)
    addTreeDrops(ModBlocks.blueSapling)
    addDrop(ModBlocks.pinkGrass) { block -> drops(block, Blocks.DIRT) }
    addDrop(ModBlocks.autumnGrass) { block -> drops(block, Blocks.DIRT) }
    addDrop(ModBlocks.blueGrass) { block -> drops(block, Blocks.DIRT) }
  }

  private fun registerIronShulkerDrops(type: BlockEntityType<IronShulkerBlockEntity>, block: Block) {
    // Allow iron shulkers to keep their contents when destroyed by an explosion, mined by a turtle, etc.
    val builder = LootTable.builder()
      .pool(addSurvivesExplosionCondition(
        block,
        LootPool.builder()
          .rolls(ConstantLootNumberProvider.create(1.0f))
          .with(ItemEntry.builder(block)
            .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
            .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
              .withOperation("Lock", "BlockEntityTag.Lock")
              .withOperation("LootTable", "BlockEntityTag.LootTable")
              .withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed"))
            .apply(SetContentsLootFunction.builder(type)
              .withEntry(DynamicEntry.builder(IronShulkerBlock.contents)))
          )
      ))

    addDrop(block, builder)
  }

  private fun addTreeDrops(tree: ScTree) {
    addDrop(tree.leaves) { block -> leavesDrops(block, tree.sapling, *saplingDropChance) }
    addDrop(tree.sapling)
  }
}
