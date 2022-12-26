package pw.switchcraft.goodies.elytra

import net.fabricmc.fabric.api.entity.event.v1.FabricElytraItem
import net.minecraft.block.DispenserBlock
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ArmorItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.Wearable
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import pw.switchcraft.goodies.util.BaseItem

abstract class BaseElytraItem(settings: Settings) : BaseItem(settings), FabricElytraItem, Wearable {
  init {
    DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR)
  }

  override fun canRepair(stack: ItemStack, ingredient: ItemStack): Boolean =
    ingredient.isOf(Items.PHANTOM_MEMBRANE)

  override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
    val stack = user.getStackInHand(hand)
    val slot = MobEntity.getPreferredEquipmentSlot(stack)
    val equipped = user.getEquippedStack(slot)

    return if (equipped.isEmpty) {
      user.equipStack(slot, stack.copy())
      if (!world.isClient()) {
        user.incrementStat(Stats.USED.getOrCreateStat(this))
      }

      stack.count = 0
      TypedActionResult.success(stack, world.isClient())
    } else {
      TypedActionResult.fail(stack)
    }
  }

  override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA

  override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
    // Don't add any description
  }
}
