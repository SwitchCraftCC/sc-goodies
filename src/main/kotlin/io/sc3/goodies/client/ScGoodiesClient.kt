package pw.switchcraft.goodies.client

import dev.emi.trinkets.api.client.TrinketRendererRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.DyeColor
import org.slf4j.LoggerFactory
import pw.switchcraft.goodies.Registration.ModBlockEntities
import pw.switchcraft.goodies.Registration.ModBlocks
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.goodies.Registration.ModScreens
import pw.switchcraft.goodies.client.elytra.ElytraClientEvents
import pw.switchcraft.goodies.client.enderstorage.EnderStorageBlockEntityRenderer
import pw.switchcraft.goodies.client.enderstorage.EnderStorageItemRenderer
import pw.switchcraft.goodies.client.enderstorage.EnderStorageScreen
import pw.switchcraft.goodies.client.hoverboots.HoverBootsTrinketRenderer
import pw.switchcraft.goodies.client.ironchest.*
import pw.switchcraft.goodies.client.itemmagnet.ItemMagnetHud
import pw.switchcraft.goodies.client.itemmagnet.ItemMagnetTrinketRenderer
import pw.switchcraft.goodies.client.misc.ConcreteSpeedupHandler
import pw.switchcraft.goodies.ironchest.IronChestVariant
import pw.switchcraft.goodies.itemmagnet.ItemMagnetHotkey
import pw.switchcraft.goodies.nature.ScTree

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
    BuiltinItemRendererRegistry.INSTANCE.register(ModItems.enderStorage, EnderStorageItemRenderer)
    HandledScreens.register(ModScreens.enderStorage, ::EnderStorageScreen)

    // Hover Boots
    ModItems.hoverBoots.values.forEach {
      TrinketRendererRegistry.registerRenderer(it, HoverBootsTrinketRenderer)
    }

    // Item Magnet
    TrinketRendererRegistry.registerRenderer(ModItems.itemMagnet, ItemMagnetTrinketRenderer)
    ItemMagnetHotkey.initEvents()
    ItemMagnetHud.initEvents()

    // Elytra
    ElytraClientEvents.initEvents()

    ConcreteSpeedupHandler.initEvents()
    registerTreeRenderLayers(ModBlocks.sakuraSapling)
    registerTreeRenderLayers(ModBlocks.mapleSapling)
    registerTreeRenderLayers(ModBlocks.blueSapling)
  }

  private fun registerIronChestRenderer(variant: IronChestVariant) {
    with(variant) {
      BlockEntityRendererRegistry.register(chestBlockEntityType)
        { IronChestBlockEntityRenderer(chestBlock) }
      BuiltinItemRendererRegistry.INSTANCE.register(chestBlock, IronChestItemRenderer(this))
      HandledScreens.register(chestScreenHandlerType, ::IronChestScreen)
    }
  }

  private fun registerIronShulkerRenderer(variant: IronChestVariant) {
    with(variant) {
      BlockEntityRendererRegistry.register(shulkerBlockEntityType)
        { IronShulkerBlockEntityRenderer(variant) }

      BuiltinItemRendererRegistry.INSTANCE.register(shulkerBlock, IronShulkerItemRenderer(this, null))
      DyeColor.values().forEach { color ->
        BuiltinItemRendererRegistry.INSTANCE.register(dyedShulkerBlocks[color]!!, IronShulkerItemRenderer(this, color))
      }

      HandledScreens.register(shulkerScreenHandlerType, ::IronChestScreen)
    }
  }

  private fun registerTreeRenderLayers(tree: ScTree) {
    BlockRenderLayerMap.INSTANCE.putBlock(tree.leaves, RenderLayer.getCutoutMipped())
    BlockRenderLayerMap.INSTANCE.putBlock(tree.sapling, RenderLayer.getCutout())
    BlockRenderLayerMap.INSTANCE.putBlock(tree.potted, RenderLayer.getCutout())
  }
}
