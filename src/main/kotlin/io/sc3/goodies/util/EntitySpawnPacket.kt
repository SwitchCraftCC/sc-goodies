package io.sc3.goodies.util

import io.netty.buffer.Unpooled
import io.sc3.goodies.ScGoodies.ModId
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.createS2CPacket
import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.registry.Registries.ENTITY_TYPE
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

/* TODO: Move to sc-library */
object EntitySpawnPacket {
  private val spawnPacketId = ModId("spawn_packet")

  fun create(e: Entity, pos: BlockPos? = null): Packet<ClientPlayPacketListener> {
    check(!e.world.isClient) { "SpawnPacketUtil.create called on the logical client!" }

    val buf = PacketByteBuf(Unpooled.buffer())
    buf.writeVarInt(ENTITY_TYPE.getRawId(e.type))
    buf.writeUuid(e.uuid)
    buf.writeVarInt(e.id)

    PacketBufUtil.writeVec3d(buf, pos?.let { Vec3d(it.x.toDouble(), it.y.toDouble(), it.z.toDouble()) } ?: e.pos)
    PacketBufUtil.writeAngle(buf, e.pitch)
    PacketBufUtil.writeAngle(buf, e.yaw)

    if (e is CustomSpawnableEntity) {
      val data = NbtCompound()
      e.writeCustomSpawnData(data)
      buf.writeNullable(data, PacketByteBuf::writeNbt)
    }

    return createS2CPacket(spawnPacketId, buf)
  }

  fun initEvents() {
    ClientPlayNetworking.registerGlobalReceiver(spawnPacketId) { client, _, buf, _ ->
      val type = ENTITY_TYPE[buf.readVarInt()]
      val uuid = buf.readUuid()
      val id = buf.readVarInt()
      val pos = PacketBufUtil.readVec3d(buf)
      val pitch = PacketBufUtil.readAngle(buf)
      val yaw = PacketBufUtil.readAngle(buf)
      val data = buf.readNullable(PacketByteBuf::readNbt)

      client.execute {
        checkNotNull(client.world) { "Tried to spawn entity in a null world!" }

        val e = type.create(client.world)
          ?: throw IllegalStateException("Failed to create instance of entity \"${ENTITY_TYPE.getId(type)}\"!")

        e.updateTrackedPosition(pos.x, pos.y, pos.z)
        e.refreshPositionAfterTeleport(pos.x, pos.y, pos.z)
        e.pitch = pitch
        e.yaw = yaw
        e.id = id
        e.uuid = uuid

        if (e is CustomSpawnableEntity && data != null) {
          e.readCustomSpawnData(data)
        }

        client.world?.addEntity(id, e)
      }
    }
  }
}

object PacketBufUtil {
  private fun packAngle(angle: Float) = MathHelper.floor(angle * 256 / 360).toByte()
  private fun unpackAngle(angleByte: Byte) = angleByte * 360 / 256f

  fun writeAngle(byteBuf: PacketByteBuf, angle: Float) {
    byteBuf.writeByte(packAngle(angle).toInt())
  }

  fun readAngle(byteBuf: PacketByteBuf) =
    unpackAngle(byteBuf.readByte())

  fun writeVec3d(byteBuf: PacketByteBuf, vec3d: Vec3d) {
    byteBuf.writeDouble(vec3d.x)
    byteBuf.writeDouble(vec3d.y)
    byteBuf.writeDouble(vec3d.z)
  }

  fun readVec3d(byteBuf: PacketByteBuf): Vec3d {
    val x = byteBuf.readDouble()
    val y = byteBuf.readDouble()
    val z = byteBuf.readDouble()
    return Vec3d(x, y, z)
  }
}

interface CustomSpawnableEntity {
  fun readCustomSpawnData(nbt: NbtCompound) {}
  fun writeCustomSpawnData(nbt: NbtCompound) {}
}
