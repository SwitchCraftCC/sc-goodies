package pw.switchcraft.goodies.util

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.text.Text.literal
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting.*
import pw.switchcraft.goodies.ScGoodies.modId

object Tooltips {
  private val isClient by lazy {
    FabricLoader.getInstance().environmentType == EnvType.CLIENT
  }

  private val holdingShift
    get() = isClient && Screen.hasShiftDown()

  private val moreInfoText = translatable(
    "$modId.tooltip.more_info",
    translatable("key.keyboard.left.shift").formatted(WHITE)
  ).formatted(DARK_GRAY)

  fun addDescLines(tooltip: MutableList<Text>, baseKey: String, suffix: String = ".desc") {
    val baseText = translatable("$baseKey$suffix")
    val lines = baseText.string.split("\n")
      .map { literal(it.trim()).formatted(GRAY) }

    if (lines.size > 1 && isClient) {
      // Only show the first line if the player isn't holding shift
      if (!holdingShift) {
        tooltip.add(lines.first())
        tooltip.add(moreInfoText)
        return
      }
    }

    // Otherwise, show everything
    tooltip.addAll(lines)
  }
}
