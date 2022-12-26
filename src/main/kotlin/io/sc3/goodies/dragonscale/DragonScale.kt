package io.sc3.goodies.dragonscale

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.boss.dragon.EnderDragonEntity
import net.minecraft.item.ItemStack
import io.sc3.goodies.Registration.ModItems

object DragonScale {
  @JvmStatic
  fun dragonUpdatePostDeath(dragon: EnderDragonEntity) {
    with (dragon) {
      if (world.isClient) return
      val fight = this.fight ?: return

      // Spawn a dragon scale 100 ticks after death if this is the second+ fight
      if (fight.hasPreviouslyKilled() && ticksSinceDeath == 100) {
        val itemEntity = ItemEntity(world, pos.x, pos.y, pos.z, ItemStack(ModItems.dragonScale))
        world.spawnEntity(itemEntity)
      }
    }
  }
}
