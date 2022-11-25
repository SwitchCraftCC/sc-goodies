package pw.switchcraft.goodies.misc

import net.minecraft.block.sapling.SaplingGenerator
import net.minecraft.util.math.random.Random
import pw.switchcraft.goodies.Registration.ModBlocks.sakuraTreeFeature

class SakuraSaplingGenerator : SaplingGenerator() {
  override fun getTreeFeature(random: Random, bees: Boolean) = sakuraTreeFeature
}
