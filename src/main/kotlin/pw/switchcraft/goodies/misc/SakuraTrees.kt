package pw.switchcraft.goodies.misc

import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.fabricmc.fabric.api.`object`.builder.v1.trade.TradeOfferHelper
import net.minecraft.block.ComposterBlock
import net.minecraft.loot.LootTables.SIMPLE_DUNGEON_CHEST
import net.minecraft.loot.LootTables.VILLAGE_PLAINS_CHEST
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.village.TradeOffers
import net.minecraft.village.VillagerProfession
import pw.switchcraft.goodies.Registration.ModItems.sakuraSapling

object SakuraTrees {
  private val sakuraLootWeights = mapOf(
    VILLAGE_PLAINS_CHEST to 6,
    SIMPLE_DUNGEON_CHEST to 3,
  )

  fun init() {
    TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 3) {
      TradeOffers.SellItemFactory(sakuraSapling, 6, 1, 15)
    }

    LootTableEvents.MODIFY.register { _, _, id, builder, _ ->
      val weight = sakuraLootWeights[id] ?: return@register
      val entry = ItemEntry.builder(sakuraSapling)
        .weight(weight)
        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 4.0f)))
        .build()

      builder.modifyPools { it.with(entry) }
    }

    ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(sakuraSapling.asItem(), 0.3f)
  }
}
