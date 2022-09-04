package pw.switchcraft.goodies.client.enderstorage

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
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
    backgroundHeight = 148

    super.init()
  }

  override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader)
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, tex)

    // Use the top 3 rows from generic_54.png
    drawTexture(matrices, x, y, 0, 0, backgroundWidth, 71)
    drawTexture(matrices, x, y + 71, 0, 126, backgroundWidth, 96)
  }

  override fun drawForeground(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
    super.drawForeground(matrices, mouseX, mouseY)
    // TODO: Render the owner's name, frequency, and other info
  }

  override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
    renderBackground(matrices)
    super.render(matrices, mouseX, mouseY, delta)
    drawMouseoverTooltip(matrices, mouseX, mouseY)
  }

  companion object {
    val tex = Identifier("textures/gui/container/generic_54.png")
  }
}
