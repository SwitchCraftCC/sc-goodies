package io.sc3.goodies.ironstorage

import io.sc3.goodies.ScGoodies.modId
import io.sc3.goodies.util.BaseItem
import io.sc3.library.Tooltips.addDescLines
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.ActionResult
import net.minecraft.util.ActionResult.PASS
import net.minecraft.util.ActionResult.SUCCESS
import net.minecraft.util.Formatting.GRAY
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World

class IronStorageUpgradeItem(
  private val upgrade: IronStorageUpgrade,
  private val type: IronStorageUpgradeType,
  settings: Settings
) : BaseItem(settings) {
  private val from by upgrade::from
  private val to by upgrade::to

  private val tooltipExtra = listOf(translatable("block.$modId.storage.desc", to.size)
    .formatted(GRAY))

  /**
   * NB: World.canPlayerModifyAt and Player.isCreative checks are not necessary here as they are both done by
   * ServerPlayerInteractionManager.interactBlock, but if this is switched to another use method, then they must be
   * added accordingly.
   */
  override fun useOnBlock(ctx: ItemUsageContext): ActionResult {
    val world = ctx.world
    if (world.isClient) return PASS

    val pos = ctx.blockPos
    val be = world.getBlockEntity(pos) as? LootableContainerBlockEntity ?: return PASS

    val oldState = world.getBlockState(pos)
    val oldBlock = oldState.block
    val properties = type.getOldProperties(be, oldState, from)
    val customName = be.customName

    // Check if the upgrade is valid (vanilla -> iron, iron -> gold, gold -> diamond, and the correct block type)
    if (!type.isValidUpgrade(oldBlock, from, to)) return PASS

    // Don't upgrade if the chest is opened by any player
    if (type.getViewers(be) > 0) return PASS

    // Copy the items from the old inventory
    val size = be.size()
    val contents = DefaultedList.ofSize(size, ItemStack.EMPTY)
    for (i in 0 until size) {
      contents[i] = be.getStack(i)
    }

    // Remove the old inventory
    world.removeBlockEntity(pos)
    world.removeBlock(pos, false)

    // Create the new inventory
    val newBlock = type.getNewBlock(oldBlock, from, to)
    val newState = type.getNewState(newBlock, properties)
    world.setBlockState(pos, newState)

    val newBe = world.getBlockEntity(pos) as LootableContainerBlockEntity
    newBe.customName = customName

    // Copy the items to the new inventory
    for (i in 0 until size) {
      newBe.setStack(i, contents[i])
    }

    // Destroy the upgrade item
    ctx.stack.decrement(1)
    return SUCCESS
  }

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    // Don't call super, we don't want the default .desc implementation
    addDescLines(tooltip, getTranslationKey(stack), extraLines = tooltipExtra)
  }
}
