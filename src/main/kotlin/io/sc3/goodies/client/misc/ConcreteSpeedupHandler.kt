package io.sc3.goodies.client.misc

import io.sc3.goodies.misc.ConcreteExtras
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.client.input.KeyboardInput
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.BlockPos

private const val VEL_MULTIPLIER = 1.25f
private const val Y_OFFSET = 1.0 / 16.0

object ConcreteSpeedupHandler {
  private var speedupBlocks = mutableSetOf<Block>()

  // Keep the state separate from the CPE one
  private val input by lazy { KeyboardInput(MinecraftClient.getInstance().options) }

  internal fun initEvents() {
    ConcreteExtras.colors.values.forEach { color ->
      speedupBlocks.add(color.baseBlock)
      speedupBlocks.add(color.slabBlock)
      speedupBlocks.add(color.stairsBlock)
    }
  }

  @JvmStatic
  fun playerTick(cp: ClientPlayerEntity) {
    val pos = cp.pos
    val floorPos = BlockPos.ofFloored(pos.x, pos.y - Y_OFFSET, pos.z)
    val floor = cp.world.getBlockState(floorPos).block

    if (speedupBlocks.contains(floor)) {
      input.tick(cp.shouldSlowDown(), 0.0f) // TODO: Does this need to be handled?

      if (
        (input.movementForward != 0.0f || input.movementSideways != 0.0f)
        && !cp.isSwimming && !cp.isFallFlying && !cp.abilities.flying
      ) {
        cp.setVelocity(
          cp.velocity.x * VEL_MULTIPLIER,
          cp.velocity.y,
          cp.velocity.z * VEL_MULTIPLIER
        )
      }
    }
  }
}
