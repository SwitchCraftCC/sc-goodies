package io.sc3.goodies.tomes

import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.tomes.AncientTomeItem.Companion.stackEnchantment
import io.sc3.goodies.util.AnvilEvents
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.loot.v2.LootTableSource
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.enchantment.Enchantments.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items.ENCHANTED_BOOK
import net.minecraft.loot.LootManager
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.LootTables.*
import net.minecraft.loot.entry.EmptyEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.registry.Registries.LOOT_FUNCTION_TYPE
import net.minecraft.registry.Registry.register
import net.minecraft.resource.ResourceManager
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.Property
import net.minecraft.text.Text.literal
import net.minecraft.util.Identifier
import net.minecraft.util.math.random.Random

private const val UPGRADE_COST = 10
private const val UPGRADE_COST_MAXED = 30

object TomeEnchantments {
  val validEnchantments = listOf(
    FEATHER_FALLING,
    SWIFT_SNEAK,
    THORNS,
    SHARPNESS,
    SMITE,
    BANE_OF_ARTHROPODS,
    KNOCKBACK,
    FIRE_ASPECT,
    LOOTING,
    SWEEPING,
    EFFICIENCY,
    UNBREAKING,
    FORTUNE,
    POWER,
    PUNCH,
    LUCK_OF_THE_SEA,
    LURE,
    LOYALTY,
    RIPTIDE,
    IMPALING,
    PIERCING
  )

  private const val maxTomeCount = 3.0f
  private const val lootWeightEmpty = 30
  private val lootWeights = mapOf(
    STRONGHOLD_LIBRARY_CHEST  to 10,
    SIMPLE_DUNGEON_CHEST      to 3,
    BASTION_TREASURE_CHEST    to 15,
    WOODLAND_MANSION_CHEST    to 10
  )

  fun init() {
    AnvilEvents.CHANGE.register(::onAnvilChange)

    register(LOOT_FUNCTION_TYPE, ModId("tome_enchant"), TomeLootFunction.type)
    LootTableEvents.MODIFY.register(::enhanceLootTables)
  }

  private fun enhanceLootTables(resourceManager: ResourceManager, lootManager: LootManager, id: Identifier,
                                builder: LootTable.Builder, source: LootTableSource) {
    val weight = lootWeights[id] ?: return
    val entry = ItemEntry.builder(ModItems.ancientTome)
      .weight(weight)
      .quality(2)
      .apply { TomeLootFunction(emptyArray()) }
      .build()

    builder.pool(LootPool.builder()
      .rolls(UniformLootNumberProvider.create(0.0f, maxTomeCount))
      .with(EmptyEntry.builder().weight(lootWeightEmpty))
      .with(entry))
  }

  fun applyRandomEnchantment(stack: ItemStack, rand: Random) {
    val ench = validEnchantments[rand.nextInt(validEnchantments.size)]
    EnchantedBookItem.addEnchantment(stack, EnchantmentLevelEntry(ench, ench.maxLevel))
  }

  private fun onAnvilChange(handler: AnvilScreenHandler, left: ItemStack, right: ItemStack,
                            output: CraftingResultInventory, name: String?, baseCost: Int,
                            playerEntity: PlayerEntity, levelCost: Property): Boolean {
    if (left.isEmpty || right.isEmpty) return true

    if (right.isOf(ModItems.ancientTome)) {
      val tomeEnch = stackEnchantment(right) ?: return true
      val enchants = EnchantmentHelper.get(left)
      val matched = enchants[tomeEnch] ?: return true

      if (matched <= tomeEnch.maxLevel) {
        val lvl = matched + 1
        enchants[tomeEnch] = lvl

        val cost = if (lvl > tomeEnch.maxLevel) UPGRADE_COST_MAXED else UPGRADE_COST

        applyOutput(name, left, enchants, cost, output, levelCost)
        return false
      }
    } else if (right.isOf(ENCHANTED_BOOK)) {
      val currentEnchants = EnchantmentHelper.get(left)
      val newEnchants = EnchantmentHelper.get(right)

      var isOver = false
      var isMatched = false

      newEnchants.forEach { (ench, level) ->
        if (level > ench.maxLevel) {
          isOver = true

          if (ench.isAcceptableItem(left) || left.isOf(ENCHANTED_BOOK)) {
            isMatched = true

            // Remove incompatible enchantments from the target book
            currentEnchants.entries.removeIf { (other) -> isIncompatible(other, ench) }

            currentEnchants[ench] = level
          }
        } else if (ench.isAcceptableItem(left)) {
          // Don't apply incompatible enchantments to the target item
          val incompatible = currentEnchants.entries.any { (other) -> isIncompatible(other, ench) }

          if (!incompatible) {
            currentEnchants[ench] = level
          }
        }
      }

      if (isOver && isMatched) {
        applyOutput(name, left, currentEnchants, UPGRADE_COST, output, levelCost)
        return false
      }
    }

    return true
  }

  private fun isIncompatible(otherEnch: Enchantment, ench: Enchantment?) =
    otherEnch != ench && !otherEnch.canCombine(ench)

  private fun applyOutput(name: String?, left: ItemStack, enchants: Map<Enchantment, Int>, cost: Int,
                          output: CraftingResultInventory, levelCost: Property) {
    val out = left.copy()
    EnchantmentHelper.set(enchants, out)

    val finalCost = if (!name.isNullOrEmpty() && (!out.hasCustomName() || name != left.name.string)) {
      out.setCustomName(literal(name))
      cost + 1
    } else {
      cost
    }

    output.setStack(0, out)
    levelCost.set(finalCost)
  }
}
