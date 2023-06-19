import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleAnimation
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.shared.turtle.TurtleUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.ItemEntity
import net.minecraft.util.math.Box
class MagnetTurtlePeripheral(var turtleAccess: ITurtleAccess, var side: TurtleSide) : IPeripheral {
  override fun getType(): String {
    return "magnet"
  }

  override fun equals(other: IPeripheral?): Boolean {
    return other is MagnetTurtlePeripheral
  }

  @LuaFunction(mainThread = true)
  fun magnetize(distance: Int): MethodResult {
    if (distance in 1..24) {
      turtleAccess.consumeFuel(distance * distance)
      return if (turtleAccess.fuelLevel >= distance * distance || !turtleAccess.isFuelNeeded) {
        val range: Box = Box(turtleAccess.position).expand(distance.toDouble(), (2).toDouble(), distance.toDouble())
        val entityList: List<Entity> = turtleAccess.level.getOtherEntities(null, range)
        for (entity in entityList) {
          if (entity is ItemEntity) {
            TurtleUtil.storeItemOrDrop(turtleAccess, entity.stack)
            entity.remove(Entity.RemovalReason.DISCARDED)
          }
        }
        turtleAccess.playAnimation(if (side == TurtleSide.LEFT) TurtleAnimation.SWING_LEFT_TOOL else TurtleAnimation.SWING_RIGHT_TOOL)
        MethodResult.of(true)
      } else {
        MethodResult.of(false, "Not enough fuel")
      }
    }
    return MethodResult.of(false, "Invalid distance")
  }
}
