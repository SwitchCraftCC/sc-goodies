package pw.switchcraft.goodies.itemmagnet

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketItem
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.world.World
import pw.switchcraft.goodies.util.Tooltips

private const val TICK_FREQ = 3
private const val MAX_RANGE = 6.0
private const val MIN_RANGE = 0.5
private const val SPEED = 0.75

class ItemMagnetItem(settings: Settings) : TrinketItem(settings) {
  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    super.appendTooltip(stack, world, tooltip, context)
    Tooltips.addDescLines(tooltip, getTranslationKey(stack))
  }

  override fun tick(magnetStack: ItemStack, slot: SlotReference, player: LivingEntity) {
    // TODO: Check enabled state from magnetStack

    // Only run on the server and once every four ticks. Give each player their own tick offset so that the load is
    // spread out a bit.
    val tickOffset = player.uuid.hashCode() % TICK_FREQ
    val world = player.world
    if (world.isClient || world.server!!.ticks % TICK_FREQ != tickOffset || player !is ServerPlayerEntity) {
      return
    }

    val range = player.boundingBox.expand(MAX_RANGE)
    val items = world.getEntitiesByClass(ItemEntity::class.java, range) { true }

    for (item in items) {
      // Ensure the item can actually be picked up (pickup delay)
      val stack = item.stack
      if (item.cannotPickup() || stack.isEmpty) {
        continue
      }

      // Ensure there is space in the inventory for the item
      if (!player.inventory.containsAny { it.isEmpty || it.isItemEqual(stack) && it.count < it.maxCount }) {
        continue
      }

      // Zwoop the item towards the player
      val distance = item.distanceTo(player)
      if (distance > MIN_RANGE) {
        val vector = player.eyePos.subtract(item.pos).normalize().multiply(SPEED)
        item.velocity = vector
        item.velocityDirty = true
      }
    }
  }
}
