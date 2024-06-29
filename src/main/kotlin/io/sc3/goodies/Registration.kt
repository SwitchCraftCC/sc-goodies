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
import io.sc3.goodies.ScGoodies.modId
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
import io.sc3.goodies.misc.*
import io.sc3.goodies.nature.ScGrass
import io.sc3.goodies.nature.ScSaplingGenerator
import io.sc3.goodies.nature.ScTree
import io.sc3.goodies.seats.SeatEntity
import io.sc3.goodies.shark.DyedSharkItem
import io.sc3.goodies.shark.SpecialSharkItem
import io.sc3.goodies.shark.SpecialSharkType
import io.sc3.goodies.tomes.AncientTomeItem
import io.sc3.goodies.tomes.TomeEnchantments
import io.sc3.goodies.util.BaseItem
import io.sc3.goodies.util.niceDyeOrder
import io.sc3.library.networking.registerServerReceiver
import io.sc3.library.recipe.RecipeHandler
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry
import net.fabricmc.fabric.api.registry.TillableBlockRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.*
import net.minecraft.block.AbstractBlock.ContextPredicate
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.damage.DamageType
import net.minecraft.item.*
import net.minecraft.registry.Registerable
import net.minecraft.registry.Registries.*
import net.minecraft.registry.Registry.register
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryKeys.CONFIGURED_FEATURE
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.BlockSoundGroup.GRASS
import net.minecraft.text.Text
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
  private val itemGroup = RegistryKey.of(RegistryKeys.ITEM_GROUP, ModId("main"))

  internal fun init() {
    register(ITEM_GROUP, itemGroup, FabricItemGroup.builder()
      .displayName(Text.translatable("itemGroup.$modId.main"))
      .icon { ItemStack(Items.AXOLOTL_BUCKET) }
      .entries { _, entries ->
        items.forEach(entries::add)
        entries.addAll(AncientTomeItem.getTomeStacks())
      }
      .build())

    // Force static initializers to run
    listOf(ModBlocks, ModItems, ModBlockEntities, ModScreens, ModEntities, ModDamageSources)

    // Iron Chests and Shulkers
    IronStorageVariant.entries.forEach { variant ->
      registerIronChest(variant)

      registerIronShulker(variant) // Undyed shulker
      niceDyeOrder.forEach { registerIronShulker(variant, it) }

      // Shulker block entities, done in bulk for each dyed variant + undyed
      registerIronShulkerBlockEntities(variant)

      registerIronBarrel(variant)
    }

    IronStorageUpgrade.entries.forEach { upgrade ->
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
    niceDyeOrder
      .forEach { rItem("elytra_${it.getName()}", DyedElytraItem(it, elytraSettings())) }
    SpecialElytraType.entries
      .forEach { rItem("elytra_${it.type}", SpecialElytraItem(it, elytraSettings())) }
    ElytraCauldronBehavior.registerBehavior()

    // Dyed + Special Sharks
    niceDyeOrder
      .forEach { rItem("shark_${it.getName()}", DyedSharkItem(it, itemSettings())) }
    SpecialSharkType.entries
      .forEach { rItem("shark_${it.type}", SpecialSharkItem(it, itemSettings())) }

    // Concrete Slabs and Stairs
    ConcreteExtras.colors.values.forEach {
      val slabBlock = rBlock(it.slabBlockId.path,
        SlabBlock(AbstractBlock.Settings.copy(it.baseBlock)))
      rItem(slabBlock, ::BlockItem)

      val stairsBlock = rBlock(it.stairsBlockId.path, StairsBlock(it.baseBlock.defaultState,
        AbstractBlock.Settings.copy(it.baseBlock)))
      rItem(stairsBlock, ::BlockItem)
    }
    // Pretty Slabs and Stairs
    val amethystSlab = rBlock(
      AmethystExtras.slabBlockId.path,
      SlabBlock(AbstractBlock.Settings.copy(AmethystExtras.baseBlock)))
    rItem(amethystSlab, ::BlockItem)
    val amethystStairs = rBlock(
      AmethystExtras.stairsBlockId.path,
      StairsBlock(AmethystExtras.baseBlock.defaultState,
        AbstractBlock.Settings.copy(AmethystExtras.baseBlock))
    )
    rItem(amethystStairs, ::BlockItem)

    TomeEnchantments.init()
    EndermitesFormShulkers.init()

    // Nature
    FlattenableBlockRegistry.register(ModBlocks.pinkGrass, Blocks.DIRT_PATH.defaultState)
    FlattenableBlockRegistry.register(autumnGrass, Blocks.DIRT_PATH.defaultState)
    FlattenableBlockRegistry.register(blueGrass, Blocks.DIRT_PATH.defaultState)

    TillableBlockRegistry.register(
      ModBlocks.pinkGrass,
      HoeItem::canTillFarmland,
      HoeItem.createTillAction(Blocks.FARMLAND.defaultState)
    )
    TillableBlockRegistry.register(
      autumnGrass,
      HoeItem::canTillFarmland,
      HoeItem.createTillAction(Blocks.FARMLAND.defaultState)
    )
    TillableBlockRegistry.register(
      blueGrass,
      HoeItem::canTillFarmland,
      HoeItem.createTillAction(Blocks.FARMLAND.defaultState)
    )

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

  fun bootstrapDamageTypes(damageTypeRegisterable: Registerable<DamageType?>) {
    damageTypeRegisterable.register(ModDamageSources.barrelHammer, DamageType("sc-goodies.barrel_hammer", 0.1f))
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
      .copy(Blocks.ENDER_CHEST)
      .luminance { 0 }
      .strength(12.5f, 600.0f)))

    val dimmableLight = rBlock("dimmable_light", DimmableLight(AbstractBlock.Settings
      .copy(Blocks.REDSTONE_LAMP)
      .luminance(DimmableLight.createLightLevelFromPowerState())
      .strength(0.5F)
      .sounds(BlockSoundGroup.GLASS)))

    val pinkGrass = rBlock("pink_grass", ScGrass(grassSettings(MapColor.PINK)))
    val autumnGrass = rBlock("autumn_grass", ScGrass(grassSettings(MapColor.ORANGE)))
    val blueGrass = rBlock("blue_grass", ScGrass(grassSettings(MapColor.LIGHT_BLUE)))

    val sakuraSapling = registerSapling("sakura", MapColor.PINK)
    val mapleSapling = registerSapling("maple", MapColor.ORANGE)
    val blueSapling = registerSapling("blue", MapColor.LIGHT_BLUE)

    fun <T : Block> rBlock(name: String, value: T): T =
      register(BLOCK, ModId(name), value)

    fun chestSettings(): AbstractBlock.Settings = AbstractBlock.Settings.create()
      .mapColor(MapColor.STONE_GRAY)
      .strength(2.0f)
      .requiresTool()
      .nonOpaque()

    fun shulkerSettings(color: DyeColor?): AbstractBlock.Settings {
      val predicate = ContextPredicate { _, world, pos ->
        val be = world.getBlockEntity(pos) as? IronShulkerBlockEntity
        be?.suffocates() ?: true
      }

      return AbstractBlock.Settings.create()
        .mapColor(color?.mapColor ?: MapColor.PURPLE)
        .pistonBehavior(PistonBehavior.DESTROY)
        .strength(2.0f)
        .dynamicBounds()
        .nonOpaque()
        .suffocates(predicate)
        .blockVision(predicate)
    }

    fun barrelSettings() = chestSettings()

    private fun leavesSettings(mapColor: MapColor): AbstractBlock.Settings = AbstractBlock.Settings.create()
      .mapColor(mapColor)
      .sounds(GRASS)
      .strength(0.2f)
      .ticksRandomly()
      .nonOpaque()
      .allowsSpawning { _, _, _, _ -> false }
      .suffocates { _, _, _ -> false }
      .blockVision { _, _, _ -> false }

    private fun saplingSettings(mapColor: MapColor): AbstractBlock.Settings = AbstractBlock.Settings.create()
      .mapColor(mapColor)
      .sounds(GRASS)
      .pistonBehavior(PistonBehavior.DESTROY)
      .noCollision()
      .ticksRandomly()
      .breakInstantly()
      .sounds(GRASS)

    private fun potSettings(): AbstractBlock.Settings = AbstractBlock.Settings.copy(Blocks.POTTED_OAK_SAPLING)

    private fun grassSettings(mapColor: MapColor): AbstractBlock.Settings = AbstractBlock.Settings.create()
      .mapColor(mapColor)
      .sounds(GRASS)
      .ticksRandomly()
      .strength(0.6f)

    private fun registerSapling(name: String, mapColor: MapColor): ScTree {
      val feature = RegistryKey.of(CONFIGURED_FEATURE, ModId("${name}_tree"))
      val sapling = rBlock("${name}_sapling", SaplingBlock(ScSaplingGenerator(feature), saplingSettings(mapColor)))
      val leaves = rBlock("${name}_leaves", LeavesBlock(leavesSettings(mapColor)))
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
    val stairWrench = rItem("stair_wrench", StairWrenchItem(itemSettings()
      .maxCount(1)))

    val barrelHammer = rItem("barrel_hammer", BarrelHammerItem(itemSettings()
      .maxDamage(64)))

    val enderStorage = rItem(ModBlocks.enderStorage, ::EnderStorageItem, itemSettings())

    val hoverBoots = niceDyeOrder.associateWith {
      rItem("hover_boots_${it.getName()}", HoverBootsItem(it, itemSettings().maxCount(1)))
    }

    val itemMagnet = rItem("item_magnet", ItemMagnetItem(itemSettings()
      .maxDamage(MAGNET_MAX_DAMAGE)))
    val dragonScale = rItem("dragon_scale", BaseItem(itemSettings()
      .maxCount(16)
      .rarity(EPIC)))
    val ancientTome = rItem("ancient_tome", AncientTomeItem(itemSettings()
      .maxCount(1)
      .rarity(UNCOMMON)))

    val salami = rItem("salami", BaseItem(itemSettings().food(FoodComponent.Builder()
      .hunger(3)
      .saturationModifier(1.2f)
      .alwaysEdible()
      .snack()
      .build())))
    
    val popcorn = rItem("popcorn", PopcornItem(itemSettings()
      .food(PopcornItem.foodComponent)
      .maxCount(1)))

    val iceCreamVanilla = rIceCreamItem("icecream_vanilla")
    val iceCreamChocolate = rIceCreamItem("icecream_chocolate")
    val iceCreamSweetBerry = rIceCreamItem("icecream_sweetberry")
    val iceCreamNeapolitan = rIceCreamItem("icecream_neapolitan")
    val iceCreamSpruce = rIceCreamItem("icecream_spruce")
    val iceCreamMelon = rIceCreamItem("icecream_melon")
    val iceCreamBeetroot = rIceCreamItem("icecream_beetroot")
    val iceCreamSundae = rIceCreamItem("icecream_sundae", FoodComponent.Builder()
      .hunger(7)
      .saturationModifier(8.0f)
      .alwaysEdible()
      .build())

    val dimmableLight = rItem(ModBlocks.dimmableLight, ::BlockItem, itemSettings())

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

    private fun rIceCreamItem(name: String, foodComponent: FoodComponent = IceCreamItem.foodComponent): Item =
      rItem(name, IceCreamItem(itemSettings()
        .food(foodComponent)
        .maxCount(16)))

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
    val glassItemFrameEntity: EntityType<GlassItemFrameEntity> = register(ENTITY_TYPE, ModId("glass_item_frame"),
      FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::GlassItemFrameEntity)
        .dimensions(EntityDimensions.changing(0.5f, 0.5f))
        .trackRangeChunks(10)
        .trackedUpdateRate(Integer.MAX_VALUE)
        .forceTrackedVelocityUpdates(false)
        .build())

    val seatEntity: EntityType<SeatEntity> = register(ENTITY_TYPE, ModId("seat"),
      FabricEntityTypeBuilder.create(SpawnGroup.MISC, ::SeatEntity)
        .dimensions(EntityDimensions.fixed(0.125f, 0.0f))
        .trackRangeChunks(10)
        .trackedUpdateRate(Integer.MAX_VALUE)
        .forceTrackedVelocityUpdates(false)
        .build())
  }

  object ModDamageSources {
    val barrelHammer: RegistryKey<DamageType?> =
      RegistryKey.of(RegistryKeys.DAMAGE_TYPE, ModId("barrel_hammer"))
  }
}
