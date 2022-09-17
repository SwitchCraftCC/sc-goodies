package pw.switchcraft.goodies.enderstorage

import com.mojang.brigadier.Command
import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.math.BlockPos
import pw.switchcraft.goodies.ScGoodies.modId
import pw.switchcraft.goodies.util.dyeArg
import pw.switchcraft.goodies.util.parseDyeArg
import pw.switchcraft.goodies.util.parseUserArg
import pw.switchcraft.goodies.util.userArg

val NO_FREQUENCY = SimpleCommandExceptionType(translatable("block.$modId.ender_storage.not_found"))

class EnderStorageCommand(val private: Boolean) : Command<ServerCommandSource> {
  override fun run(ctx: CommandContext<ServerCommandSource>): Int {
    val player = ctx.source.playerOrThrow
    val user = if (private) parseUserArg(ctx, "user") else null
    val frequency = Frequency(
      owner     = user?.id,
      ownerName = user?.name,
      left      = parseDyeArg(ctx, "left"),
      middle    = parseDyeArg(ctx, "middle"),
      right     = parseDyeArg(ctx, "right")
    )

    // Don't create the inventory if it doesn't exist, error instead
    val inv = EnderStorageProvider.getInventory(player.server, frequency, create = false)
      ?: throw NO_FREQUENCY.create()

    player.openHandledScreen(object : ExtendedScreenHandlerFactory {
      // Don't add viewingPlayers here
      override fun createMenu(syncId: Int, playerInv: PlayerInventory, player: PlayerEntity): ScreenHandler =
        EnderStorageScreenHandler(syncId, playerInv, inv, BlockPos.ORIGIN, frequency)

      override fun getDisplayName(): Text = translatable("block.$modId.ender_storage")

      override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeBlockPos(BlockPos.ORIGIN)
        frequency.toPacket(buf)
      }
    })

    return SINGLE_SUCCESS
  }
}

object EnderStorageCommands {
  internal fun register() {
    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
      dispatcher.register(literal("enderstorage")
        .then(literal("public")
          .requires(Permissions.require("sc-goodies.enderstorage.view.public", 3))
          .then(dyeArg("left").then(dyeArg("middle").then(dyeArg("right")
            .executes(EnderStorageCommand(false))))))
        .then(literal("private")
          .requires(Permissions.require("sc-goodies.enderstorage.view.private", 3))
          .then(userArg("user")
            .then(dyeArg("left").then(dyeArg("middle").then(dyeArg("right")
              .executes(EnderStorageCommand(true)))))))
      )
    }
  }
}
