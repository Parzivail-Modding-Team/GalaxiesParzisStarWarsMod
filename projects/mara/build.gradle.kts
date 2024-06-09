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
	testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
	implementation("org.apache.commons:commons-lang3:3.12.0")
}
