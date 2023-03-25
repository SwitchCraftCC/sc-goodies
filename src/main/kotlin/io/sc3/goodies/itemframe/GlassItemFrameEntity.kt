package io.sc3.goodies.itemframe

import io.sc3.goodies.Registration.ModEntities
import io.sc3.goodies.Registration.ModItems
import net.minecraft.entity.EntityType
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry.BOOLEAN
import net.minecraft.entity.decoration.ItemFrameEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.BannerItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.registry.tag.BlockTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class GlassItemFrameEntity : ItemFrameEntity {
  private val posBehind
    get() = blockPos.offset(facing.opposite)

  constructor(world: World, pos: BlockPos, facing: Direction) :
    super(ModEntities.glassItemFrameEntity, world, pos, facing)

  constructor(type: EntityType<out GlassItemFrameEntity>, world: World) :
    super(type, world)

  constructor(type: EntityType<out GlassItemFrameEntity>, world: World, pos: BlockPos, facing: Direction) :
    super(type, world, pos, facing)

  override fun initDataTracker() {
    super.initDataTracker()
    dataTracker.startTracking(isGlowingFrame, false)
  }

  override fun interact(player: PlayerEntity, hand: Hand): ActionResult {
    // Allow interactions to pass through to the block entity behind the frame unless the user is shift-clicking
    val item = heldItemStack
    if (!player.isSneaking && !item.isEmpty && item.item !is BannerItem) {
      val behind = posBehind
      val be = world.getBlockEntity(behind)

      if (be != null && be is Inventory) { // TODO: Replace with tag?
        val behindBlock = world.getBlockState(behind)
        val result = behindBlock.onUse(world, player, hand, BlockHitResult(pos, facing, behind, true))
        if (result.isAccepted) return result
      }
    }

    return super.interact(player, hand)
  }

  override fun canStayAttached() =
    super.canStayAttached() || world.getBlockState(posBehind).isIn(BlockTags.STANDING_SIGNS)

  override fun readCustomDataFromNbt(nbt: NbtCompound) {
    super.readCustomDataFromNbt(nbt)
    dataTracker.set(isGlowingFrame, nbt.getBoolean("isGlowingFrame"))
  }

  override fun writeCustomDataToNbt(nbt: NbtCompound) {
    super.writeCustomDataToNbt(nbt)
    nbt.putBoolean("isGlowingFrame", dataTracker.get(isGlowingFrame))
  }

  override fun createSpawnPacket(): Packet<ClientPlayPacketListener> =
    EntitySpawnS2CPacket(this, facing.id, decorationBlockPos)

  override fun getAsItemStack(): ItemStack = if (dataTracker.get(isGlowingFrame)) {
    ItemStack(ModItems.glowGlassItemFrame)
  } else {
    ItemStack(ModItems.glassItemFrame)
  }

  companion object {
    val isGlowingFrame: TrackedData<Boolean> = DataTracker.registerData(GlassItemFrameEntity::class.java, BOOLEAN)
  }
}
