package pw.switchcraft.goodies.util

import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun Box.rotateTowards(facing: Direction): Box = rotateY(when(facing) {
  Direction.EAST -> 3
  Direction.SOUTH -> 2
  Direction.WEST -> 1
  else -> 0
})

fun Box.rotateY(count: Int): Box {
  val min = Vec3d(minX - 8, minY - 8, minZ - 8)
    .rotateY(count * Math.PI.toFloat() / 2)
  val max = Vec3d(maxX - 8, maxY - 8, maxZ - 8)
    .rotateY(count * Math.PI.toFloat() / 2)

  return Box(
    (min(min.x + 8, max.x + 8) * 32).roundToInt() / 32.0,
    (min(min.y + 8, max.y + 8) * 32).roundToInt() / 32.0,
    (min(min.z + 8, max.z + 8) * 32).roundToInt() / 32.0,
    (max(min.x + 8, max.x + 8) * 32).roundToInt() / 32.0,
    (max(min.y + 8, max.y + 8) * 32).roundToInt() / 32.0,
    (max(min.z + 8, max.z + 8) * 32).roundToInt() / 32.0
  )
}

fun Box.toDiv16(): Box =
  Box(minX / 16.0, minY / 16.0, minZ / 16.0, maxX / 16.0, maxY / 16.0, maxZ / 16.0)

fun Box.toDiv16VoxelShape(): VoxelShape =
  VoxelShapes.cuboid(minX / 16.0, minY / 16.0, minZ / 16.0, maxX / 16.0, maxY / 16.0, maxZ / 16.0)
