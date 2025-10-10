# Development Workflow 

## Setting up a development environment
PSWG builds using the Gradle build system. I recommend you use IntelliJ as an IDE. Simply import the gradle project, setup your development workspace, and build.

* Set up your development workspace by importing the Gradle project
* Decompile Minecraft sources: `gradlew genSources`
* All dependencies are met with Maven and are subsequently compiled into the resulting jarfile.
* Enable annotation processing in IntelliJ under `File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`
* Build a jarfile with `gradlew build` to produce `pswg-<version>.jar` in `./build/libs`.

Recommended:

* [MinecraftDev IntelliJ IDEA Plugin](https://plugins.jetbrains.com/plugin/8327)

## Migrating to new Minecraft versions

1. Visit [Fabric Develop](https://fabricmc.net/develop/) and update the following lines in `gradle.properties`:

```properties
minecraft_version=<version>
yarn_mappings=<version>
loader_version=<version>
# Fabric API
fabric_version=<version>
```

2. Update the Loom version in `build.gradle.kts`:

```kotlin
plugins {
    ...
    id("fabric-loom") version "<version>"
    ...
}
```

3. If required, update the Gradle wrapper in `gradle/wrapper/gradle-wrapper.properties`

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-<version>-bin.zip
```

4. Reimport the Gradle project