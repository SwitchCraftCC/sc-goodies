package pw.switchcraft.goodies.misc

import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry.BLOCK
import pw.switchcraft.goodies.ScGoodies.ModId

object ConcreteExtras {
  val colors = DyeColor.values().associateWith(::ConcreteExtra)

  class ConcreteExtra(val color: DyeColor) {
    private val col
      get() = color.getName()

    val baseBlockId = Identifier("${col}_concrete")
    val baseBlock by lazy { BLOCK.get(baseBlockId) }

    val slabBlockId = ModId("${col}_concrete_slab")
    val slabBlock by lazy { BLOCK.get(slabBlockId) }

    val stairsBlockId = ModId("${col}_concrete_stairs")
    val stairsBlock by lazy { BLOCK.get(stairsBlockId) }

    val texture = Identifier("block/${col}_concrete")
  }
}
