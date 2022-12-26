package io.sc3.goodies.nature

import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.random.Random
import net.minecraft.world.gen.feature.ConfiguredFeature

class ScSaplingGenerator(private val feature: RegistryKey<ConfiguredFeature<*, *>>) : SaplingGenerator() {
  override fun getTreeFeature(random: Random, bees: Boolean) = feature
}
