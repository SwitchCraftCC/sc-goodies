package io.sc3.goodies.datagen

import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.ironstorage.IronShulkerBlock
import io.sc3.goodies.ironstorage.IronShulkerBlockEntity
import io.sc3.goodies.ironstorage.IronStorageVariant
import io.sc3.goodies.misc.AmethystExtras
import io.sc3.goodies.misc.ConcreteExtras
import io.sc3.goodies.nature.ScTree
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.Item
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.MatchToolLootCondition
import net.minecraft.loot.condition.TableBonusLootCondition
import net.minecraft.loot.entry.DynamicEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.function.CopyNameLootFunction
import net.minecraft.loot.function.CopyNbtLootFunction
import net.minecraft.loot.function.SetContentsLootFunction
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.predicate.NumberRange
import net.minecraft.predicate.item.EnchantmentPredicate
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.util.DyeColor

class BlockLootTableProvider(out: FabricDataOutput) : FabricBlockLootTableProvider(out) {
  private val saplingDropChance = floatArrayOf(0.05f, 0.0625f, 0.084f, 0.1f)

  override fun generate() {
    IronStorageVariant.values().forEach { variant ->
      with (variant) {
        val type = shulkerBlockEntityType

        // Chest drop
        addDrop(chestBlock) { block -> nameableContainerDrops(block) }

        registerIronShulkerDrops(type, shulkerBlock) // Undyed shulker

        DyeColor.values().forEach { color ->
          val block = dyedShulkerBlocks[color]
            ?: throw IllegalStateException("Shulker block for variant=$this color=$color is null")
          registerIronShulkerDrops(type, block)
        }

        // Barrel drop
        addDrop(barrelBlock) { block -> nameableContainerDrops(block) }
      }
    }

    ConcreteExtras.colors.values.forEach { color ->
      addDrop(color.slabBlock, ::slabDrops)
      addDrop(color.stairsBlock)
    }
    addDrop(AmethystExtras.slabBlock)
    addDrop(AmethystExtras.stairsBlock)

    addTreeDrops(ModBlocks.sakuraSapling)
    addTreeDrops(ModBlocks.mapleSapling)
    addTreeDrops(ModBlocks.blueSapling)
    addTreeDrops(ModBlocks.pearSapling)
    addDrop(ModBlocks.pinkGrass) { block -> drops(block, Blocks.DIRT) }
    addDrop(ModBlocks.autumnGrass) { block -> drops(block, Blocks.DIRT) }
    addDrop(ModBlocks.blueGrass) { block -> drops(block, Blocks.DIRT) }

    addDrop(ModBlocks.dimmableLight)
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

  /* todo: copy oak tree drop behavior automatically */
  fun fruitLeavesDrops(leaves: Block?, drop: Block?, fruit: Item, vararg chance: Float): LootTable.Builder {
    return leavesDrops(leaves, drop, *chance).pool(
      LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f)).conditionally(
        MatchToolLootCondition.builder(
          ItemPredicate.Builder.create()
            .enchantment(EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))
        ).invert()
      ).with(
        (addSurvivesExplosionCondition(
          leaves,
          ItemEntry.builder(fruit)
        ) as LeafEntry.Builder<*>).conditionally(
          TableBonusLootCondition.builder(
            Enchantments.FORTUNE,
            0.005f,
            0.0055555557f,
            0.00625f,
            0.008333334f,
            0.025f
          )
        ) as LootPoolEntry.Builder<*>
      )
    )
  }
  private fun addTreeDrops(tree: ScTree) {
    addDrop(tree.leaves) { block ->
      if (tree.fruit != null) {
        fruitLeavesDrops(block, tree.sapling, tree.fruit, *saplingDropChance)
      } else {
        leavesDrops(block, tree.sapling, *saplingDropChance)
      }
    }
    addDrop(tree.sapling)
  }
}
