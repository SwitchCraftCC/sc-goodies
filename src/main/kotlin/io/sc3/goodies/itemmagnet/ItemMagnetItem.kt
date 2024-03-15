package io.sc3.goodies.itemmagnet

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketItem
import dev.emi.trinkets.api.TrinketsApi
import io.sc3.goodies.itemmagnet.ItemMagnetHotkey.toggleBinding
import io.sc3.goodies.itemmagnet.ItemMagnetState.playerMagnetRadius
import io.sc3.library.Tooltips
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting.*
import net.minecraft.world.World
import kotlin.math.absoluteValue

private const val TICK_FREQ = 3
private const val MIN_VACUUM_RANGE = 0.5
private const val SPEED = 0.75

private const val MIN_RANGE = 3
private const val MAX_RANGE = 6
private const val EXTRA_DISABLE_RANGE = 2

const val MAGNET_MAX_DAMAGE = 6912
const val MAGNET_XP_MULTIPLIER = 16

class ItemMagnetItem(settings: Settings) : TrinketItem(settings) {
  override fun hasGlint(stack: ItemStack) = stackEnabled(stack)

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    super.appendTooltip(stack, world, tooltip, context)
    // Show the level and enabled status before the description.

    // Only show the enabled status if it is equipped in the trinket slot.
    val enabled = stackEnabled(stack)
    if (world?.isClient == true) {
      addEnabledTooltip(stack, tooltip, enabled)

      if (stackBlocked(stack)) {
        tooltip.add(translatable("$translationKey.blocked").formatted(RED))
      }
    }

    val level = stackLevel(stack)
    if (level >= 0) {
      tooltip.add(translatable("$translationKey.level", level, radius(level)))
    }

    Tooltips.addDescLines(tooltip, getTranslationKey(stack))
  }

  private fun addEnabledTooltip(stack: ItemStack, tooltip: MutableList<Text>, enabled: Boolean) {
    val player = MinecraftClient.getInstance().player ?: return
    val trinket = TrinketsApi.getTrinketComponent(player).orElse(null) ?: return
    if (!trinket.isEquipped { it == stack }) return

    val key = if (enabled) "enabled" else "disabled"
    val color = if (enabled) GREEN else RED
    val keybinding = toggleBinding.boundKeyLocalizedText.copy().formatted(WHITE)
    tooltip.add(translatable("$translationKey.$key", keybinding).formatted(color))
  }

  override fun tick(magnetStack: ItemStack, slot: SlotReference, player: LivingEntity) {
    val radius = stackRadius(magnetStack)
    val damage = magnetStack.damage
    val maxDamage = magnetStack.maxDamage

    // Only run on the server and once every three ticks. Give each player their own tick offset so that the load is
    // spread out a bit.
    val tickOffset = player.uuid.hashCode().absoluteValue % TICK_FREQ
    val world = player.world
    if (world.isClient || world.server!!.ticks % TICK_FREQ != tickOffset || player !is ServerPlayerEntity) {
      return
    }

    val range = player.boundingBox.expand(radius.toDouble())
    val extraDisableRange = range.expand(EXTRA_DISABLE_RANGE.toDouble())

    // Look for other players with magnets in the range in case they will conflict
    val otherPlayers = world.getEntitiesByClass(ServerPlayerEntity::class.java, extraDisableRange) {
      it != player && it.isAlive && !it.isSpectator
    }
    val blocked = player.isSpectator || otherPlayers.any {
      val components = ItemMagnetState.magnetComponents(it)
      val otherRadius = playerMagnetRadius(components) ?: return@any false
      val otherRange = it.boundingBox.expand(otherRadius.toDouble() + EXTRA_DISABLE_RANGE.toDouble())
      extraDisableRange.intersects(otherRange)
    }

    // If there are conflicts, disable the magnet
    if (blocked != stackBlocked(magnetStack)) {
      setStackBlocked(magnetStack, blocked)
      if (blocked) return
    }

    // Do nothing if the magnet is disabled, out of charge, or we are in spectator mode
    if (!stackEnabled(magnetStack) || damage >= maxDamage || player.isSpectator) {
      return
    }

    // Look for items to pick up
    val items = world.getEntitiesByClass(ItemEntity::class.java, range) { true }
    for (item in items) {
      // Ensure the item can actually be picked up (pickup delay)
      val stack = item.stack
      if (item.cannotPickup() || stack.isEmpty) {
        continue
      }

      // Ensure there is space in the inventory for the item
      if (player.inventory.main.none { it.isEmpty || ItemStack.canCombine(it, stack) && it.count < it.maxCount }) {
        continue
      }

      // Zwoop the item towards the player
      val distance = item.distanceTo(player)
      if (distance > MIN_VACUUM_RANGE) {
        val vector = player.eyePos.subtract(item.pos).normalize().multiply(SPEED)
        item.velocity = vector
        item.velocityDirty = true
        ItemMagnetState.magnetizeItem(item, player)
      }
    }
  }

  companion object {
    fun stackLevel(stack: ItemStack): Int {
      if (stack.isEmpty) return 0
      return stack.orCreateNbt.getInt("level")
    }

    fun radius(level: Int): Int
      = (MIN_RANGE + level).coerceAtMost(MAX_RANGE)
    fun stackRadius(stack: ItemStack): Int = radius(stackLevel(stack))

    fun stackEnabled(stack: ItemStack): Boolean =
      !stack.orCreateNbt.getBoolean("disabled")
    fun setStackEnabled(stack: ItemStack, enabled: Boolean) =
      stack.orCreateNbt.putBoolean("disabled", !enabled)

    fun stackBlocked(stack: ItemStack): Boolean =
      stack.orCreateNbt.getBoolean("blocked")
    fun setStackBlocked(stack: ItemStack, blocked: Boolean) =
      stack.orCreateNbt.putBoolean("blocked", blocked)
  }
}
