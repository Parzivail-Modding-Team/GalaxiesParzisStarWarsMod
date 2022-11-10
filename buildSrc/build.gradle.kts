repositories {
	mavenCentral()
	maven(url = "https://maven.fabricmc.net/") {
		name = "Fabric"
	}
	gradlePluginPortal()

}

dependencies {
	implementation("com.parzivail.internal:pswg-submodule-dependencies:0.1")
	implementation("net.fabricmc:fabric-loom:1.0-SNAPSHOT")
	implementation("io.github.juuxel:loom-quiltflower:1.7.3")
}
