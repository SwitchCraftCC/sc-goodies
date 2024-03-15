package io.sc3.goodies.client.itemmagnet

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.itemmagnet.ItemMagnetItem
import io.sc3.goodies.itemmagnet.ItemMagnetState
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Arm.LEFT

object ItemMagnetHud {
  private val blockedTex = ModId("textures/gui/item_magnet_blocked.png")

  private val mc by lazy { MinecraftClient.getInstance() }
  private val textRenderer by mc::textRenderer

  private fun renderHud(ctx: DrawContext, tickDelta: Float) {
    val player = mc.player ?: return
    if (player.isSpectator || mc.options.hudHidden) return

    val magnet = ItemMagnetState.magnetComponents(player).firstOrNull() ?: return

    val x = when (player.mainArm) {
      LEFT -> mc.window.scaledWidth / 2 - 91 - 26
      else -> mc.window.scaledWidth / 2 + 91 + 10
    }
    val y = mc.window.scaledHeight - 19

    if (ItemMagnetItem.stackBlocked(magnet) != null && ItemMagnetItem.stackEnabled(magnet)) {
      // Blocked icon
      ctx.drawTexture(blockedTex, x, y, 200, 0.0f, 0.0f, 16, 16, 16, 16)
      return
    }

    // Magnet item
    ctx.drawItem(player, magnet, x, y, 0)
    // Durability bar
    ctx.drawItemInSlot(textRenderer, magnet, x, y, "")
  }

  internal fun initEvents() {
    HudRenderCallback.EVENT.register(::renderHud)
  }
}
