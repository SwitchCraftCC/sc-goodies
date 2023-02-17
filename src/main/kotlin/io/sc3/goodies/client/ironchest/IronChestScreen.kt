package io.sc3.goodies.client.ironchest

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import io.sc3.goodies.ironstorage.IronChestScreenHandler

class IronChestScreen(
  handler: IronChestScreenHandler,
  playerInv: PlayerInventory,
  title: Text
) : HandledScreen<IronChestScreenHandler>(handler, playerInv, title) {
  private val variant by handler::variant

  override fun init() {
    backgroundWidth = 14 + (variant.columns * 18)
    backgroundHeight = 94 + (variant.rows * 18)

    super.init()
  }

  override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
    RenderSystem.setShader(GameRenderer::getPositionTexProgram)
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, variant.screenTex)

    drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight)
  }

  override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
    // No-op: don't draw the container title/inventory title, we don't have space!
  }

  override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
    renderBackground(matrices)
    super.render(matrices, mouseX, mouseY, delta)
    drawMouseoverTooltip(matrices, mouseX, mouseY)
  }
}
