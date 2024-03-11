package io.sc3.goodies.client.enderstorage

import io.sc3.goodies.client.enderstorage.EnderStorageScreen.Companion.enderStorageTex
import net.minecraft.client.font.MultilineText
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text

class EnderStorageDescriptionWidget(
  private val tr: TextRenderer,
  x: Int,
  y: Int,
  val text: Text,
  private val multilineText: MultilineText = MultilineText.create(tr, text, TEXT_WIDTH),
) : ClickableWidget(
  x,
  y,
  BAR_WIDTH,
  widgetHeight(tr, multilineText.count()),
  text
) {
  private val scrollHeight = tr.fontHeight * (multilineText.count() - MAX_LINES)
  private val scroller = ScrollingText(scrollHeight.toFloat())

  override fun renderButton(ctx: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
    // If there's no text, don't render anything
    if (multilineText.count() == 0) return

    // If the text is too long, scroll it. Pause scrolling if the user is hovering over the widget
    if (multilineText.count() > MAX_LINES && !isHovered) {
      scroller.update(delta)
    }

    // Draw the background
    ctx.drawNineSlicedTexture(
      enderStorageTex,
      x, y,
      width, height,
      SLICE_SIZE, SLICE_SIZE,
      BAR_WIDTH, BAR_HEIGHT,
      4, 185
    )

    // Draw the text, sliced to fit in the widget
    ctx.enableScissor(x + 7, y, x + 7 + TEXT_WIDTH, y + height - SLICE_SIZE)
    multilineText.draw(
      ctx,
      x + 7,
      y + SLICE_SIZE - scroller.pos.toInt(),
      tr.fontHeight,
      0xFFFFFF
    )
    ctx.disableScissor()
  }

  override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
    scroller.mouseScrolled(amount)
    return true
  }

  override fun appendClickableNarrations(builder: NarrationMessageBuilder) {}

  companion object {
    private const val BAR_WIDTH = 168
    private const val BAR_HEIGHT = 17
    private const val SLICE_SIZE = 4

    private const val TEXT_WIDTH = 156
    private const val MAX_LINES = 3

    private fun widgetHeight(tr: TextRenderer, lines: Int) = if (lines > 0) {
      (tr.fontHeight * lines.coerceAtMost(MAX_LINES)) + (SLICE_SIZE * 2)
    } else {
      0
    }
  }
}
