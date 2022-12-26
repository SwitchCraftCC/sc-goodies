package io.sc3.goodies.util

import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object ChestUtil {
  fun playSound(world: World, pos: BlockPos, soundEvent: SoundEvent) {
    val x = pos.x.toDouble() + 0.5
    val y = pos.y.toDouble() + 0.5
    val z = pos.z.toDouble() + 0.5
    world.playSound(null, x, y, z, soundEvent, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f)
  }
}
