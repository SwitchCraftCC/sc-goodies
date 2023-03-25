package io.sc3.goodies.datagen

import io.sc3.goodies.Registration
import io.sc3.goodies.datagen.recipes.RecipeGenerator
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.registry.RegistryBuilder
import net.minecraft.registry.RegistryKeys
import org.slf4j.LoggerFactory

object ScGoodiesDatagen : DataGeneratorEntrypoint {
  val log = LoggerFactory.getLogger("ScGoodies/ScGoodiesDatagen")!!

  override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
    log.info("sc-goodies datagen initializing")

    val pack = generator.createPack()
    pack.addProvider(::BlockTagProvider)
    pack.addProvider(::ItemTagProvider)
    pack.addProvider(::DamageTypeTagProvider)
    pack.addProvider(::ModelProvider)
    pack.addProvider(::BlockLootTableProvider)
    pack.addProvider(::RecipeGenerator)
    pack.addProvider(::LanguageProvider)
    pack.addProvider(::DynamicRegistryProvider)
  }

  override fun buildRegistry(registryBuilder: RegistryBuilder) {
    registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, Registration::bootstrapFeatures)
    registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, Registration::bootstrapDamageTypes)
  }
}
