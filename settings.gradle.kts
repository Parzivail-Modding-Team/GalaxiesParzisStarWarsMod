pluginManagement {
	val loom_version: String by settings

	plugins {
		id("net.fabricmc.fabric-loom") version loom_version
	}

	repositories {
		mavenCentral()
		maven(url = "https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		gradlePluginPortal()
	}
}

// Compile-only annotations
include(":projects:framework")

// Annotation processors for the compile-only annotations
include(":projects:framework-generator")

// The core mod
include(":projects:pswg_core")

// The blasters module
include(":projects:pswg_blasters")

// The gadgets module
include(":projects:pswg_gadgets")

// The dummy entrypoint module that depends on all other modules
include(":projects:pswg_entrypoint")

// The Aurek Tooklkit, a suite of ingame editors to add and edit base and addon features
// include(":projects:toolkit")

// Lexer, parser, and API for the Mara programming language
// include(":projects:mara")
