pluginManagement {
	repositories {
		mavenCentral()
		maven(url = "https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		gradlePluginPortal()
	}
}

// The core mod
include(":projects:pswg_core")

// The blasters module
include(":projects:pswg_blasters")

// The dummy entrypoint module that depends on all other modules
include(":projects:pswg_entrypoint")

// The Aurek Tooklkit, a suite of ingame editors to add and edit base and addon features
// include(":projects:toolkit")

// Lexer, parser, and API for the Mara programming language
// include(":projects:mara")
