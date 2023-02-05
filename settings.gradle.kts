pluginManagement {
	repositories {
		mavenCentral()
		maven(url = "https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		gradlePluginPortal()
	}
}

include(":projects:pswg")
include(":projects:tarkin")
include(":projects:tarkin-api")
include(":projects:addon-test")
include(":projects:addon-clonewars")
include(":projects:toolkit")
