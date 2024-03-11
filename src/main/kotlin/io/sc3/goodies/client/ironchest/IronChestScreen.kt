package io.sc3.goodies.client.ironchest

import io.sc3.goodies.ironstorage.IronChestScreenHandler
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

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

  override fun drawBackground(ctx: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
    ctx.drawTexture(variant.screenTex, x, y, 0, 0, backgroundWidth, backgroundHeight)
  }

  override fun drawForeground(ctx: DrawContext, mouseX: Int, mouseY: Int) {
    // No-op: don't draw the container title/inventory title, we don't have space!
  }

  override fun render(ctx: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
    renderBackground(ctx)
    super.render(ctx, mouseX, mouseY, delta)
    drawMouseoverTooltip(ctx, mouseX, mouseY)
  }
}
