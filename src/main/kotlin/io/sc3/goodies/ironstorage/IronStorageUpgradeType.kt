package io.sc3.goodies.ironstorage

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.state.property.Property

abstract class IronStorageUpgradeType {
  /** Map of vanilla properties -> iron storage properties. For iron -> gold and iron -> diamond conversion, only the
   * values are used. */
  abstract val propertyMap: Map<Property<*>, Property<*>>

  abstract fun getViewers(be: BlockEntity): Int

  abstract fun isValidUpgrade(oldBlock: Block, from: IronStorageVariant?, to: IronStorageVariant): Boolean

  abstract fun getNewBlock(oldBlock: Block, from: IronStorageVariant?, to: IronStorageVariant): Block

  fun getOldProperties(be: BlockEntity, state: BlockState, from: IronStorageVariant?): Map<Property<*>, Comparable<*>> =
    if (from == null) {
      // Vanilla -> Iron upgrade
      propertyMap.mapValues { state[it.key] }
    } else {
      // Iron -> Gold or Gold -> Diamond upgrade
      propertyMap.mapValues { state[it.value] }
    }

  fun getNewState(to: Block, values: Map<Property<*>, Comparable<*>>): BlockState =
    values.entries.fold(to.defaultState) { state, (property, value) ->
      state.with(property as Property<Comparable<Any>>, value as Comparable<Any>)
    }
}
