plugins {
	`java-library`
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21

	withSourcesJar()
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.github.javaparser:javaparser-core-serialization:3.25.3");
}
