package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.DyeColor
import net.minecraft.util.DyeColor.*
import net.minecraft.util.registry.Registry.ITEM
import pw.switchcraft.goodies.Registration.ModBlocks
import pw.switchcraft.goodies.Registration.ModItems
import pw.switchcraft.goodies.Registration.ModItems.itemGroup
import pw.switchcraft.goodies.ScGoodies.ModId
import pw.switchcraft.goodies.elytra.SpecialElytraType
import pw.switchcraft.goodies.ironchest.IronChestUpgrade
import pw.switchcraft.goodies.ironchest.IronChestVariant
import pw.switchcraft.goodies.misc.ConcreteExtras

class LanguageProvider(gen: FabricDataGenerator) : FabricLanguageProvider(gen) {
  private val colorNames = mapOf(
    WHITE      to "White",
    ORANGE     to "Orange",
    MAGENTA    to "Magenta",
    LIGHT_BLUE to "Light Blue",
    YELLOW     to "Yellow",
    LIME       to "Lime",
    PINK       to "Pink",
    GRAY       to "Gray",
    LIGHT_GRAY to "Light Gray",
    CYAN       to "Cyan",
    PURPLE     to "Purple",
    BLUE       to "Blue",
    BROWN      to "Brown",
    GREEN      to "Green",
    RED        to "Red",
    BLACK      to "Black"
  )

  override fun generateTranslations(builder: TranslationBuilder) {
    builder.add(itemGroup, "SwitchCraft Goodies")
    builder.add("category.sc-goodies", "SwitchCraft Goodies") // Keybind category

    // Iron Chests and Shulkers
    builder.add("block.sc-goodies.storage.desc", "Capable of storing up to %s stacks of items.")

    IronChestVariant.values().forEach { variant ->
      builder.add(variant.chestBlock, "${variant.humanName} Chest")
      builder.add(variant.shulkerBlock, "${variant.humanName} Shulker Box")
      variant.dyedShulkerBlocks.forEach { (color, block) ->
        builder.add(block, "${colorNames[color]} ${variant.humanName} Shulker Box")
      }
    }

    IronChestUpgrade.values().forEach { upgrade ->
      val from = upgrade.from?.humanName ?: "Wood"
      val to = upgrade.to.humanName
      val toDesc = to.lowercase().addArticle()

      val chestFromDesc = (upgrade.from?.humanName?.lowercase() ?: "vanilla wood").addArticle()
      builder.add(upgrade.chestUpgrade, "$from to $to Chest Upgrade")
      builder.sub(upgrade.chestUpgrade, "Upgrade $chestFromDesc chest to $toDesc chest.")

      val shulkerFromDesc = (upgrade.from?.humanName?.lowercase() ?: "vanilla").addArticle()
      builder.add(upgrade.shulkerUpgrade, "$from to $to Shulker Box Upgrade")
      builder.sub(upgrade.shulkerUpgrade, "Upgrade $shulkerFromDesc shulker box to $toDesc shulker box.")
    }

    // Ender Storage
    val es = ModBlocks.enderStorage; val esk = es.translationKey
    builder.add(es, "Ender Storage")
    builder.sub(es, "Advanced Ender Chest that links chests based on color patterns.\n" +
      "Use dye on the lid to change the frequency.\n" +
      "Use a diamond on the latch to use a personal frequency.")
    builder.sub(es, "Use an emerald on the latch to allow ComputerCraft to change the frequency.", "desc.computercraft")
    builder.sub(es, "You are not the owner of this Ender Storage.", "not_owner")
    builder.sub(es, "Computers are now %s to change the frequency of this Ender Storage.", "computer_changes.allowed")
    builder.sub(es, "ALLOWED", "computer_changes.allowed.colored")
    builder.sub(es, "Computers are now %s from changing the frequency of this Ender Storage.", "computer_changes.denied")
    builder.sub(es, "DENIED", "computer_changes.denied.colored")
    builder.sub(es, "Owner: %s", "owner_name")
    builder.sub(es, "Public", "public")
    builder.sub(es, "That Ender Storage does not exist.", "not_found")
    builder.sub(es, "Frequency: %s, %s, %s", "frequency")
    colorNames.forEach { (color, name) -> builder.add("$esk.frequency.${color.getName()}", name) }

    // Hover Boots
    val hb = ModItems.hoverBoots[WHITE]!! // Translation keys are shared between all colors
    builder.add(hb, "Hover Boots")
    builder.sub(hb, "Allows you to jump higher.")

    // Item Magnet
    val im = ModItems.itemMagnet
    builder.add(im, "Item Magnet")
    builder.sub(im, "Vacuums nearby items into your inventory.\n" +
      "Charge with experience orbs.\n" +
      "Upgrade with Nether Stars and Netherite Ingots.")
    builder.sub(im, "Magnet enabled (press %s to toggle in-game)", "enabled")
    builder.sub(im, "Magnet disabled (press %s to toggle in-game)", "disabled")
    builder.sub(im, "Magnet blocked (other players are nearby)", "blocked")
    builder.sub(im, "Level %s (%s block radius)", "level")
    builder.sub(im, "Charge: %s/%s", "charge")
    builder.add("key.sc-goodies.toggle_item_magnet", "Toggle Item Magnet")

    // Elytra
    DyeColor.values()
      .forEach { builder.add(ITEM.get(ModId("elytra_${it.getName()}")), "${colorNames[it]} Elytra") }
    SpecialElytraType.values()
      .forEach { builder.add(ITEM.get(ModId("elytra_${it.type}")), "${it.humanName} Elytra") }

    // Ancient Tome
    val at = ModItems.ancientTome
    builder.add(at, "Ancient Tome")
    builder.sub(at, "Can enchant an item one level beyond the max level.")
    builder.sub(at, "+I %s (max. %s)", "level_tooltip")

    // Misc
    builder.add(ModItems.dragonScale, "Dragon Scale")
    builder.sub(ModItems.dragonScale, "Can clone Elytra in the crafting table.\n" +
      "The scale will be consumed when crafting.")
    builder.add(ModItems.popcorn, "Popcorn")
    builder.sub(ModItems.popcorn, "A bottomless bag of popcorn.")

    builder.add(ModBlocks.sakuraLeaves, "Sakura Leaves")
    builder.add(ModBlocks.sakuraSapling, "Sakura Sapling")
    builder.add(ModBlocks.pottedSakuraSapling, "Potted Sakura Sapling")

    // Concrete Slabs and Stairs
    ConcreteExtras.colors.values.forEach {
      builder.add(it.slabBlock, colorNames[it.color] + " Concrete Slab")
      builder.add(it.stairsBlock, colorNames[it.color] + " Concrete Stairs")
    }
  }

  private fun TranslationBuilder.sub(item: Item, value: String, sub: String = "desc") {
    add(item.translationKey + ".$sub", value)
  }

  private fun TranslationBuilder.sub(block: Block, value: String, sub: String = "desc") {
    add(block.translationKey + ".$sub", value)
  }

  private fun String.addArticle() = if (lowercase().matches(Regex("^[aeiou].*"))) "an $this" else "a $this"
}
