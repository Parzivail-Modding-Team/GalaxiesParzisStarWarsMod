plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven(url = "https://maven.fabricmc.net/") {
        name = "Fabric"
    }
    gradlePluginPortal()
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation("net.fabricmc:fabric-loom:0.12-SNAPSHOT")
    implementation("io.github.juuxel:loom-quiltflower:1.7.3")
}
