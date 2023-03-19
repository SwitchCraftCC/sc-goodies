package io.sc3.goodies.enderstorage

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import io.sc3.goodies.ScGoodies.modId
import io.sc3.goodies.enderstorage.EnderStorageProvider.EnderStorageInventory
import io.sc3.goodies.util.parseDyeArg
import io.sc3.goodies.util.parseUserArg
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text.translatable

abstract class EnderStorageBaseCommand(val private: Boolean) : Command<ServerCommandSource> {
  fun getFrequency(ctx: CommandContext<ServerCommandSource>): Frequency {
    val user = if (private) parseUserArg(ctx, "user") else null
    return Frequency(
      owner     = user?.id,
      ownerName = user?.name,
      left      = parseDyeArg(ctx, "left"),
      middle    = parseDyeArg(ctx, "middle"),
      right     = parseDyeArg(ctx, "right")
    )
  }

  fun getInventory(ctx: CommandContext<ServerCommandSource>): Pair<EnderStorageInventory, Frequency> {
    val frequency = getFrequency(ctx)

    // Don't create the inventory if it doesn't exist, error instead
    val inv = EnderStorageProvider.getInventory(ctx.source.server, frequency, create = false)
      ?: throw NO_FREQUENCY.create()

    return inv to frequency
  }

  companion object {
    val NO_FREQUENCY = SimpleCommandExceptionType(translatable("block.$modId.ender_storage.not_found"))
  }
}
