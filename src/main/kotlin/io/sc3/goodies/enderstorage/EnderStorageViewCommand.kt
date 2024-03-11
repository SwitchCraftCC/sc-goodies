package io.sc3.goodies.enderstorage

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.Text.translatable
import net.minecraft.util.math.BlockPos

class EnderStorageViewCommand(target: EnderStorageTargetType) : EnderStorageBaseCommand(target) {
  override fun run(ctx: CommandContext<ServerCommandSource>): Int {
    val player = ctx.source.playerOrThrow
    val (inv, frequency) = getInventory(ctx)
    val (state) = getState(ctx)

    player.openHandledScreen(object : ExtendedScreenHandlerFactory {
      // Don't add viewingPlayers here
      override fun createMenu(syncId: Int, playerInv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return EnderStorageScreenHandler(syncId, playerInv, inv, BlockPos.ORIGIN, frequency, state)
      }

      override fun getDisplayName(): Text = translatable(TL_KEY)

      override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeBlockPos(BlockPos.ORIGIN)
        frequency.toPacket(buf)
        state.toPacket(buf)
      }
    })

    return SINGLE_SUCCESS
  }
}
