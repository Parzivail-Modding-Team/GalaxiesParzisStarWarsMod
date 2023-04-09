pluginManagement {
	repositories {
		mavenCentral()
		maven(url = "https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		gradlePluginPortal()
	}
}

// The base mod and core functionality
include(":projects:pswg")

// TARKIN (Text Asset Record-Keeping, Integration, and Normalization) data generation tools
include(":projects:tarkin")

// Annotations and other API features to decorate mods for use with TARKIN
include(":projects:tarkin-api")

// Minimum code required to create a PSWG-enabled addon
include(":projects:addon-test")

// The PSWG Team's official Clone Wars-Era addon
include(":projects:addon-clonewars")

// The Aurek Tooklkit, a suite of ingame editors to add and edit base and addon features
include(":projects:toolkit")

// Lexer, parser, and API for the Mara programming language
include(":projects:mara")
