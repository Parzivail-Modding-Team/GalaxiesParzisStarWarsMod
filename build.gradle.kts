import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

plugins {
	id("fabric-loom") version "0.11-SNAPSHOT"
	id("io.github.juuxel.loom-quiltflower") version "1.7.1"
	`maven-publish`
}

repositories {
	mavenCentral()

	maven(url = "https://ladysnake.jfrog.io/artifactory/mods") {
		name = "Ladysnake Mods"
	}

	maven(url = "https://maven.terraformersmc.com/releases") {
		name = "TerraformersMC"
	}

//	maven(url = "https://raw.githubusercontent.com/TerraformersMC/Archive/main/releases") {
//		name = "TerraformersMC Archive"
//	}

	maven(url = "https://maven.shedaniel.me") {
		name = "Shedaniel Maven"
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17

	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this task, sources will not be generated.
	withSourcesJar()
}

/*
 * Gets the version name from the latest Git tag
 */

fun getVersionName(): String
{
	val stdout = ByteArrayOutputStream()
	exec {
		commandLine = listOf("git", "describe", "--tags", "--dirty")
		standardOutput = stdout
	}
	val version = stdout.toString().trim()

	val pattern = Pattern.compile("""^([0-9.]+)\+([0-9.]+)((?:-[0-9]+-g[0-9a-f]+)?(?:-dirty)?)?${'$'}""")
    val m = pattern.matcher(version)
    if (m.matches())
    {
        val pre = m.group(3)
        if (pre != null && pre.isNotEmpty())
            return "${m.group(1)}$pre+${m.group(2)}"
    }
	return version
}

loom {
	accessWidenerPath.set(file("src/main/resources/pswg.accesswidener"))
}

quiltflower {
	quiltflowerVersion.set("1.8.1")
}

val archives_base_name: String by project.ext
val maven_group: String by project.ext
val minecraft_version: String by project.ext
val yarn_mappings: String by project.ext
val loader_version: String by project.ext
val fabric_version: String by project.ext
val cca_version: String by project.ext
val cloth_config_version: String by project.ext
val modmenu_version: String by project.ext
val rei_version: String by project.ext
val libzoomer_version: String by project.ext
val trinkets_version: String by project.ext

base.archivesName.set(archives_base_name)
version = getVersionName()
group = maven_group

dependencies {
	implementation("com.google.code.findbugs:jsr305:3.0.2")
	// Used by dependencies
	implementation("com.demonwav.mcdev:annotations:1.0")

	//to change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${minecraft_version}")
	mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
	modImplementation("net.fabricmc:fabric-loader:${loader_version}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")

	// CCA Base
	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${cca_version}")
	include("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${cca_version}")
	// CCA Entity
	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${cca_version}")
	include("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${cca_version}")

	// Cloth Config
	modImplementation("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}") {
		exclude(group = "net.fabricmc.fabric-api")
	}
	include("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}")

	// Mod Menu
	modImplementation("com.terraformersmc:modmenu:${modmenu_version}")

	// Roughly Enough Items
	modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rei_version}")
	modRuntimeOnly("me.shedaniel:RoughlyEnoughItems-fabric:${rei_version}")

	// LibZoomer
	modImplementation("io.github.ennuil:LibZoomer:${libzoomer_version}")
	include("io.github.ennuil:LibZoomer:${libzoomer_version}")

	// Trinkets
	modImplementation("dev.emi:trinkets:${trinkets_version}")
	include("dev.emi:trinkets:${trinkets_version}")
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.jar {
	from(project.file("LICENSE")) {
        rename { "${it}_${archives_base_name}"}
    }

	exclude("com/parzivail/datagen")
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// select the repositories you want to publish to
	repositories {
		// uncomment to publish to the local maven
		// mavenLocal()
	}
}

open class CITask : DefaultTask()

tasks.register<CITask>("ci") {
	dependsOn(tasks.build)
}
