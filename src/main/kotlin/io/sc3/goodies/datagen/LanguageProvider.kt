package io.sc3.goodies.datagen

import io.sc3.goodies.Registration
import io.sc3.goodies.Registration.ModBlocks
import io.sc3.goodies.Registration.ModItems
import io.sc3.goodies.ScGoodies.modId
import io.sc3.goodies.elytra.DyedElytraItem
import io.sc3.goodies.elytra.SpecialElytraType
import io.sc3.goodies.ironstorage.IronStorageUpgrade
import io.sc3.goodies.ironstorage.IronStorageVariant
import io.sc3.goodies.misc.AmethystExtras
import io.sc3.goodies.misc.ConcreteExtras
import io.sc3.goodies.nature.ScTree
import io.sc3.goodies.shark.DyedSharkItem
import io.sc3.goodies.shark.SpecialSharkType
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.DyeColor.*

class LanguageProvider(out: FabricDataOutput) : FabricLanguageProvider(out) {
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
    builder.add("itemGroup.$modId.main", "SwitchCraft Goodies")
    builder.add("category.$modId", "SwitchCraft Goodies") // Keybind category

    // Iron Chests and Shulkers
    builder.add("block.$modId.storage.desc", "Capable of storing up to %s stacks of items.")

    IronStorageVariant.entries.forEach { variant ->
      builder.add(variant.chestBlock, "${variant.humanName} Chest")
      builder.add(variant.shulkerBlock, "${variant.humanName} Shulker Box")
      variant.dyedShulkerBlocks.forEach { (color, block) ->
        builder.add(block, "${colorNames[color]} ${variant.humanName} Shulker Box")
      }
      builder.add(variant.barrelBlock, "${variant.humanName} Barrel")
    }

    IronStorageUpgrade.entries.forEach { upgrade ->
      val from = upgrade.from?.humanName ?: "Vanilla"
      val to = upgrade.to.humanName

      builder.add(upgrade.upgradeItem, "$from to $to Storage Upgrade")
      builder.sub(upgrade.upgradeItem, "Upgrade $from chests, barrels, or shulker boxes to\n$to chests, barrels, or shulker boxes.")
    }

    builder.add(ModItems.stairWrench, "Stair Wrench")
    builder.sub(ModItems.stairWrench, "Mode: %s", "mode")
    builder.sub(ModItems.stairWrench, "Use on stairs to cycle through property values.\nUse while sneaking to cycle through the property to manipulate.")
    builder.sub(ModItems.stairWrench, "Invalid property: %s", "invalid")

    builder.add(ModItems.barrelHammer, "Barrel Hammer")
    builder.sub(ModItems.barrelHammer, "Use while sneaking to convert chests into barrels and vice versa.\nAlso works on Iron, Gold and Diamond Chests and Barrels.")
    builder.add("death.attack.$modId.barrel_hammer", "%s was bonked by %s")
    builder.add("death.attack.$modId.barrel_hammer.item", "%s was bonked by %s using %s")

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
    builder.sub(es, "Personal (owner: %s)", "personal")
    builder.sub(es, "That Ender Storage does not exist.", "not_found")
    builder.sub(es, "Frequency: %s, %s, %s", "frequency")
    builder.sub(es, "That name is not valid.", "invalid_name")
    builder.sub(es, "That description is not valid.", "invalid_description")
    builder.sub(es, "Changed the name for your %s, %s, %s Ender Storage to: %s", "changed_name_own")
    builder.sub(es, "Changed the name for the public %s, %s, %s Ender Storage to: %s", "changed_name_public")
    builder.sub(es, "Changed the name for %s's personal %s, %s, %s Ender Storage to: %s", "changed_name_private")
    builder.sub(es, "Changed the description for your %s, %s, %s Ender Storage to: %s", "changed_description_own")
    builder.sub(es, "Changed the description for the public %s, %s, %s Ender Storage to: %s", "changed_description_public")
    builder.sub(es, "Changed the description for %s's personal %s, %s, %s Ender Storage to: %s", "changed_description_private")
    builder.sub(es, "Cleared the name for your %s, %s, %s Ender Storage.", "cleared_name_own")
    builder.sub(es, "Cleared the name for the public %s, %s, %s Ender Storage.", "cleared_name_public")
    builder.sub(es, "Cleared the name for %s's personal %s, %s, %s Ender Storage.", "cleared_name_private")
    builder.sub(es, "Cleared the description for your %s, %s, %s Ender Storage.", "cleared_description_own")
    builder.sub(es, "Cleared the description for the public %s, %s, %s Ender Storage.", "cleared_description_public")
    builder.sub(es, "Cleared the description for %s's personal %s, %s, %s Ender Storage.", "cleared_description_private")
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
      "Upgrade with Nether Stars and Netherite Ingots.\n" +
      "Place in an Item Frame to disable nearby magnets.")
    builder.sub(im, "Magnet enabled (press %s to toggle in-game)", "enabled")
    builder.sub(im, "Magnet disabled (press %s to toggle in-game)", "disabled")
    builder.sub(im, "Magnet blocked (other players are nearby)", "blocked_player")
    builder.sub(im, "Magnet blocked (magnet item frame is nearby)", "blocked_item_frame")
    builder.sub(im, "Level %s (%s block radius)", "level")
    builder.sub(im, "Charge: %s/%s", "charge")
    builder.add("key.$modId.toggle_item_magnet", "Toggle Item Magnet")

    // Elytra
    DyedElytraItem.dyedElytraItems
      .forEach { (color, item) -> builder.add(item, "${colorNames[color]} Elytra") }
    SpecialElytraType.entries
      .forEach { builder.add(it.item, "${it.humanName} Elytra") }

    // Sharks
    DyedSharkItem.dyedSharkItems.forEach { (color, item) ->
      builder.add(item, "${colorNames[color]} Soft Toy Shark")
      // Include the accent-less name in the description too, to make it easier to search for in REI.
      builder.sub(item, "A soft toy shark, also known as a BLÅHAJ (Blahaj).", "desc")
    }
    SpecialSharkType.entries.forEach {
      builder.add(it.item, "${it.humanName} Soft Toy Shark")
      builder.sub(it.item, "A soft toy shark, also known as a BLÅHAJ (Blahaj).", "desc")
    }

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

    builder.add(ModItems.iceCreamVanilla, "Vanilla Ice Cream")
    builder.sub(ModItems.iceCreamVanilla, "A delicious treat!")
    builder.add(ModItems.iceCreamChocolate, "Chocolate Ice Cream")
    builder.sub(ModItems.iceCreamChocolate, "A delicious treat!")
    builder.add(ModItems.iceCreamSweetBerry, "Sweet Berry Ice Cream")
    builder.sub(ModItems.iceCreamSweetBerry, "A delicious treat!")
    builder.add(ModItems.iceCreamNeapolitan, "Neapolitan Ice Cream")
    builder.sub(ModItems.iceCreamNeapolitan, "Vanilla, Chocolate, and Sweet Berry.")
    builder.add(ModItems.iceCreamSpruce, "Spruce Ice Cream")
    builder.sub(ModItems.iceCreamSpruce, "A refreshing treat!")
    builder.add(ModItems.iceCreamMelon, "Melon Ice Cream")
    builder.sub(ModItems.iceCreamMelon, "A delicious treat!")
    builder.add(ModItems.iceCreamBeetroot, "Beetroot Ice Cream")
    builder.sub(ModItems.iceCreamBeetroot, "An earthy treat!")
    builder.add(ModItems.iceCreamSundae, "Triple Gooberberry Sunrise")
    builder.sub(ModItems.iceCreamSundae, "A suspiciously familiar sundae.")

    builder.add(ModItems.glassItemFrame, "Glass Item Frame")
    builder.sub(ModItems.glassItemFrame, "An item frame with an invisible background when it contains an item.")
    builder.add(ModItems.glowGlassItemFrame, "Glow Glass Item Frame")
    builder.sub(ModItems.glowGlassItemFrame, "A glowing item frame with an invisible background when it contains an item.")
    builder.add(Registration.ModEntities.glassItemFrameEntity, "Glass Item Frame")

    builder.add(ModItems.salami, "Salami")
    builder.sub(ModItems.salami, "Yummy salami")

    builder.add(ModBlocks.sakuraSapling, "Sakura")
    builder.add(ModBlocks.mapleSapling, "Maple")
    builder.add(ModBlocks.blueSapling, "Peppy")
    builder.add(ModBlocks.pinkGrass, "Pink Grass")
    builder.add(ModBlocks.autumnGrass, "Autumn Grass")
    builder.add(ModBlocks.blueGrass, "Peppy Grass")

    builder.add(ModBlocks.dimmableLight, "Dimmable Lamp")
    builder.sub(ModBlocks.dimmableLight, "Emits an amount of light equivalent to the block's redstone input")

    // Concrete Slabs and Stairs
    ConcreteExtras.colors.values.forEach {
      builder.add(it.slabBlock, colorNames[it.color] + " Concrete Slab")
      builder.add(it.stairsBlock, colorNames[it.color] + " Concrete Stairs")
    }

    // Amethyst Slabs and Stairs
    builder.add(AmethystExtras.slabBlock, "Amethyst Slab")
    builder.add(AmethystExtras.stairsBlock, "Amethyst Stairs")

    // Seats
    builder.add("block.$modId.seat.occupied.unknown", "This seat is occupied")
    builder.add("block.$modId.seat.occupied.player", "This seat is occupied by %s")
    builder.add("block.$modId.seat.mount", "Press %1\$s to stand up")
  }

  private fun TranslationBuilder.sub(item: Item, value: String, sub: String = "desc") {
    add(item.translationKey + ".$sub", value)
  }

  private fun TranslationBuilder.sub(block: Block, value: String, sub: String = "desc") {
    add(block.translationKey + ".$sub", value)
  }

  private fun TranslationBuilder.add(tree: ScTree, name: String) {
    add(tree.leaves, "$name Leaves")
    add(tree.sapling, "$name Sapling")
    add(tree.potted, "Potted $name Sapling")
  }
}
