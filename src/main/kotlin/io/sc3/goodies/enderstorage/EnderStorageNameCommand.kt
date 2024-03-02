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

class EnderStorageNameCommand(target: EnderStorageTargetType) : EnderStorageBaseCommand(target) {
  override fun run(ctx: CommandContext<ServerCommandSource>): Int {
    val (state, freq) = getState(ctx)
    val name = getString(ctx, "name")

    if (!FrequencyState.isValidName(name)) {
      throw INVALID_NAME.create()
    }

    state.name = name
    EnderStorageProvider.state.markDirty()

    ctx.source.sendFeedback({
      val base = of("", GREEN)
      val freqText = freq.toTextParts(YELLOW)
      val nameText = of(name, YELLOW)
      when (target) {
        OWN -> base + translatable("$TL_KEY.changed_name_own", *freqText, nameText)
        PUBLIC -> base + translatable("$TL_KEY.changed_name_public", *freqText, nameText)
        PRIVATE -> base + translatable("$TL_KEY.changed_name_private",
          of(freq.ownerName ?: freq.owner?.toString() ?: "Unknown", YELLOW), *freqText, nameText)
      }
    }, true)

    return SINGLE_SUCCESS
  }

  companion object {
    val INVALID_NAME = SimpleCommandExceptionType(translatable("$TL_KEY.invalid_name"))
  }
}
