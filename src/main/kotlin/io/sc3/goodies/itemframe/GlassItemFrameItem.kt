package io.sc3.goodies.itemframe

import io.sc3.goodies.util.BaseItem
import net.minecraft.entity.EntityType
import net.minecraft.entity.decoration.AbstractDecorationEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class GlassItemFrameItem<T : AbstractDecorationEntity>(
  settings: Settings,
  private val entityFactory: (world: World, pos: BlockPos, facing: Direction) -> T
) : BaseItem(settings) {
  override fun useOnBlock(ctx: ItemUsageContext): ActionResult {
    val pos = ctx.blockPos
    val facing = ctx.side
    val blockPos = pos.offset(facing)
    val player = ctx.player ?: return ActionResult.FAIL
    val stack = ctx.stack

    if (!canPlaceOn(player, facing, stack, blockPos)) {
      return ActionResult.FAIL
    }

    val world = ctx.world
    val entity = entityFactory.invoke(world, blockPos, facing)

    val nbt = stack.nbt
    if (nbt != null) {
      EntityType.loadFromEntityNbt(world, player, entity, nbt)
    }

    return if (entity.canStayAttached()) {
      if (!world.isClient) {
        entity.onPlace()
        world.emitGameEvent(player, GameEvent.ENTITY_PLACE, blockPos)
        world.spawnEntity(entity)
      }

      stack.decrement(1)
      ActionResult.success(world.isClient)
    } else {
      ActionResult.CONSUME
    }
  }

  private fun canPlaceOn(player: PlayerEntity, facing: Direction, stack: ItemStack, pos: BlockPos) =
    !player.world.isOutOfHeightLimit(pos) && player.canPlaceOn(pos, facing, stack)
}
