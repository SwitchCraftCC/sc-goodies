package pw.switchcraft.goodies.elytra

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents
import net.minecraft.entity.EquipmentSlot

object ElytraEvents {
  internal fun initEvents() {
    LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register { player ->
      val equipped = player.getEquippedStack(EquipmentSlot.CHEST)
      equipped.item !is BaseElytraItem
    }
  }
}
