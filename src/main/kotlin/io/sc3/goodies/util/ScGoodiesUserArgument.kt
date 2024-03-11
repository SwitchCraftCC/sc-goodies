package io.sc3.goodies.util

import com.mojang.authlib.GameProfile
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.GameProfileArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import java.util.*

fun userArg(name: String): RequiredArgumentBuilder<ServerCommandSource, String>
  = argument(name, word())
  .suggests { ctx, builder ->
    val names = ctx.source.server.userCache?.byName?.keys ?: emptySet()
    CommandSource.suggestMatching(names, builder)
    builder.buildFuture()
  }

fun parseUserArg(ctx: CommandContext<ServerCommandSource>, name: String): GameProfile {
  val cache = ctx.source.server.userCache ?: throw IllegalStateException("User cache not available")
  val query = getString(ctx, name)

  // Try matching a UUID first, otherwise resolve the query as a username
  val profile = tryParseUuid(query)?.let { cache.getByUuid(it) } ?: cache.findByName(query)
  return profile.orElseThrow { GameProfileArgumentType.UNKNOWN_PLAYER_EXCEPTION.create() }
}

private fun tryParseUuid(string: String): UUID? = try {
  UUID.fromString(string)
} catch (e: IllegalArgumentException) {
  null
}
