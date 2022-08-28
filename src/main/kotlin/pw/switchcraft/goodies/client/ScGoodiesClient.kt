package pw.switchcraft.goodies.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreens
import org.slf4j.LoggerFactory
import pw.switchcraft.goodies.chest.IronChestVariant
import pw.switchcraft.goodies.client.chest.IronChestBlockEntityRenderer
import pw.switchcraft.goodies.client.chest.IronChestScreen
import pw.switchcraft.goodies.client.shulker.IronShulkerBlockEntityRenderer

object ScGoodiesClient : ClientModInitializer {
  val log = LoggerFactory.getLogger("ScGoodies/ScGoodiesClient")!!

  override fun onInitializeClient() {
    log.info("sc-goodies client initializing")

    IronChestVariant.values().forEach { variant ->
      registerIronChestRenderer(variant)
      registerIronShulkerRenderer(variant) // Accounts for both dyed and undyed shulkers
    }
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
