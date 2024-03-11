package io.sc3.goodies.enderstorage

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import io.sc3.goodies.ScGoodies.modId
import io.sc3.goodies.enderstorage.EnderStorageProvider.EnderStorageInventory
import io.sc3.goodies.enderstorage.EnderStorageTargetType.OWN
import io.sc3.goodies.enderstorage.EnderStorageTargetType.PRIVATE
import io.sc3.goodies.util.parseDyeArg
import io.sc3.goodies.util.parseUserArg
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text.translatable

enum class EnderStorageTargetType {
  OWN,
  PUBLIC,
  PRIVATE
}

abstract class EnderStorageBaseCommand(
  val target: EnderStorageTargetType
) : Command<ServerCommandSource> {
  private fun getFrequency(ctx: CommandContext<ServerCommandSource>): Frequency {
    val user = when (target) {
      OWN     -> ctx.source.playerOrThrow.gameProfile
      PRIVATE -> parseUserArg(ctx, "user")
      else    -> null
    }

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

  fun getState(ctx: CommandContext<ServerCommandSource>): Pair<FrequencyState, Frequency> {
    // Use getInventory to enforce the frequency's existence - don't allow players to populate descriptions for
    // non-existent frequencies
    val frequency = getInventory(ctx).second
    return EnderStorageProvider.getState(frequency) to frequency
  }

  companion object {
    @JvmStatic
    protected val TL_KEY = "block.$modId.ender_storage"

    val NO_FREQUENCY = SimpleCommandExceptionType(translatable("$TL_KEY.not_found"))
  }
}
