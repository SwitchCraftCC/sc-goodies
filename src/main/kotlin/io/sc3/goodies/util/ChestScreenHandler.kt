package io.sc3.goodies.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

abstract class ChestScreenHandler(
  syncId: Int,
  playerInv: PlayerInventory,
  val inv: Inventory,
  type: ScreenHandlerType<out ScreenHandler>,
  private val rows: Int,
  private val cols: Int = 9,
  yStart: Int = 8,
  playerYStart: Int = yStart + 4
) : ScreenHandler(type, syncId) {
  private val size
    get() = rows * cols

  init {
    checkSize(inv, size)

    inv.onOpen(playerInv.player)

    // Chest slots
    for (y in 0 until rows) {
      for (x in 0 until cols) {
        val slot = y * cols + x
        addSlot(makeSlot(slot, 8 + x * 18, yStart + y * 18))
      }
    }

    // Player inventory
    val playerX = ((cols - 9) * 18 / 2).coerceAtLeast(0) + 8
    val playerY = (rows * 18) + playerYStart
    for (y in 0 until 3) {
      for (x in 0 until 9) {
        val slot = x + y * 9 + 9
        addSlot(Slot(playerInv, slot, playerX + x * 18, y * 18 + playerY))
      }
    }

    // Player hotbar
    for (slot in 0 until 9) {
      addSlot(Slot(playerInv, slot, playerX + slot * 18, playerY + 58))
    }
  }

  override fun canUse(player: PlayerEntity) = inv.canPlayerUse(player)

  override fun quickMove(player: PlayerEntity, index: Int): ItemStack {
    val slot = slots[index]
    if (!slot.hasStack()) return ItemStack.EMPTY

    val existing = slot.stack
    val result = existing.copy()

    if (index < size) {
      // One of our own slots, insert into the player's inventory
      if (!insertItem(existing, size, slots.size, true)) return ItemStack.EMPTY
    } else {
      // One of the player's inventory slots, insert into our inventory
      if (!insertItem(existing, 0, size, false)) return ItemStack.EMPTY
    }

    if (existing.isEmpty) {
      slot.stack = ItemStack.EMPTY
    } else {
      slot.markDirty()
    }

    return result
  }

  override fun onClosed(player: PlayerEntity) {
    super.onClosed(player)
    inv.onClose(player)
  }

  open fun makeSlot(index: Int, x: Int, y: Int): Slot = Slot(inv, index, x, y)
}
