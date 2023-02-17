package io.sc3.goodies.ironstorage

import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.ironstorage.IronStorageVariant.*
import net.minecraft.block.BlockState
import net.minecraft.block.entity.LootableContainerBlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

enum class IronStorageUpgrade(
  val itemName: String,
  val from: IronStorageVariant?,
  val to: IronStorageVariant,
) {
  VANILLA_IRON("vanilla_iron", from = null, to = IRON),
  IRON_GOLD("iron_gold", from = IRON, to = GOLD),
  IRON_DIAMOND("iron_diamond", from = IRON, to = DIAMOND),
  GOLD_DIAMOND("gold_diamond", from = GOLD, to = DIAMOND);

  val upgradeItem by lazy {
    Registries.ITEM.get(ModId(itemName + "_chest_upgrade")) as IronStorageUpgradeItem
  }

  companion object {
    fun convertContainerBlock(world: World, pos: BlockPos, be: LootableContainerBlockEntity, newState: BlockState) {
      val customName = be.customName

      // Copy the items from the old inventory
      val size = be.size()
      val contents = DefaultedList.ofSize(size, ItemStack.EMPTY)
      for (i in 0 until size) {
        contents[i] = be.getStack(i)
      }

      // Remove the old inventory
      world.removeBlockEntity(pos)
      world.removeBlock(pos, false)

      // Create the new inventory
      world.setBlockState(pos, newState)

      val newBe = world.getBlockEntity(pos) as LootableContainerBlockEntity
      newBe.customName = customName

      // Copy the items to the new inventory
      for (i in 0 until size) {
        newBe.setStack(i, contents[i])
      }
    }
  }
}
