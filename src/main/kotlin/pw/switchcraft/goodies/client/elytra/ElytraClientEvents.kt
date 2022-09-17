package pw.switchcraft.goodies.client.elytra

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents
import net.minecraft.entity.EquipmentSlot
import pw.switchcraft.goodies.elytra.BaseElytraItem

object ElytraClientEvents {
  internal fun initEvents() {
    LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register { player ->
      val equipped = player.getEquippedStack(EquipmentSlot.CHEST)
      equipped.item !is BaseElytraItem
    }
  }
}
