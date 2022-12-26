package io.sc3.goodies.itemmagnet

import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.library.networking.ScLibraryPacket

class ToggleItemMagnetPacket : ScLibraryPacket() {
  override val id = ToggleItemMagnetPacket.id

  companion object {
    val id = ModId("toggle_item_magnet")
    fun fromBytes(buf: PacketByteBuf) = ToggleItemMagnetPacket()
  }

  override fun toBytes(buf: PacketByteBuf) {}

  override fun onServerReceive(server: MinecraftServer, player: ServerPlayerEntity, handler: ServerPlayNetworkHandler,
                               responseSender: PacketSender) {
    super.onServerReceive(server, player, handler, responseSender)
    ItemMagnetState.onPlayerToggle(player)
  }
}
