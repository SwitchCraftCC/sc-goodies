package io.sc3.goodies.enderstorage

import net.minecraft.inventory.Inventories
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.PersistentState

class EnderStorageState : PersistentState() {
  val inventories = mutableMapOf<Frequency, EnderStorageProvider.EnderStorageInventory>()

  override fun writeNbt(nbt: NbtCompound): NbtCompound {
    inventories.forEach { (frequency, inv) ->
      val inventoryNbt = NbtCompound()
      nbt.put(frequency.toKey(), Inventories.writeNbt(inventoryNbt, inv.items))
    }

    return nbt
  }

  companion object {
    fun fromNbt(nbt: NbtCompound): EnderStorageState {
      val state = EnderStorageState()

      nbt.keys.forEach { key ->
        val frequency = Frequency.fromKey(key)
        val inventoryNbt = nbt.getCompound(key)

        val inv = EnderStorageProvider.EnderStorageInventory()
        Inventories.readNbt(inventoryNbt, inv.items)
        state.inventories[frequency] = inv
      }

      return state
    }
  }
}
