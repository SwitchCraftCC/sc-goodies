package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Models
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.goodies.ironchest.IronChestUpgrade

class ItemModelProvider(generator: FabricDataGenerator) : FabricModelProvider(generator) {
  override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
  }

  override fun generateItemModels(gen: ItemModelGenerator) {
    IronChestUpgrade.values().forEach { upgrade ->
      log.info("Registering item model for $upgrade chest upgrade")
      gen.register(upgrade.chestUpgrade, Models.GENERATED)

      log.info("Registering item model for $upgrade shulker upgrade")
      gen.register(upgrade.shulkerUpgrade, Models.GENERATED)
    }

    ModItems.hoverBoots.values.forEach {
      gen.register(it, Models.GENERATED)
    }

    gen.register(ModItems.itemMagnet, Models.GENERATED)
  }

  companion object {
    private val log by ScGoodiesDatagen::log
  }
}
