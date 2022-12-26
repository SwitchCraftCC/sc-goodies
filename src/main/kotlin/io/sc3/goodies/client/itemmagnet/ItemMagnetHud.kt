package pw.switchcraft.goodies.client.itemmagnet

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Arm.LEFT
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.itemmagnet.ItemMagnetItem
import pw.switchcraft.goodies.itemmagnet.ItemMagnetState

object ItemMagnetHud {
  private val blockedTex = ModId("textures/gui/item_magnet_blocked.png")

  private val mc by lazy { MinecraftClient.getInstance() }
  private val itemRenderer by mc::itemRenderer
  private val textRenderer by mc::textRenderer

  private fun renderHud(matrices: MatrixStack, tickDelta: Float) {
    val player = mc.player ?: return
    if (player.isSpectator) return

    val magnet = ItemMagnetState.magnetComponents(player).firstOrNull() ?: return

    val x = when (player.mainArm) {
      LEFT -> mc.window.scaledWidth / 2 - 91 - 26
      else -> mc.window.scaledWidth / 2 + 91 + 10
    }
    val y = mc.window.scaledHeight - 19

    if (ItemMagnetItem.stackBlocked(magnet) && ItemMagnetItem.stackEnabled(magnet)) {
      // Blocked icon
      RenderSystem.setShader(GameRenderer::getPositionTexProgram)
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
      RenderSystem.setShaderTexture(0, blockedTex)
      DrawableHelper.drawTexture(matrices, x, y, 200, 0.0f, 0.0f, 16, 16, 16, 16)
      return
    }

    // Magnet item
    itemRenderer.renderInGuiWithOverrides(player, magnet, x, y, 0)
    // Durability bar
    itemRenderer.renderGuiItemOverlay(textRenderer, magnet, x, y, "")
  }

  internal fun initEvents() {
    HudRenderCallback.EVENT.register(::renderHud)
  }
}
