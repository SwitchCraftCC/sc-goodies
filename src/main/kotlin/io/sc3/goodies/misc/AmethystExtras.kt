package io.sc3.goodies.misc

import net.minecraft.registry.Registries.BLOCK
import net.minecraft.util.Identifier
import io.sc3.goodies.ScGoodies.ModId

object AmethystExtras {
  val baseBlockId = Identifier("amethyst_block")
  val baseBlock by lazy { BLOCK.get(baseBlockId) }

  val slabBlockId = ModId("amethyst_slab")
  val slabBlock by lazy { BLOCK.get(slabBlockId) }

  val stairsBlockId = ModId("amethyst_stairs")
  val stairsBlock by lazy { BLOCK.get(stairsBlockId) }

  val texture = Identifier("block/amethyst_block")
}
