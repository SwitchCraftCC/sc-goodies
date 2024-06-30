package io.sc3.goodies.misc

import io.sc3.goodies.util.BaseItem
import io.sc3.text.color
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.StairsBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.property.Property
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class StairWrenchItem(settings: Settings) : BaseItem(settings) {
  private var properties: List<Property<*>> = listOf(StairsBlock.FACING, StairsBlock.HALF, StairsBlock.SHAPE)

  override fun canMine(state: BlockState?, world: World, pos: BlockPos?, miner: PlayerEntity): Boolean {
    if (!world.isClient) {
      this.use(miner, state ?: return false, world, pos ?: return false, false, miner.getStackInHand(Hand.MAIN_HAND))
    }
    return false
  }

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    var mode = stack.orCreateNbt.getString("Property")
    if (mode.isEmpty()) {
      stack.nbt?.putString("Property", StairsBlock.FACING.name)
      mode = StairsBlock.FACING.name
    }
    tooltip.add(translatable("$translationKey.mode", mode).color(Formatting.GRAY))
    super.appendTooltip(stack, world, tooltip, context)
  }

  override fun useOnBlock(context: ItemUsageContext): ActionResult {
    val playerEntity = context.player
    val world = context.world
    if (!world.isClient && playerEntity != null) {
      val blockPos = context.blockPos
      if (!this.use(playerEntity, world.getBlockState(blockPos), world, blockPos, true, context.stack)) {
        return ActionResult.FAIL
      }
    }

    return ActionResult.success(world.isClient)
  }

  private fun use(player: PlayerEntity, state: BlockState, world: WorldAccess, pos: BlockPos, update: Boolean, stack: ItemStack): Boolean {
    val block = state.block
    if (block is StairsBlock) {
      val stateManager = block.stateManager
      val mode = stack.orCreateNbt.getString("Property")
      val property = stateManager.getProperty(mode) ?: properties.first()
      if (update) {
        val blockState = cycle(state, property, player.shouldCancelInteraction())
        world.setBlockState(pos, blockState, Block.FORCE_STATE or Block.NOTIFY_LISTENERS)
      } else {
        val newProperty = cycle(properties, property, player.shouldCancelInteraction())
        stack.orCreateNbt.putString("Property", newProperty.name)
        sendMessage(player as ServerPlayerEntity, translatable("$translationKey.mode", newProperty.name))
      }
      return true
    }
    return false
  }

  private fun <T: Comparable<T>>cycle(state: BlockState, property: Property<T>, inverse: Boolean): BlockState {
    return state.with(property, cycle(property.values, state.get(property), inverse))
  }

  private fun <T>cycle(elements: Iterable<T>, current: T, inverse: Boolean): T {
    return if (inverse) Util.previous(elements, current) else Util.next(elements, current)
  }


  private fun sendMessage(player: ServerPlayerEntity, message: Text) {
    player.sendMessageToClient(message, true)
  }
}
