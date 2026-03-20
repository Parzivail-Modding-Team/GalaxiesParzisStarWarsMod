plugins {
	application
	java
}

repositories {
	mavenCentral()
	maven(url = "https://maven.fabricmc.net/")
}

dependencies {
	implementation("com.fasterxml.jackson.core:jackson-databind:2.21.1")
	implementation("org.dom4j:dom4j:2.2.0")
	implementation("net.fabricmc:class-tweaker:0.1.1")
	implementation("org.ow2.asm:asm:9.9")
}

java {
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25

	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

application {
	mainClass = "dev.pswg.toolchain.Main"
}

tasks.withType<JavaCompile> {
	options.release = 25
	options.encoding = "UTF-8"
}
