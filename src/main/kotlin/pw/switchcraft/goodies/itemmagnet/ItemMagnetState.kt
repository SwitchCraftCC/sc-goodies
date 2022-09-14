package pw.switchcraft.goodies.itemmagnet

import dev.emi.trinkets.api.TrinketsApi
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import pw.switchcraft.goodies.Registration
import pw.switchcraft.goodies.itemmagnet.ItemMagnetItem.Companion.setStackEnabled
import pw.switchcraft.goodies.itemmagnet.ItemMagnetItem.Companion.stackEnabled
import pw.switchcraft.goodies.itemmagnet.ItemMagnetItem.Companion.stackRadius

object ItemMagnetState {
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
}
