package io.sc3.goodies

import io.sc3.goodies.Registration.ModBlockEntities.rBlockEntity
import io.sc3.goodies.Registration.ModBlocks.autumnGrass
import io.sc3.goodies.Registration.ModBlocks.barrelSettings
import io.sc3.goodies.Registration.ModBlocks.blueGrass
import io.sc3.goodies.Registration.ModBlocks.blueSapling
import io.sc3.goodies.Registration.ModBlocks.chestSettings
import io.sc3.goodies.Registration.ModBlocks.mapleSapling
import io.sc3.goodies.Registration.ModBlocks.rBlock
import io.sc3.goodies.Registration.ModBlocks.sakuraSapling
import io.sc3.goodies.Registration.ModBlocks.shulkerSettings
import io.sc3.goodies.Registration.ModItems.elytraSettings
import io.sc3.goodies.Registration.ModItems.itemSettings
import io.sc3.goodies.Registration.ModItems.rItem
import io.sc3.goodies.ScGoodies.ModId
import io.sc3.goodies.datagen.recipes.handlers.RECIPE_HANDLERS
import io.sc3.goodies.elytra.DyedElytraItem
import io.sc3.goodies.elytra.ElytraCauldronBehavior
import io.sc3.goodies.elytra.SpecialElytraItem
import io.sc3.goodies.elytra.SpecialElytraType
import io.sc3.goodies.enderstorage.*
import io.sc3.goodies.hoverboots.HoverBootsItem
import io.sc3.goodies.ironstorage.*
import io.sc3.goodies.itemframe.GlassItemFrameEntity
import io.sc3.goodies.itemframe.GlassItemFrameItem
import io.sc3.goodies.itemmagnet.ItemMagnetItem
import io.sc3.goodies.itemmagnet.MAGNET_MAX_DAMAGE
import io.sc3.goodies.itemmagnet.ToggleItemMagnetPacket
import io.sc3.goodies.misc.ConcreteExtras
import io.sc3.goodies.misc.EndermitesFormShulkers
import io.sc3.goodies.misc.PopcornItem
import io.sc3.goodies.nature.ScGrass
import io.sc3.goodies.nature.ScSaplingGenerator
import io.sc3.goodies.nature.ScTree
import io.sc3.goodies.tomes.AncientTomeItem
import io.sc3.goodies.tomes.TomeEnchantments
import io.sc3.goodies.util.BaseItem
import io.sc3.library.networking.registerServerReceiver
import io.sc3.library.recipe.RecipeHandler
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.*
import net.minecraft.block.AbstractBlock.ContextPredicate
import net.minecraft.block.Material.SOLID_ORGANIC
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.SpawnGroup
import net.minecraft.item.*
import net.minecraft.registry.Registerable
import net.minecraft.registry.Registries.*
import net.minecraft.registry.Registry.register
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys.CONFIGURED_FEATURE
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.sound.BlockSoundGroup.GRASS
import net.minecraft.util.DyeColor
import net.minecraft.util.Rarity.EPIC
import net.minecraft.util.Rarity.UNCOMMON
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.intprovider.ConstantIntProvider
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.TreeFeatureConfig
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer
import net.minecraft.world.gen.stateprovider.BlockStateProvider
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer
import java.util.*

object Registration {
  private val items = mutableListOf<Item>()

  internal fun init() {
    // Force static initializers to run
    listOf(ModBlocks, ModItems, ModBlockEntities, ModScreens, ModEntities)

    // Iron Chests and Shulkers
    IronStorageVariant.values().forEach { variant ->
      registerIronChest(variant)

      registerIronShulker(variant) // Undyed shulker
      DyeColor.values().forEach { registerIronShulker(variant, it) }

      // Shulker block entities, done in bulk for each dyed variant + undyed
      registerIronShulkerBlockEntities(variant)

      registerIronBarrel(variant)
    }

    IronStorageUpgrade.values().forEach { upgrade ->
      rItem("${upgrade.itemName}_chest_upgrade", IronStorageUpgradeItem(upgrade, itemSettings()))
    }

    IronShulkerCauldronBehavior.registerBehavior()

    // Ender Storage
    EnderStorageBlockEntity.initEvents()
    EnderStorageCommands.register()
    if (FabricLoader.getInstance().isModLoaded("computercraft")) {
      EnderStorageMethods.register()
    }

    // Item Magnets
    registerServerReceiver(ToggleItemMagnetPacket.id, ToggleItemMagnetPacket::fromBytes)

    // Dyed + Special Elytra
    DyeColor.values()
      .forEach { rItem("elytra_${it.getName()}", DyedElytraItem(it, elytraSettings())) }
    SpecialElytraType.values()
      .forEach { rItem("elytra_${it.type}", SpecialElytraItem(it, elytraSettings())) }
    ElytraCauldronBehavior.registerBehavior()

    // Concrete Slabs and Stairs
    ConcreteExtras.colors.values.forEach {
      val slabBlock = rBlock(it.slabBlockId.path,
        SlabBlock(AbstractBlock.Settings.copy(it.baseBlock)))
      rItem(slabBlock, ::BlockItem)

      val stairsBlock = rBlock(it.stairsBlockId.path, StairsBlock(it.baseBlock.defaultState,
        AbstractBlock.Settings.copy(it.baseBlock)))
      rItem(stairsBlock, ::BlockItem)
    }

    TomeEnchantments.init()
    EndermitesFormShulkers.init()

    // Nature
    FlattenableBlockRegistry.register(ModBlocks.pinkGrass, Blocks.DIRT_PATH.defaultState)
    FlattenableBlockRegistry.register(autumnGrass, Blocks.DIRT_PATH.defaultState)
    FlattenableBlockRegistry.register(blueGrass, Blocks.DIRT_PATH.defaultState)

    RECIPE_HANDLERS.forEach(RecipeHandler::registerSerializers)
  }

  internal fun bootstrapFeatures(featureRegisterable: Registerable<ConfiguredFeature<*, *>>) {
    featureRegisterable.register(
      sakuraSapling.treeFeature,
      ConfiguredFeature(
        Feature.TREE,
        TreeFeatureConfig.Builder(
          BlockStateProvider.of(Blocks.SPRUCE_LOG),
          LargeOakTrunkPlacer(3, 11, 0),
          BlockStateProvider.of(sakuraSapling.leaves),
          LargeOakFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(4), 4),
          TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
        ).ignoreVines().build()
      )
    )

    featureRegisterable.register(
      mapleSapling.treeFeature,
      ConfiguredFeature(
        Feature.TREE,
        TreeFeatureConfig.Builder(
          BlockStateProvider.of(Blocks.OAK_LOG),
          LargeOakTrunkPlacer(3, 9, 0),
          BlockStateProvider.of(mapleSapling.leaves),
          LargeOakFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(4), 4),
          TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
        ).ignoreVines().build()
      )
    )

    featureRegisterable.register(
      blueSapling.treeFeature,
      ConfiguredFeature(
        Feature.TREE,
        TreeFeatureConfig.Builder(
          BlockStateProvider.of(Blocks.BIRCH_LOG),
          LargeOakTrunkPlacer(3, 8, 0),
          BlockStateProvider.of(blueSapling.leaves),
          LargeOakFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(4), 4),
          TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
        ).ignoreVines().build()
      )
    )
  }

  private fun registerIronChest(variant: IronStorageVariant) {
    with (variant) {
      // Register the block and item
      val chestBlock = rBlock(chestId, IronChestBlock(chestSettings(), this))
      rItem(chestBlock, ::BlockItem)

      // Register the block entity and screen handler
      rBlockEntity(chestId, chestBlock) { pos, state -> IronChestBlockEntity(this, pos, state) }
      register(SCREEN_HANDLER, ModId(chestId), chestScreenHandlerType)
    }
  }

  private fun registerIronShulker(variant: IronStorageVariant, color: DyeColor? = null) {
    with (variant) {
      val id = if (color != null) "${shulkerId}_${color.getName()}" else shulkerId

      // Register the block and item
      val shulkerBlock = rBlock(id, IronShulkerBlock(shulkerSettings(color), this, color))
      rItem(shulkerBlock, ::IronShulkerItem, itemSettings().maxCount(1))
    }
  }

  private fun registerIronShulkerBlockEntities(variant: IronStorageVariant) {
    with (variant) {
      val blocks = mutableSetOf(shulkerBlock)
      blocks.addAll(dyedShulkerBlocks.values)

      // Register the block entity
      rBlockEntity(shulkerId, *blocks.toTypedArray())
        { pos, state -> IronShulkerBlockEntity(this, pos, state) }

      register(SCREEN_HANDLER, ModId(shulkerId), shulkerScreenHandlerType)
    }
  }

  private fun registerIronBarrel(variant: IronStorageVariant) {
    with (variant) {
      // Register the block and item
      val barrelBlock = rBlock(barrelId, IronBarrelBlock(barrelSettings(), this))
      rItem(barrelBlock, ::BlockItem)

      // Register the block entity (screen handler is shared with chest)
      rBlockEntity(barrelId, barrelBlock) { pos, state -> IronBarrelBlockEntity(this, pos, state) }
    }
  }

  object ModBlocks {
    val enderStorage = rBlock("ender_storage", EnderStorageBlock(AbstractBlock.Settings
      .of(Material.STONE).requiresTool().strength(12.5f, 600.0f)))

    val pinkGrass = rBlock("pink_grass", ScGrass(grassSettings(MapColor.PINK)))
    val autumnGrass = rBlock("autumn_grass", ScGrass(grassSettings(MapColor.ORANGE)))
    val blueGrass = rBlock("blue_grass", ScGrass(grassSettings(MapColor.LIGHT_BLUE)))

    val sakuraSapling = registerSapling("sakura")
    val mapleSapling = registerSapling("maple")
    val blueSapling = registerSapling("blue")

    fun <T : Block> rBlock(name: String, value: T): T =
      register(BLOCK, ModId(name), value)

    fun blockSettings(): AbstractBlock.Settings = AbstractBlock.Settings
      .of(Material.STONE)
      .strength(2.0f)
      .nonOpaque()

    fun chestSettings(): AbstractBlock.Settings = AbstractBlock.Settings
      .of(Material.STONE)
      .strength(2.0f)
      .requiresTool()
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

    fun barrelSettings(): AbstractBlock.Settings = AbstractBlock.Settings
      .of(Material.STONE)
      .strength(2.0f)
      .requiresTool()
      .nonOpaque()

    private fun leavesSettings(): AbstractBlock.Settings = AbstractBlock.Settings
      .of(Material.LEAVES)
      .strength(0.2f)
      .ticksRandomly()
      .sounds(GRASS)
      .nonOpaque()
      .allowsSpawning { _, _, _, _ -> false }
      .suffocates { _, _, _ -> false }
      .blockVision { _, _, _ -> false }

    private fun saplingSettings(): AbstractBlock.Settings = AbstractBlock.Settings
      .of(Material.PLANT)
      .noCollision()
      .ticksRandomly()
      .breakInstantly()
      .sounds(GRASS)

    private fun potSettings(): AbstractBlock.Settings = AbstractBlock.Settings.copy(Blocks.POTTED_OAK_SAPLING)

    private fun grassSettings(color: MapColor): AbstractBlock.Settings = AbstractBlock.Settings
      .of(SOLID_ORGANIC, color)
      .ticksRandomly()
      .strength(0.6f)
      .sounds(GRASS)

    private fun registerSapling(name: String): ScTree {
      val feature = RegistryKey.of(CONFIGURED_FEATURE, ModId("${name}_tree"))
      val sapling = rBlock("${name}_sapling", SaplingBlock(ScSaplingGenerator(feature), saplingSettings()))
      val leaves = rBlock("${name}_leaves", LeavesBlock(leavesSettings()))
      val potted = rBlock("potted_${name}_sapling", FlowerPotBlock(sapling, potSettings()))
      val saplingItem = rItem(sapling, ::BlockItem, itemSettings())
      val leavesItem = rItem(leaves, ::BlockItem, itemSettings())

      val tree = ScTree(
        sapling,
        leaves,
        feature,
        potted,
        saplingItem
      )
      tree.registerTreeLoot()
      return tree
    }
  }

  object ModItems {
    val itemGroup: ItemGroup = FabricItemGroup.builder(ModId("main"))
      .icon { ItemStack(Items.AXOLOTL_BUCKET) }
      .entries { _, entries ->
        items.forEach(entries::add)
        entries.addAll(AncientTomeItem.getTomeStacks())
      }
      .build()

    val barrelHammer = rItem("barrel_hammer", BarrelHammerItem(itemSettings()
      .maxDamage(64)))

    val enderStorage = rItem(ModBlocks.enderStorage, ::BlockItem, itemSettings())

    val hoverBoots = DyeColor.values().associateWith {
      rItem("hover_boots_${it.getName()}", HoverBootsItem(it, itemSettings().maxCount(1)))
    }

    val itemMagnet = rItem("item_magnet", ItemMagnetItem(itemSettings()
      .maxDamage(MAGNET_MAX_DAMAGE)))
    val dragonScale = rItem("dragon_scale", BaseItem(itemSettings()
      .maxCount(16)
      .rarity(EPIC)))
    val popcorn = rItem("popcorn", PopcornItem(itemSettings()
      .food(PopcornItem.foodComponent)
      .maxCount(1)))
    val ancientTome = rItem("ancient_tome", AncientTomeItem(itemSettings()
      .maxCount(1)
      .rarity(UNCOMMON)))

    // TODO: Clean up
    val pinkGrass = rItem(ModBlocks.pinkGrass, ::BlockItem, itemSettings())
    val autumnGrass = rItem(ModBlocks.autumnGrass, ::BlockItem, itemSettings())
    val blueGrass = rItem(ModBlocks.blueGrass, ::BlockItem, itemSettings())

    val glassItemFrame = rItem("glass_item_frame", GlassItemFrameItem(itemSettings(), ::GlassItemFrameEntity))
    val glowGlassItemFrame = rItem("glow_glass_item_frame", GlassItemFrameItem(itemSettings()) { world, pos, facing ->
      GlassItemFrameEntity(world, pos, facing)
        .apply { dataTracker.set(GlassItemFrameEntity.isGlowingFrame, true) }
    })

    fun <T : Item> rItem(name: String, value: T): T =
      register(ITEM, ModId(name), value).also { items.add(it) }

    fun <B : Block, I : Item> rItem(parent: B, supplier: (B, Item.Settings) -> I,
                                    settings: Item.Settings = itemSettings()): I {
      val item = register(ITEM, BLOCK.getId(parent), supplier(parent, settings))
      Item.BLOCK_ITEMS[parent] = item
      items.add(item)
      return item
    }

    fun itemSettings(): FabricItemSettings = FabricItemSettings()

    fun elytraSettings(): FabricItemSettings = itemSettings()
      .maxDamage(432)
      .rarity(UNCOMMON)
      .equipmentSlot { EquipmentSlot.CHEST }
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

  object ModEntities {
    val glassItemFrameEntity = register(ENTITY_TYPE, ModId("glass_item_frame"),
      FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::GlassItemFrameEntity)
        .dimensions(EntityDimensions.changing(0.5f, 0.5f))
        .trackRangeChunks(10)
        .trackedUpdateRate(Integer.MAX_VALUE)
        .forceTrackedVelocityUpdates(false)
        .build())
  }
}
