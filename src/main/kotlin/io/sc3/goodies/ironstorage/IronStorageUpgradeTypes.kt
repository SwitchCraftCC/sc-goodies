package io.sc3.goodies.ironstorage

import io.sc3.goodies.mixin.BarrelBlockEntityAccessor
import io.sc3.library.WaterloggableBlock
import net.minecraft.block.BarrelBlock
import net.minecraft.block.Block
import net.minecraft.block.ChestBlock
import net.minecraft.block.ShulkerBoxBlock
import net.minecraft.block.entity.BarrelBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.state.property.Property

object IronChestUpgradeType : IronStorageUpgradeType() {
  override val propertyMap = mapOf<Property<*>, Property<*>>(
    ChestBlock.FACING to IronChestBlock.facing,
    ChestBlock.WATERLOGGED to WaterloggableBlock.waterlogged
  )

  override fun getViewers(be: BlockEntity): Int = when (be) {
    is ChestBlockEntity -> ChestBlockEntity.getPlayersLookingInChestCount(be.world, be.pos)
    is IronChestBlockEntity -> be.stateManager.viewerCount
    else -> 0
  }

  override fun isValidUpgrade(oldBlock: Block, from: IronStorageVariant?, to: IronStorageVariant): Boolean =
    (from == null && oldBlock is ChestBlock)
      || (from != null && oldBlock is IronChestBlock && oldBlock.variant == from)

  override fun getNewBlock(oldBlock: Block, from: IronStorageVariant?, to: IronStorageVariant): Block =
    to.chestBlock
}

object IronShulkerUpgradeType : IronStorageUpgradeType() {
  override val propertyMap = mapOf<Property<*>, Property<*>>(
    ShulkerBoxBlock.FACING to IronShulkerBlock.facing
  )

  override fun getViewers(be: BlockEntity): Int = when (be) {
    is ChestBlockEntity -> ChestBlockEntity.getPlayersLookingInChestCount(be.world, be.pos)
    is IronChestBlockEntity -> be.stateManager.viewerCount
    else -> 0
  }

  override fun isValidUpgrade(oldBlock: Block, from: IronStorageVariant?, to: IronStorageVariant): Boolean =
    (from == null && oldBlock is ShulkerBoxBlock)
      || (from != null && oldBlock is IronShulkerBlock && oldBlock.variant == from)

  override fun getNewBlock(oldBlock: Block, from: IronStorageVariant?, to: IronStorageVariant): Block {
    val color = when (oldBlock) {
      is ShulkerBoxBlock -> oldBlock.color
      is IronShulkerBlock -> oldBlock.color
      else -> null
    }

    return if (color != null) {
      to.dyedShulkerBlocks[color]!!
    } else {
      to.shulkerBlock
    }
  }
}

object IronBarrelUpgradeType : IronStorageUpgradeType() {
  override val propertyMap = mapOf<Property<*>, Property<*>>(
    BarrelBlock.FACING to IronBarrelBlock.facing,
    BarrelBlock.OPEN to IronBarrelBlock.open
  )

  override fun getViewers(be: BlockEntity): Int = when (be) {
    is BarrelBlockEntity -> (be as BarrelBlockEntityAccessor).stateManager.viewerCount
    is IronBarrelBlockEntity -> be.stateManager.viewerCount
    else -> 0
  }

  override fun isValidUpgrade(oldBlock: Block, from: IronStorageVariant?, to: IronStorageVariant): Boolean =
    (from == null && oldBlock is BarrelBlock)
      || (from != null && oldBlock is IronBarrelBlock && oldBlock.variant == from)

  override fun getNewBlock(oldBlock: Block, from: IronStorageVariant?, to: IronStorageVariant): Block =
    to.barrelBlock
}

fun upgradeTypeFromBlock(block: Block): IronStorageUpgradeType? = when (block) {
  is ChestBlock       -> IronChestUpgradeType
  is IronChestBlock   -> IronChestUpgradeType
  is ShulkerBoxBlock  -> IronShulkerUpgradeType
  is IronShulkerBlock -> IronShulkerUpgradeType
  is BarrelBlock      -> IronBarrelUpgradeType
  is IronBarrelBlock  -> IronBarrelUpgradeType
  else -> null
}
