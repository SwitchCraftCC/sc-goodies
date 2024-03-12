package io.sc3.goodies.enderstorage

import net.minecraft.inventory.Inventories
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState

class EnderStorageState : PersistentState() {
  val inventories = mutableMapOf<Frequency, EnderStorageProvider.EnderStorageInventory>()
  val states = mutableMapOf<Frequency, FrequencyState>()

  override fun writeNbt(nbt: NbtCompound): NbtCompound {
    inventories.forEach { (frequency, inv) ->
      val freqNbt = NbtCompound()
      Inventories.writeNbt(freqNbt, inv.items) // Write the items
      nbt.put(frequency.toKey(), freqNbt)
    }

    // Write the states separately, even if they don't have an inventory for some reason
    states.forEach { (frequency, state) ->
      val freqNbt = nbt.getCompound(frequency.toKey()) // Returns an empty compound if it doesn't exist
      state.toNbt(freqNbt)
      nbt.put(frequency.toKey(), freqNbt)
    }

    return nbt
  }

  companion object {
    fun fromNbt(server: MinecraftServer, nbt: NbtCompound): EnderStorageState {
      val state = EnderStorageState()

      nbt.keys.forEach { key ->
        val frequency = Frequency.fromKey(key)
        val freqNbt = nbt.getCompound(key)

        val inv = EnderStorageProvider.EnderStorageInventory(server)
        Inventories.readNbt(freqNbt, inv.items) // Read the items
        state.inventories[frequency] = inv
        state.states[frequency] = FrequencyState.fromNbt(freqNbt) // Read the name and description
      }

      return state
    }
  }
}
