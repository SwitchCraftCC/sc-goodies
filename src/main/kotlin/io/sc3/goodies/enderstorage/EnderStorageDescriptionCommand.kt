package io.sc3.goodies.enderstorage

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import io.sc3.goodies.enderstorage.EnderStorageTargetType.*
import io.sc3.text.of
import io.sc3.text.plus
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting.GREEN
import net.minecraft.util.Formatting.YELLOW

class EnderStorageDescriptionCommand(
  target: EnderStorageTargetType,
  private val clear: Boolean = false
) : EnderStorageBaseCommand(target) {
  override fun run(ctx: CommandContext<ServerCommandSource>): Int {
    val (state, freq) = getState(ctx)
    val desc = if (!clear) getString(ctx, "description") else null

    if (!clear && !FrequencyState.isValidDescription(desc)) {
      throw INVALID_DESCRIPTION.create()
    }

    state.description = desc
    EnderStorageProvider.state.markDirty()

    ctx.source.sendFeedback({
      val base = of("", GREEN)
      val freqText = freq.toTextParts(YELLOW)
      val descText = if (!clear) of(desc, YELLOW) else null

      if (clear) {
        when (target) {
          OWN -> base + translatable("$TL_KEY.cleared_description_own", *freqText)
          PUBLIC -> base + translatable("$TL_KEY.cleared_description_public", *freqText)
          PRIVATE -> base + translatable("$TL_KEY.cleared_description_private",
            of(freq.ownerName ?: freq.owner?.toString() ?: "Unknown", YELLOW), *freqText)
        }
      } else {
        when (target) {
          OWN -> base + translatable("$TL_KEY.changed_description_own", *freqText, descText)
          PUBLIC -> base + translatable("$TL_KEY.changed_description_public", *freqText, descText)
          PRIVATE -> base + translatable("$TL_KEY.changed_description_private",
            of(freq.ownerName ?: freq.owner?.toString() ?: "Unknown", YELLOW), *freqText, descText)
        }
      }
    }, true)

    return SINGLE_SUCCESS
  }

  companion object {
    val INVALID_DESCRIPTION = SimpleCommandExceptionType(translatable("$TL_KEY.invalid_description"))
  }
}
