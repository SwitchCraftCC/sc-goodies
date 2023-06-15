package io.sc3.goodies.client.enderstorage

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.enderstorage.EnderStorageScreenHandler
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier

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

  override fun drawBackground(ctx: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
    ctx.drawTexture(tex, x, y, 0, 0, backgroundWidth, backgroundHeight)
  }

  override fun drawForeground(ctx: DrawContext, mouseX: Int, mouseY: Int) {
    super.drawForeground(ctx, mouseX, mouseY)

    val freq = handler.frequency

    drawFrequencyWool(ctx, 0, freq.left)
    drawFrequencyWool(ctx, 1, freq.middle)
    drawFrequencyWool(ctx, 2, freq.right)

    ctx.drawText(textRenderer, freq.ownerText, 30, 6, 0xFFFFFF, false)
  }

  override fun render(ctx: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
    renderBackground(ctx)
    super.render(ctx, mouseX, mouseY, delta)
    drawMouseoverTooltip(ctx, mouseX, mouseY)
  }

  private fun drawFrequencyWool(ctx: DrawContext, i: Int, color: DyeColor) {
    ctx.drawTexture(wool[color], 10 + (i * 6), 6, (i * 12.0f) / 16.0f, 0.0f, 4, 8, 16, 16)
  }

  companion object {
    private val tex = ModId("textures/gui/container/ender_storage.png")

    private val wool = DyeColor.values().associateWith {
      Identifier("textures/block/${it.getName()}_wool.png")
    }
  }
}
