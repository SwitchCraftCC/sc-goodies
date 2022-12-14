package pw.switchcraft.goodies.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.registry.RegistryBuilder
import net.minecraft.registry.RegistryKeys
import org.slf4j.LoggerFactory
import pw.switchcraft.goodies.Registration
import pw.switchcraft.goodies.datagen.recipes.RecipeGenerator

object ScGoodiesDatagen : DataGeneratorEntrypoint {
  val log = LoggerFactory.getLogger("ScGoodies/ScGoodiesDatagen")!!

  override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
    log.info("sc-goodies datagen initializing")

    val pack = generator.createPack()
    pack.addProvider(::ItemTagProvider)
    pack.addProvider(::ModelProvider)
    pack.addProvider(::BlockLootTableProvider)
    pack.addProvider(::RecipeGenerator)
    pack.addProvider(::LanguageProvider)
    pack.addProvider(::WorldgenProvider)
  }

  override fun buildRegistry(registryBuilder: RegistryBuilder) {
    registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, Registration::bootstrapFeatures)
  }
}
