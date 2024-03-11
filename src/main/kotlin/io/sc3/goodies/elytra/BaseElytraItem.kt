package io.sc3.goodies.elytra

import io.sc3.goodies.util.BaseItem
import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem
import net.minecraft.block.DispenserBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.Equipment
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

abstract class BaseElytraItem(settings: Settings) : BaseItem(settings), FabricElytraItem, Equipment {
  init {
    DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR)
  }

  override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean =
    ingredient.isOf(Items.PHANTOM_MEMBRANE)

  override fun getSlotType() = EquipmentSlot.CHEST

  override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
    return equipAndSwap(this, world, user, hand)
  }

  override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    // Don't add any description
  }
}
