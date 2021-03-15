import java.io.ByteArrayOutputStream

plugins {
	id("fabric-loom") version "0.6-SNAPSHOT"
	`maven-publish`
}

repositories {
	mavenCentral()

	maven {
		name = "Ladysnake Mods"
		url = uri("https://ladysnake.jfrog.io/artifactory/mods/")

	}

	maven {
		name = "TerraformersMC"
		url = uri("https://maven.terraformersmc.com/releases/")
	}

	maven {
		name = "Shedaniel Maven"
		url = uri("https://maven.shedaniel.me/")
	}
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(8))
	}
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8

	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this task, sources will not be generated.
	withSourcesJar()
}

/**
 * Gets the version name from the latest Git tag
 */
fun getVersionName() = ByteArrayOutputStream().use {
	exec {
		workingDir = projectDir
		commandLine = listOf("git", "describe", "--tags", "--dirty")
		standardOutput = it
	}
	it.toString().trim()
}

val archives_base_name: String by ext
val maven_group: String by ext

val minecraft_version: String by ext
val yarn_mappings: String by ext
val loader_version: String by ext
val fabric_version: String by ext

val cca_version: String by ext
val cloth_config_version: String by ext
val modmenu_version: String by ext

base {
	archivesBaseName = archives_base_name
}
version = getVersionName()
group = maven_group


dependencies {
	implementation("com.google.code.findbugs:jsr305:3.0.2")

	//to change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${loader_version}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")

	// CCA Base
	modImplementation("io.github.onyxstudios.Cardinal-Components-API:cardinal-components-base:${cca_version}")
	include("io.github.onyxstudios.Cardinal-Components-API:cardinal-components-base:${cca_version}")
	// CCA Entity
	modImplementation("io.github.onyxstudios.Cardinal-Components-API:cardinal-components-entity:${cca_version}")
	include("io.github.onyxstudios.Cardinal-Components-API:cardinal-components-entity:${cca_version}")

	// Cloth Config
	modImplementation("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}") {
		exclude(group = "net.fabricmc.fabric-api")
	}
	include("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}")

	// Mod Menu
	modImplementation("com.terraformersmc:modmenu:${modmenu_version}")
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.version))
	}
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.jar {
	from("LICENSE")

	exclude("com/parzivail/datagen")
}

tasks.wrapper {
	distributionType = Wrapper.DistributionType.ALL
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			// add all the jars that should be included when publishing to maven
			artifact(tasks.remapJar) {
				builtBy(tasks.remapJar)
			}
			artifact(tasks.getByName<Jar>("sourcesJar")) {
				builtBy(tasks.remapSourcesJar)
			}
		}
	}

	// select the repositories you want to publish to
	repositories {
		// uncomment to publish to the local maven
		// mavenLocal()
	}
}
