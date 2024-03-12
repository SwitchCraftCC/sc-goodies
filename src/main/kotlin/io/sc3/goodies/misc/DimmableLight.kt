package io.sc3.goodies.misc

import io.sc3.goodies.util.BaseBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.ToIntFunction

class DimmableLight(settings: Settings) : BaseBlock(settings) {
  companion object {
    val power: IntProperty = Properties.POWER

    fun createLightLevelFromPowerState(): ToIntFunction<BlockState> {
      return ToIntFunction { state: BlockState -> state.get(Properties.POWER) ?: 0 }
    }
  }

  override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
    builder.add(power)
  }

  override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
    return defaultState.with(power, ctx.world.getReceivedRedstonePower(ctx.blockPos)) as BlockState
  }

  override fun neighborUpdate(
    state: BlockState,
    world: World,
    pos: BlockPos,
    sourceBlock: Block,
    sourcePos: BlockPos,
    notify: Boolean
  ) {
    if (!world.isClient) {
      val receivedPower = world.getReceivedRedstonePower(pos)
      if (state.get(power) != receivedPower) {
        world.setBlockState(pos, state.with(power, receivedPower), NOTIFY_LISTENERS)
      }
    }
  }

  init {
    defaultState = stateManager.defaultState
      .with(power, 0)
  }
}
