package pw.switchcraft.goodies.util

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.ColorArgumentType
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.DyeColor

fun dyeArg(name: String): RequiredArgumentBuilder<ServerCommandSource, String>
  = argument(name, word())
  .suggests { _, builder ->
    CommandSource.suggestMatching(DyeColor.values().map { it.getName() }, builder)
    builder.buildFuture()
  }

fun parseDyeArg(ctx: CommandContext<ServerCommandSource>, name: String): DyeColor {
  val color = StringArgumentType.getString(ctx, name)
  return DyeColor.byName(color, null) ?: throw ColorArgumentType.INVALID_COLOR_EXCEPTION.create(color)
}
