package pw.switchcraft.goodies.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreens
import org.slf4j.LoggerFactory
import pw.switchcraft.goodies.Registration.ModBlockEntities
import pw.switchcraft.goodies.Registration.ModScreens
import pw.switchcraft.goodies.client.enderstorage.EnderStorageBlockEntityRenderer
import pw.switchcraft.goodies.client.enderstorage.EnderStorageScreen
import pw.switchcraft.goodies.client.ironchest.IronChestBlockEntityRenderer
import pw.switchcraft.goodies.client.ironchest.IronChestScreen
import pw.switchcraft.goodies.client.ironchest.IronShulkerBlockEntityRenderer
import pw.switchcraft.goodies.ironchest.IronChestVariant

object ScGoodiesClient : ClientModInitializer {
  val log = LoggerFactory.getLogger("ScGoodies/ScGoodiesClient")!!

  override fun onInitializeClient() {
    log.info("sc-goodies client initializing")

    // Iron Chests
    IronChestVariant.values().forEach { variant ->
      registerIronChestRenderer(variant)
      registerIronShulkerRenderer(variant) // Accounts for both dyed and undyed shulkers
    }

    // Ender Storage
    BlockEntityRendererRegistry.register(ModBlockEntities.enderStorage, ::EnderStorageBlockEntityRenderer)
    HandledScreens.register(ModScreens.enderStorage, ::EnderStorageScreen)
  }

  private fun registerIronChestRenderer(variant: IronChestVariant) {
    with(variant) {
      BlockEntityRendererRegistry.register(chestBlockEntityType)
        { IronChestBlockEntityRenderer(chestBlock, it) }
      HandledScreens.register(chestScreenHandlerType, ::IronChestScreen)
    }
  }

  private fun registerIronShulkerRenderer(variant: IronChestVariant) {
    with(variant) {
      BlockEntityRendererRegistry.register(shulkerBlockEntityType)
        { IronShulkerBlockEntityRenderer(variant, it) }
      HandledScreens.register(shulkerScreenHandlerType, ::IronChestScreen)
    }
  }
}
