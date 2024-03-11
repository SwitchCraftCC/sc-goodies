package io.sc3.goodies.client.enderstorage

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.text.Text

class EnderStorageNameWidget(
  private val tr: TextRenderer,
  x: Int,
  y: Int,
  val text: Text,
) : ClickableWidget(
  x,
  y,
  NAME_WIDTH,
  tr.fontHeight,
  text
) {
  private val textWidth = tr.getWidth(text).toFloat()
  private val scroller = ScrollingText(textWidth - NAME_WIDTH)

  override fun renderButton(ctx: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
    // If the text is too long, scroll it. Pause scrolling if the user is hovering over the widget
    if (textWidth > NAME_WIDTH && !isHovered) {
      scroller.update(delta)
    }

    // Draw the text, sliced to fit in the widget
    ctx.enableScissor(x, y, x + width, y + height)
    ctx.drawText(tr, text, x - scroller.pos.toInt(), y, 0xFFFFFF, false)
    ctx.disableScissor()
  }

  override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
    scroller.mouseScrolled(amount)
    return true
  }

  override fun appendClickableNarrations(builder: NarrationMessageBuilder) {}

  companion object {
    private const val NAME_WIDTH = 124
  }
}
