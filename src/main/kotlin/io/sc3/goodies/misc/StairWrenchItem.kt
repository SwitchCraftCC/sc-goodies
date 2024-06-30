package io.sc3.goodies.misc

import io.sc3.goodies.util.BaseItem
import io.sc3.text.color
import net.minecraft.block.Block.FORCE_STATE
import net.minecraft.block.Block.NOTIFY_LISTENERS
import net.minecraft.block.BlockState
import net.minecraft.block.StairsBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.registry.tag.BlockTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.property.Property
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class StairWrenchItem(settings: Settings) : BaseItem(settings) {
  private var properties = listOf(StairsBlock.FACING, StairsBlock.HALF, StairsBlock.SHAPE)

  override fun canMine(state: BlockState, world: World, pos: BlockPos, miner: PlayerEntity): Boolean {
    if (!world.isClient) {
      crank(miner, state, world, pos, false, miner.getStackInHand(Hand.MAIN_HAND))
    }

    return false
  }

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    var mode = stack.orCreateNbt.getString("Property")
    if (mode.isEmpty()) {
      // Fresh item (crafting result), REI, etc. Show the first property as the default mode
      stack.nbt?.putString("Property", StairsBlock.FACING.name)
      mode = StairsBlock.FACING.name
    }

    tooltip.add(translatable("$translationKey.mode", mode).color(Formatting.GRAY))
    super.appendTooltip(stack, world, tooltip, context)
  }

  override fun useOnBlock(context: ItemUsageContext): ActionResult {
    val player = context.player
    val world = context.world
    val pos = context.blockPos

    if (
      !world.isClient
      && player != null
      && !crank(player, world.getBlockState(pos), world, pos, update = true, context.stack)
    ) {
      return ActionResult.FAIL
    }

    return ActionResult.success(world.isClient)
  }

  private fun crank(player: PlayerEntity, state: BlockState, world: WorldAccess, pos: BlockPos, update: Boolean,
                    stack: ItemStack): Boolean {
    // TODO: Allow this to work with some other types of blocks
    if (!state.isIn(BlockTags.STAIRS)) return false

    val mode = stack.orCreateNbt.getString("Property")
    val property = state.block.stateManager.getProperty(mode)
    if (property == null || !properties.contains(property)) {
      return false // Invalid mode (target block doesn't have the property)
    }

    if (update) {
      // Update the stair block in the world
      val newProperty = cycle(state, property, player.shouldCancelInteraction())
      world.setBlockState(pos, newProperty, FORCE_STATE or NOTIFY_LISTENERS)
    } else {
      // Cycle the property mode on the item and save it
      val newProperty = cycle(properties, property, player.shouldCancelInteraction())
      stack.orCreateNbt.putString("Property", newProperty.name)

      // Inform the player of the new mode in the hotbar (only serverside)
      (player as? ServerPlayerEntity)
        ?.sendMessageToClient(translatable("$translationKey.mode", newProperty.name), true)
    }

    return true
  }

  private fun <T: Comparable<T>>cycle(state: BlockState, property: Property<T>, inverse: Boolean) =
    state.with(property, cycle(property.values, state.get(property), inverse))

  @Suppress("UNCHECKED_CAST")
  private fun <T>cycle(elements: Iterable<T>, current: T, inverse: Boolean): T =
    if (current is Direction) {
      current.rotateYClockwise() as T // Directions are backwards (NSEW) so cycle them clockwise instead
    } else if (inverse) {
      Util.previous(elements, current)
    } else {
      Util.next(elements, current)
    }
}
