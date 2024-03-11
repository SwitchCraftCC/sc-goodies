package io.sc3.goodies.client.enderstorage

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.enderstorage.EnderStorageScreenHandler
import io.sc3.text.of
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.DyeColor
import net.minecraft.util.Identifier

private const val MAIN_BG_WIDTH = 176
private const val MAIN_BG_HEIGHT = 185

class EnderStorageScreen(
  handler: EnderStorageScreenHandler,
  playerInv: PlayerInventory,
  title: Text
) : HandledScreen<EnderStorageScreenHandler>(handler, playerInv, title) {
  private val tr by ::textRenderer

  private var nameWidget: EnderStorageNameWidget? = null
  private var personalWidget: EnderStoragePersonalWidget? = null
  private var descriptionWidget: EnderStorageDescriptionWidget? = null

  override fun init() {
    titleY = 23
    playerInventoryTitleY = MAIN_BG_HEIGHT - 94

    val freq = handler.frequency
    val state = handler.state

    val name = state.name?.let { of(it) } ?: freq.ownerText
    val description = state.description?.let { of(it) } ?: of("")

    addDrawableChild(EnderStorageNameWidget(tr, 30, 6, name)
      .also { nameWidget = it })
    addDrawableChild(EnderStoragePersonalWidget(157, 5, freq.personal, freq.ownerName)
      .also { personalWidget = it })
    addDrawableChild(EnderStorageDescriptionWidget(tr, 4, 4, description)
      .also { descriptionWidget = it })

    backgroundWidth = MAIN_BG_WIDTH
    backgroundHeight = MAIN_BG_HEIGHT + descriptionWidget!!.height

    super.init()
    updateWidgetPositions()
  }

  override fun resize(client: MinecraftClient?, width: Int, height: Int) {
    super.resize(client, width, height)
    updateWidgetPositions()
  }

  private fun updateWidgetPositions() {
    nameWidget?.setPosition(x + 30, y + 6)
    personalWidget?.setPosition(x + 157, y + 5)
    descriptionWidget?.setPosition(x + 4, y + MAIN_BG_HEIGHT)
  }

  override fun drawBackground(ctx: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
    ctx.drawTexture(enderStorageTex, x, y, 0, 0, MAIN_BG_WIDTH, MAIN_BG_HEIGHT)
  }

  override fun drawForeground(ctx: DrawContext, mouseX: Int, mouseY: Int) {
    super.drawForeground(ctx, mouseX, mouseY)

    val freq = handler.frequency

    drawFrequencyWool(ctx, 0, freq.left)
    drawFrequencyWool(ctx, 1, freq.middle)
    drawFrequencyWool(ctx, 2, freq.right)
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
    val enderStorageTex = ModId("textures/gui/container/ender_storage.png")

    private val wool = DyeColor.values().associateWith {
      Identifier("textures/block/${it.getName()}_wool.png")
    }
  }
}
