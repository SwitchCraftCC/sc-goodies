package io.sc3.goodies.client.enderstorage

import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.client.enderstorage.EnderStorageScreen.Companion.enderStorageTex
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text.translatable

class EnderStoragePersonalWidget(
  x: Int,
  y: Int,
  private val personal: Boolean,
  ownerName: String?
) : ButtonWidget(
  x,
  y,
  ICON_WIDTH,
  ICON_HEIGHT,
  ScreenTexts.EMPTY,
  { },
  DEFAULT_NARRATION_SUPPLIER
) {
  private val key = ModBlocks.enderStorage.translationKey
  private val text = if (personal) {
    translatable("${key}.personal", ownerName ?: "Unknown")
  } else {
    translatable("${key}.public")
  }

  init {
    tooltip = Tooltip.of(text)
  }

  override fun renderButton(ctx: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
    val iconX = if (personal) PERSONAL_ICON_X else PUBLIC_ICON_X
    ctx.drawTexture(enderStorageTex, x, y, iconX, 0, width, height)
  }

  override fun appendDefaultNarrations(builder: NarrationMessageBuilder) {
    builder.put(NarrationPart.TITLE, text)
  }

  companion object {
    private const val PUBLIC_ICON_X = 176
    private const val PERSONAL_ICON_X = 186

    private const val ICON_WIDTH = 10
    private const val ICON_HEIGHT = 10
  }
}
