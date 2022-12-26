package io.sc3.goodies.itemmagnet

import dev.emi.trinkets.api.TrinketsApi
import net.minecraft.entity.ExperienceOrbEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import io.sc3.goodies.Registration
import io.sc3.goodies.itemmagnet.ItemMagnetItem.Companion.setStackEnabled
import io.sc3.goodies.itemmagnet.ItemMagnetItem.Companion.stackEnabled
import io.sc3.goodies.itemmagnet.ItemMagnetItem.Companion.stackRadius
import java.util.*
import kotlin.math.min

object ItemMagnetState {
  // Item entities that have been 'magnetized'
  private val magnetizedEntities: MutableMap<ItemEntity, MagnetizedState> = WeakHashMap()

  fun magnetComponents(player: LivingEntity): List<ItemStack> {
    // Get all magnets (even though there should only be one)
    val trinket = TrinketsApi.getTrinketComponent(player).orElse(null) ?: return emptyList()
    return trinket.getEquipped(Registration.ModItems.itemMagnet)
      .map { it.right }
      .toList()
  }

  private fun playerMagnetEnabled(components: List<ItemStack>)
    = components.any { stackEnabled(it) }

  fun playerMagnetRadius(components: List<ItemStack>)
    = components.maxOfOrNull { stackRadius(it) }

  fun onPlayerToggle(player: LivingEntity) {
    val components = magnetComponents(player)
    val enabled = !playerMagnetEnabled(components)
    components.forEach { setStackEnabled(it, enabled) }
  }

  fun magnetizeItem(item: ItemEntity, player: ServerPlayerEntity) {
    val state = magnetizedEntities.computeIfAbsent(item) { MagnetizedState() }
    state.ageLastMagnetized = item.itemAge
    state.playerLastMagnetized = player.uuid
  }

  @JvmStatic
  fun pickupItem(item: ItemEntity, player: PlayerEntity, beforeCount: Int) {
    // Don't worry if the item wasn't magnetized, just a normal pickup
    if (player !is ServerPlayerEntity) return
    val state = magnetizedEntities[item] ?: return

    // If the item was last magnetized more than 5 seconds ago, don't damage any magnets
    if (item.itemAge - state.ageLastMagnetized > 100) return

    // If the item was picked up by the same player that last magnetized it, and it was recent enough, damage their
    //   magnet by the stack count. At this point player.getInventory().insertStack() has already been called, so the
    //   stack count has been decreased. The amount to damage by is `i - stack.count`.
    // Note that it is okay if the player picks up more items than their magnet's damage can handle. Not the end of the
    //   world if they get some number less than 64 for free.
    val count = beforeCount - item.stack.count

    // Find the player that magnetized the item and damage it (rather than the player that picked it up)
    val magnetizer = player.server.playerManager.getPlayer(state.playerLastMagnetized) ?: return
    if (magnetizer.isCreative || magnetizer.isSpectator) return

    with (magnetComponents(magnetizer).first()) {
      // Use setDamage instead of damage, so that the magnet doesn't get broken if it reaches maxDamage
      damage = min(maxDamage, damage + count)
    }

    magnetizedEntities.remove(item)
  }

  @JvmStatic
  fun consumeXpOrb(xpOrb: ExperienceOrbEntity, player: PlayerEntity, amount: Int): Int {
    val magnet = magnetComponents(player).firstOrNull() ?: return amount
    val repairAmount = min(magnetRepairAmount(xpOrb.experienceAmount), magnet.damage)
    magnet.damage -= repairAmount
    return amount - magnetRepairCost(repairAmount) // Looping handled by ExperienceOrbEntityMixin
  }

  private fun magnetRepairAmount(xpAmount: Int) = xpAmount * MAGNET_XP_MULTIPLIER
  private fun magnetRepairCost(repairAmount: Int) = repairAmount / MAGNET_XP_MULTIPLIER

  class MagnetizedState {
    var ageLastMagnetized = 0
    // Player may die or disconnect during magnetization, and is not necessarily the same player that will pick it up
    var playerLastMagnetized: UUID? = null
  }
}
