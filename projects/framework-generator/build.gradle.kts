plugins {
	`java-library`
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":projects:framework"))

	implementation("com.palantir.javapoet:javapoet:0.5.0")

	compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
	annotationProcessor("com.google.auto.service:auto-service:1.1.1")
}
