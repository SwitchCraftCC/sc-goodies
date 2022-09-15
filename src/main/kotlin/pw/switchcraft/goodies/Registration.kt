package pw.switchcraft.goodies

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.block.*
import net.minecraft.block.AbstractBlock.ContextPredicate
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.DyeColor
import net.minecraft.util.Rarity.EPIC
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry.*
import pw.switchcraft.goodies.Registration.ModBlockEntities.rBlockEntity
import pw.switchcraft.goodies.Registration.ModBlocks.chestSettings
import pw.switchcraft.goodies.Registration.ModBlocks.rBlock
import pw.switchcraft.goodies.Registration.ModBlocks.shulkerSettings
import pw.switchcraft.goodies.Registration.ModItems.itemSettings
import pw.switchcraft.goodies.Registration.ModItems.rItem
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.datagen.recipes.handlers.RECIPE_HANDLERS
import pw.switchcraft.goodies.enderstorage.EnderStorageBlock
import pw.switchcraft.goodies.enderstorage.EnderStorageBlockEntity
import pw.switchcraft.goodies.enderstorage.EnderStorageScreenHandler
import pw.switchcraft.goodies.hoverboots.HoverBootsItem
import pw.switchcraft.goodies.ironchest.*
import pw.switchcraft.goodies.ironshulker.IronShulkerBlock
import pw.switchcraft.goodies.ironshulker.IronShulkerBlockEntity
import pw.switchcraft.goodies.ironshulker.IronShulkerCauldronBehavior
import pw.switchcraft.goodies.ironshulker.IronShulkerItem
import pw.switchcraft.goodies.itemmagnet.ItemMagnetItem
import pw.switchcraft.goodies.itemmagnet.ToggleItemMagnetPacket
import pw.switchcraft.goodies.util.BaseItem
import pw.switchcraft.library.networking.registerServerReceiver
import pw.switchcraft.library.recipe.RecipeHandler

object Registration {
  internal fun init() {
    // Force static initializers to run
    listOf(ModBlocks, ModItems, ModBlockEntities, ModScreens)

    IronChestVariant.values().forEach { variant ->
      registerIronChest(variant)

      registerIronShulker(variant) // Undyed shulker
      DyeColor.values().forEach { registerIronShulker(variant, it) }

      // Shulker block entities, done in bulk for each dyed variant + undyed
      registerIronShulkerBlockEntities(variant)
    }

    IronChestUpgrade.values().forEach { upgrade ->
      rItem(upgrade.itemName + "_chest_upgrade", IronChestUpgradeItem(upgrade, false, itemSettings()))
      rItem(upgrade.itemName + "_shulker_upgrade", IronChestUpgradeItem(upgrade, true, itemSettings()))
    }

    IronShulkerCauldronBehavior.registerBehavior()

    EnderStorageBlockEntity.initEvents()

    registerServerReceiver(ToggleItemMagnetPacket.id, ToggleItemMagnetPacket::fromBytes)

    RECIPE_HANDLERS.forEach(RecipeHandler::registerSerializers)
  }

  private fun registerIronChest(variant: IronChestVariant) {
    with (variant) {
      // Register the block and item
      val chestBlock = rBlock(chestId, IronChestBlock(chestSettings(), this))
      rItem(chestBlock, ::BlockItem)

      // Register the block entity and screen handler
      rBlockEntity(chestId, chestBlock) { pos, state -> IronChestBlockEntity(this, pos, state) }
      register(SCREEN_HANDLER, ModId(chestId), chestScreenHandlerType)
    }
  }

  private fun registerIronShulker(variant: IronChestVariant, color: DyeColor? = null) {
    with (variant) {
      val id = if (color != null) "${shulkerId}_${color.getName()}" else shulkerId

      // Register the block and item
      val shulkerBlock = rBlock(id, IronShulkerBlock(shulkerSettings(color), this, color))
      rItem(shulkerBlock, ::IronShulkerItem, itemSettings().maxCount(1))
    }
  }

  private fun registerIronShulkerBlockEntities(variant: IronChestVariant) {
    with (variant) {
      val blocks = mutableSetOf(shulkerBlock)
      blocks.addAll(dyedShulkerBlocks.values)

      // Register the block entity
      rBlockEntity(shulkerId, *blocks.toTypedArray())
        { pos, state -> IronShulkerBlockEntity(this, pos, state) }

      register(SCREEN_HANDLER, ModId(shulkerId), shulkerScreenHandlerType)
    }
  }

  object ModBlocks {
    val enderStorage = rBlock("ender_storage", EnderStorageBlock(AbstractBlock.Settings
      .of(Material.STONE).requiresTool().strength(22.5f, 600.0f)))

    fun <T : Block> rBlock(name: String, value: T): T =
      register(BLOCK, ModId(name), value)

    fun blockSettings(): AbstractBlock.Settings = AbstractBlock.Settings
      .of(Material.STONE)
      .strength(2.0f)
      .nonOpaque()

    fun chestSettings(): AbstractBlock.Settings = AbstractBlock.Settings
      .of(Material.METAL)
      .strength(3.0f)
      .nonOpaque()

    fun shulkerSettings(color: DyeColor?): AbstractBlock.Settings {
      val predicate = ContextPredicate { _, world, pos ->
        val be = world.getBlockEntity(pos) as? IronShulkerBlockEntity
        be?.suffocates() ?: true
      }

      return AbstractBlock.Settings
        .of(Material.SHULKER_BOX)
        .mapColor(color?.mapColor ?: MapColor.PURPLE)
        .strength(2.0f)
        .dynamicBounds()
        .nonOpaque()
        .suffocates(predicate)
        .blockVision(predicate)
    }
  }

  object ModItems {
    private val itemGroup = FabricItemGroupBuilder.build(ModId("main")) { ItemStack(Items.AXOLOTL_BUCKET) }

    val enderStorage = rItem(ModBlocks.enderStorage, ::BlockItem, itemSettings())

    val hoverBoots = DyeColor.values().associateWith {
      rItem("hover_boots_${it.getName()}", HoverBootsItem(it, itemSettings().maxCount(1)))
    }

    val itemMagnet = rItem("item_magnet", ItemMagnetItem(itemSettings().maxCount(1)))
    val dragonScale = rItem("dragon_scale", BaseItem(itemSettings().maxCount(16).rarity(EPIC)))

    fun <T : Item> rItem(name: String, value: T): T =
      register(ITEM, ModId(name), value)

    fun <B : Block, I : Item> rItem(parent: B, supplier: (B, Item.Settings) -> I,
                                    settings: Item.Settings = itemSettings()): I {
      val o = register(ITEM, BLOCK.getId(parent), supplier(parent, settings))
      Item.BLOCK_ITEMS[parent] = o
      return o
    }

    fun itemSettings(): Item.Settings = Item.Settings()
      .group(itemGroup)
  }

  object ModBlockEntities {
    val enderStorage = rBlockEntity("ender_storage", ModBlocks.enderStorage, factory = ::EnderStorageBlockEntity)

    fun <T : BlockEntity> rBlockEntity(name: String, vararg block: Block,
                                       factory: (BlockPos, BlockState) -> T): BlockEntityType<T> {
      val blockEntityType = FabricBlockEntityTypeBuilder.create(factory, *block).build()
      return register(BLOCK_ENTITY_TYPE, ModId(name), blockEntityType)
    }
  }

  object ModScreens {
    val enderStorage: ScreenHandlerType<EnderStorageScreenHandler> =
      register(SCREEN_HANDLER, ModId("ender_storage"), ExtendedScreenHandlerType(::EnderStorageScreenHandler))
  }
}
