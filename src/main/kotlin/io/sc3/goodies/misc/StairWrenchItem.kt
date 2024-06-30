package io.sc3.goodies.misc

import io.sc3.goodies.util.BaseItem
import io.sc3.library.ext.optString
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
import net.minecraft.util.Formatting.GRAY
import net.minecraft.util.Formatting.RED
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class StairWrenchItem(settings: Settings) : BaseItem(settings) {
  private val properties = listOf(StairsBlock.FACING, StairsBlock.HALF, StairsBlock.SHAPE)
  private val fallbackProperty = properties.first()

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    // For a fresh item (crafting result, REI, etc.), show the first property as the default mode
    val mode = stack.orCreateNbt.optString("Property") ?: fallbackProperty.name
    tooltip.add(translatable("$translationKey.mode", mode).color(GRAY))
    super.appendTooltip(stack, world, tooltip, context)
  }

  override fun useOnBlock(context: ItemUsageContext): ActionResult {
    val player = context.player
    val world = context.world
    val pos = context.blockPos

    if (
      !world.isClient
      && player != null
      // Shift-right-click to cycle the property mode, right-click to rotate the block
      && !crank(player, world.getBlockState(pos), world, pos, update = !player.isSneaking, context.stack)
    ) {
      return ActionResult.FAIL
    }

    return ActionResult.success(world.isClient)
  }

  private fun crank(player: PlayerEntity, state: BlockState, world: WorldAccess, pos: BlockPos, update: Boolean,
                    stack: ItemStack): Boolean {
    // TODO: Allow this to work with some other types of blocks
    if (!state.isIn(BlockTags.STAIRS)) return false

    val mode = stack.orCreateNbt.optString("Property") ?: fallbackProperty.name
    val property = mode?.let { state.block.stateManager.getProperty(mode) }
    if (property == null || !properties.contains(property)) {
      // Send an 'Invalid property' message to real players (but not fake players, e.g. turtles)
      if (player::class.java == ServerPlayerEntity::class.java) {
        player.sendMessage(translatable("$translationKey.invalid", mode).color(RED), true)
      }

      return false // Invalid mode (target block doesn't have the property)
    }

    if (update) {
      // Update the stair block in the world
      val newProperty = cycle(state, property)
      world.setBlockState(pos, newProperty, FORCE_STATE or NOTIFY_LISTENERS)
    } else {
      // Shift-right-click - cycle the property mode and save it to the item
      val newProperty = cycle(properties, property)
      stack.orCreateNbt.putString("Property", newProperty.name)

      // Inform the player of the new mode in the hotbar (only serverside)
      (player as? ServerPlayerEntity)
        ?.sendMessageToClient(translatable("$translationKey.mode", newProperty.name), true)
    }

    return true
  }

  private fun <T: Comparable<T>>cycle(state: BlockState, property: Property<T>) =
    state.with(property, cycle(property.values, state.get(property)))

  @Suppress("UNCHECKED_CAST")
  private fun <T>cycle(elements: Iterable<T>, current: T): T =
    if (current is Direction) {
      current.rotateYClockwise() as T // Directions are backwards (NSEW) so cycle them clockwise instead
    } else {
      Util.next(elements, current)
    }
}
