package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models.GENERATED
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.goodies.elytra.DyedElytraItem
import pw.switchcraft.goodies.elytra.SpecialElytraType
import pw.switchcraft.goodies.ironchest.IronChestUpgrade

class ItemModelProvider(generator: FabricDataGenerator) : FabricModelProvider(generator) {
  override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
  }

  override fun generateItemModels(gen: ItemModelGenerator) {
    // Iron Chests and Shulkers
    IronChestUpgrade.values().forEach { upgrade ->
      log.info("Registering item model for $upgrade chest upgrade")
      gen.register(upgrade.chestUpgrade, GENERATED)

      log.info("Registering item model for $upgrade shulker upgrade")
      gen.register(upgrade.shulkerUpgrade, GENERATED)
    }

    ModItems.hoverBoots.values.forEach {
      gen.register(it, GENERATED)
    }

    gen.register(ModItems.itemMagnet, GENERATED)
    gen.register(ModItems.dragonScale, GENERATED)

    // Dyed + Special Elytra
    DyedElytraItem.dyedElytraItems.values
      .forEach { gen.register(it, GENERATED) }
    SpecialElytraType.values()
      .forEach { gen.register(it.item, GENERATED) }
  }

  companion object {
    private val log by ScGoodiesDatagen::log
  }
}
