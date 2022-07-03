plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven(url = "https://maven.fabricmc.net/") {
        name = "Fabric"
    }
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation("net.fabricmc:fabric-loom:0.12-SNAPSHOT")
}
