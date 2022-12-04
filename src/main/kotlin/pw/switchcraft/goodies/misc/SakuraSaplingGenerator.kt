package pw.switchcraft.goodies.misc

import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.RegistryEntry
import net.minecraft.world.gen.feature.ConfiguredFeature
import pw.switchcraft.goodies.Registration.ModBlocks.sakuraTreeFeature

class SakuraSaplingGenerator : SaplingGenerator() {
  override fun getTreeFeature(random: Random, bees: Boolean): RegistryEntry<out ConfiguredFeature<*, *>>? =
    sakuraTreeFeature
}
