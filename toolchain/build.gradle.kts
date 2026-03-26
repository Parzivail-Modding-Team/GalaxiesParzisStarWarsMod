plugins {
    application
    java
}

val toolchainVersion = providers.gradleProperty("toolchainVersion").orElse("local").get()

version = toolchainVersion

repositories {
    mavenCentral()
    maven(url = "https://maven.fabricmc.net/")
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.1")
    implementation("io.github.wasabithumb:jtoml:1.5.0")
    implementation("org.dom4j:dom4j:2.2.0")
    implementation("net.fabricmc:class-tweaker:0.1.1")
    implementation("org.ow2.asm:asm:9.9")
    implementation("org.vineflower:vineflower:1.11.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25

    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "com.parzivail.toolchain.Main"
}

tasks.withType<JavaCompile> {
    options.release = 25
    options.encoding = "UTF-8"
}

val toolchainJar by tasks.registering(Jar::class) {
    group = "distribution"
    description = "Builds the standalone toolchain jar used by the wrapper scripts."
    archiveBaseName.set("toolchain")
    archiveVersion.set(toolchainVersion)
    destinationDirectory.set(layout.projectDirectory.dir("bin"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.isFile && it.extension == "jar" }
            .map { zipTree(it) }
    })

    exclude(
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/*.RSA",
        "META-INF/INDEX.LIST"
    )
}

tasks.assemble {
    dependsOn(toolchainJar)
}
