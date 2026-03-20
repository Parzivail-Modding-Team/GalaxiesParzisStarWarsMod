plugins {
	application
	java
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.fasterxml.jackson.core:jackson-databind:2.21.1")
	implementation("org.dom4j:dom4j:2.2.0")
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
