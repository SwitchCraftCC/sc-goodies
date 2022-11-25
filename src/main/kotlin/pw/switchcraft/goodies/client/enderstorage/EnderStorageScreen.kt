package pw.switchcraft.goodies.client.enderstorage

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.enderstorage.EnderStorageScreenHandler

class EnderStorageScreen(
  handler: EnderStorageScreenHandler,
  playerInv: PlayerInventory,
  title: Text
) : HandledScreen<EnderStorageScreenHandler>(handler, playerInv, title) {
  override fun init() {
    // It would really be nice to just extend GenericContainerScreen, but it doesn't expose a type parameter, so we
    // can't really pass it to HandledScreens.register :(
    backgroundWidth = 176
    backgroundHeight = 185

    titleY = 23
    playerInventoryTitleY = backgroundHeight - 94

    super.init()
  }

  override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
    RenderSystem.setShader(GameRenderer::getPositionTexProgram)
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, tex)

    drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight)
  }

  override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
    super.drawForeground(matrices, mouseX, mouseY)

    val freq = handler.frequency

    drawFrequencyWool(matrices, 0, freq.left)
    drawFrequencyWool(matrices, 1, freq.middle)
    drawFrequencyWool(matrices, 2, freq.right)

    textRenderer.draw(matrices, freq.ownerText, 30.0f, 6.0f, 0xFFFFFF)
  }

  override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
    renderBackground(matrices)
    super.render(matrices, mouseX, mouseY, delta)
    drawMouseoverTooltip(matrices, mouseX, mouseY)
  }

  private fun drawFrequencyWool(matrices: MatrixStack, i: Int, color: DyeColor) {
    RenderSystem.setShader(GameRenderer::getPositionTexProgram)
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, wool[color])

    drawTexture(matrices, 10 + (i * 6), 6, (i * 12.0f) / 16.0f, 0.0f, 4, 8, 16, 16)
  }

  companion object {
    private val tex = ModId("textures/gui/container/ender_storage.png")

    private val wool = DyeColor.values().associateWith {
      Identifier("textures/block/${it.getName()}_wool.png")
    }
  }
}
