package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.slf4j.LoggerFactory
import pw.switchcraft.goodies.datagen.recipes.RecipeGenerator

object ScGoodiesDatagen : DataGeneratorEntrypoint {
  val log = LoggerFactory.getLogger("ScGoodies/ScGoodiesDatagen")!!

  override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
    log.info("sc-goodies datagen initializing")

    generator.addProvider(ItemTagProvider(generator))
    generator.addProvider(BlockModelProvider(generator))
    generator.addProvider(ItemModelProvider(generator))
    generator.addProvider(BlockLootTableProvider(generator))
    generator.addProvider(RecipeGenerator(generator))
    generator.addProvider(LanguageProvider(generator))
  }
}
