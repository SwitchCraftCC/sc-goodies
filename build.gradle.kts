import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  val kotlinVersion: String by System.getProperties()
  kotlin("jvm").version(kotlinVersion)

  id("fabric-loom") version "1.2-SNAPSHOT"
  id("io.github.juuxel.loom-quiltflower") version "1.10.0"
  id("maven-publish")
  id("signing")
  id("com.modrinth.minotaur") version "2.+"
  id("net.darkhax.curseforgegradle") version "1.0.11"
}

val modVersion: String by project
val mavenGroup: String by project

val minecraftVersion: String by project
val minecraftTargetVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val fabricKotlinVersion: String by project
val fabricVersion: String by project

val ccVersion: String by project
val ccMcVersion: String by project
val ccTargetVersion: String by project

val nightConfigVersion: String by project
val clothConfigVersion: String by project
val clothApiVersion: String by project
val modMenuVersion: String by project

val trinketsVersion: String by project
val cardinalComponentsVersion: String by project

val scLibraryVersion: String by project
val scTextVersion: String by project
val fabricPermissionsApiVersion: String by project

val archivesBaseName = "sc-goodies"
version = modVersion
group = mavenGroup

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "17"
    apiVersion = "1.7"
    languageVersion = "1.7"
  }
}

repositories {
  mavenLocal {
    content {
      includeModule("io.sc3", "sc-library")
      includeModule("io.sc3", "sc-text")
    }
  }

  maven {
    url = uri("https://repo.lem.sh/releases")
    content {
      includeGroup("io.sc3")
    }
  }

  maven("https://squiddev.cc/maven") {
    content {
      includeGroup("cc.tweaked")
      includeModule("org.squiddev", "Cobalt")
    }
  }

  maven("https://maven.terraformersmc.com/releases") // Mod Menu
  maven("https://maven.shedaniel.me") // Cloth Config
  maven("https://ladysnake.jfrog.io/artifactory/mods") // Trinkets
  maven("https://oss.sonatype.org/content/repositories/snapshots") // fabric-permissions-api
}

dependencies {
  minecraft("com.mojang", "minecraft", minecraftVersion)
  mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")
  modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
  modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion) {
    exclude("net.fabricmc.fabric-api", "fabric-gametest-api-v1")
  }
  modImplementation("net.fabricmc", "fabric-language-kotlin", fabricKotlinVersion)

  modImplementation(include("io.sc3", "sc-library", scLibraryVersion))

  modImplementation("cc.tweaked:cc-tweaked-$ccMcVersion-fabric:$ccVersion") {
    exclude("net.fabricmc.fabric-api", "fabric-gametest-api-v1")
    exclude("fuzs.forgeconfigapiport", "forgeconfigapiport-fabric")
  }
  modRuntimeOnly("cc.tweaked:cc-tweaked-$ccMcVersion-fabric:$ccVersion") {
    exclude("net.fabricmc.fabric-api", "fabric-gametest-api-v1")
    exclude("fuzs.forgeconfigapiport", "forgeconfigapiport-fabric")
  }

  implementation(include("com.electronwill.night-config", "core", nightConfigVersion))
  implementation(include("com.electronwill.night-config", "toml", nightConfigVersion))

  modImplementation("dev.emi:trinkets:${trinketsVersion}")

  modApi("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
    exclude("net.fabricmc.fabric-api")
  }
  include("me.shedaniel.cloth", "cloth-config-fabric", clothConfigVersion)
  modImplementation(include("me.shedaniel.cloth.api", "cloth-utils-v1", clothApiVersion))

  modImplementation(include("com.terraformersmc", "modmenu", modMenuVersion))

  modImplementation(include("me.lucko", "fabric-permissions-api", fabricPermissionsApiVersion))

  modImplementation(include("dev.onyxstudios.cardinal-components-api", "cardinal-components-base", cardinalComponentsVersion))
  modImplementation(include("dev.onyxstudios.cardinal-components-api", "cardinal-components-entity", cardinalComponentsVersion))

  modImplementation(include("io.sc3", "sc-text", scTextVersion))
}

tasks {
  processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") { expand(mutableMapOf(
      "version" to project.version,
      "minecraft_target_version" to minecraftTargetVersion,
      "fabric_kotlin_version" to fabricKotlinVersion,
      "loader_version" to loaderVersion,
      "cc_target_version" to ccTargetVersion,
    )) }
  }

  jar {
    from("LICENSE") {
      rename { "${it}_${archivesBaseName}" }
    }

    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
  }

  remapJar {
    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
    destinationDirectory.set(file("${rootDir}/build/final"))
  }

  loom {
    accessWidenerPath.set(file("src/main/resources/sc-goodies.accesswidener"))

    sourceSets {
      main {
        resources {
          srcDir("src/generated/resources")
          exclude("src/generated/resources/.cache")
        }
      }
    }

    runs {
      configureEach {
        property("fabric.debug.disableModShuffle")
      }
      create("datagen") {
        client()
        name("Data Generation")
        vmArgs(
          "-Dfabric-api.datagen",
          "-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}",
          "-Dfabric-api.datagen.modid=${archivesBaseName}"
        )
        runDir("build/datagen")
      }

      named("client") {
        property("mixin.debug.export", "true")
      }
    }
  }
}

modrinth {
  token.set(findProperty("modrinthApiKey") as String? ?: "")
  projectId.set("glA8M6fC")
  versionNumber.set("$minecraftVersion-$modVersion")
  versionName.set(modVersion)
  versionType.set("release")
  uploadFile.set(tasks.remapJar as Any)
  changelog.set("Release notes can be found on the [GitHub repository](https://github.com/SwitchCraftCC/sc-goodies/commits/$minecraftVersion).")
  gameVersions.add(minecraftVersion)
  loaders.add("fabric")

  syncBodyFrom.set(provider {
    file("README.md").readText()
      .replace("img/header.png", "https://cdn.modrinth.com/data/glA8M6fC/images/4a678749f05541893dcba84ea92b8740644e57e9.png")
  })

  dependencies {
    required.project("fabric-api")
    required.project("fabric-language-kotlin")
    required.project("trinkets")
  }
}

tasks.modrinth { dependsOn(tasks.modrinthSyncBody) }
tasks.publish { dependsOn(tasks.modrinth) }

val publishCurseForge by tasks.registering(TaskPublishCurseForge::class) {
  group = PublishingPlugin.PUBLISH_TASK_GROUP
  description = "Upload artifacts to CurseForge"

  apiToken = findProperty("curseForgeApiKey") as String? ?: ""
  enabled = apiToken != ""

  val mainFile = upload("807667", tasks.remapJar.get().archiveFile)
  dependsOn(tasks.remapJar)
  mainFile.releaseType = "release"
  mainFile.changelog = "Release notes can be found on the [GitHub repository](https://github.com/SwitchCraftCC/sc-goodies/commits/$minecraftVersion)."
  mainFile.changelogType = "markdown"
  mainFile.addGameVersion(minecraftVersion)
  mainFile.addRequirement("fabric-api")
  mainFile.addRequirement("fabric-language-kotlin")
  mainFile.addRequirement("trinkets")
}

tasks.publish { dependsOn(publishCurseForge) }

publishing {
  publications {
    register("mavenJava", MavenPublication::class) {
      from(components["java"])
    }
  }

  repositories {
    maven {
      name = "lemmmyRepo"
      url = uri("https://repo.lem.sh/releases")

      if (!System.getenv("MAVEN_USERNAME").isNullOrEmpty()) {
        credentials {
          username = System.getenv("MAVEN_USERNAME")
          password = System.getenv("MAVEN_PASSWORD")
        }
      } else {
        credentials(PasswordCredentials::class)
      }

      authentication {
        create<BasicAuthentication>("basic")
      }
    }
  }
}
