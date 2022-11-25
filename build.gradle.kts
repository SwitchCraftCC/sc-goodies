import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  val kotlinVersion: String by System.getProperties()
  kotlin("jvm").version(kotlinVersion)

  id("fabric-loom") version "1.0-SNAPSHOT"
  id("io.github.juuxel.loom-quiltflower") version "1.7.3"
  id("maven-publish")
  id("signing")
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
  maven {
    url = uri("https://repo.lem.sh/releases")
    content {
      includeGroup("pw.switchcraft")
    }
  }

  maven("https://squiddev.cc/maven") {
    content {
      includeGroup("cc.tweaked")
      includeModule("org.squiddev", "Cobalt")
      includeModule("net.minecraftforge", "forgeconfigapiport-fabric")
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

  modImplementation(include("pw.switchcraft", "sc-library", scLibraryVersion))

  modCompileOnly("cc.tweaked:cc-tweaked-$ccMcVersion-fabric-api:$ccVersion") {
    exclude("net.fabricmc.fabric-api", "fabric-gametest-api-v1")
  }
  modRuntimeOnly("cc.tweaked:cc-tweaked-$ccMcVersion-fabric:$ccVersion") {
    exclude("net.fabricmc.fabric-api", "fabric-gametest-api-v1")
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
    }
  }
}

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
      credentials {
        username = System.getenv("MAVEN_USERNAME")
        password = System.getenv("MAVEN_PASSWORD")
      }
      authentication {
        create<BasicAuthentication>("basic")
      }
    }
  }
}
