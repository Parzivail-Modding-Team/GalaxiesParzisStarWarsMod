plugins {
	`java-library`
}

repositories {
	mavenCentral()
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21

	withSourcesJar()
}

dependencies {
	implementation("com.github.javaparser:javaparser-core-serialization:3.25.3");
}
