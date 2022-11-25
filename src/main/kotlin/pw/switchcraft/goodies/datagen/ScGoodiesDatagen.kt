package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.slf4j.LoggerFactory
import pw.switchcraft.goodies.datagen.recipes.RecipeGenerator

object ScGoodiesDatagen : DataGeneratorEntrypoint {
  val log = LoggerFactory.getLogger("ScGoodies/ScGoodiesDatagen")!!

  override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
    log.info("sc-goodies datagen initializing")

    val pack = generator.createPack()
    pack.addProvider(::ItemTagProvider)
    pack.addProvider(::BlockModelProvider)
    pack.addProvider(::ItemModelProvider)
    pack.addProvider(::BlockLootTableProvider)
    pack.addProvider(::RecipeGenerator)
    pack.addProvider(::LanguageProvider)
  }
}
