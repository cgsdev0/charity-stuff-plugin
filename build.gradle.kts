import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml


plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
  id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0" // Generates plugin.yml based on the Gradle config
  id("com.gradleup.shadow") version "8.3.3"
}

group = "dev.cgs.mc.charity"
version = "1.0.0-SNAPSHOT"
description = "for the kids"

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()

    // If you want to shade the NBT API as well
    maven(url = "https://repo.codemc.org/repository/maven-public/")
}

dependencies {
  paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
  compileOnly("dev.jorel:commandapi-bukkit-core:10.0.1")
}

tasks {
  compileJava {
    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release = 21
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
}

// Configure plugin.yml generation
// - name, version, and description are inherited from the Gradle project.
bukkitPluginYaml {
  main = "dev.cgs.mc.charity.CharityMain"
  load = BukkitPluginYaml.PluginLoadOrder.STARTUP
  authors.add("badcop")
  apiVersion = "1.21"
  depend.add("CommandAPI")
}
