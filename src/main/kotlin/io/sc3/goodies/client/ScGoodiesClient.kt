package io.sc3.goodies.client

import dev.emi.trinkets.api.client.TrinketRendererRegistry
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.DyeColor
import org.slf4j.LoggerFactory
import io.sc3.goodies.Registration.ModBlockEntities
import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.Registration.ModScreens
import io.sc3.goodies.client.elytra.ElytraClientEvents
import io.sc3.goodies.client.enderstorage.EnderStorageBlockEntityRenderer
import io.sc3.goodies.client.enderstorage.EnderStorageItemRenderer
import io.sc3.goodies.client.enderstorage.EnderStorageScreen
import io.sc3.goodies.client.hoverboots.HoverBootsTrinketRenderer
import io.sc3.goodies.client.ironchest.*
import io.sc3.goodies.client.itemmagnet.ItemMagnetHud
import io.sc3.goodies.client.itemmagnet.ItemMagnetTrinketRenderer
import io.sc3.goodies.client.misc.ConcreteSpeedupHandler
import io.sc3.goodies.ironchest.IronChestVariant
import io.sc3.goodies.itemmagnet.ItemMagnetHotkey
import io.sc3.goodies.nature.ScTree

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
