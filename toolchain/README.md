# PSWG Toolchain

## Background

The `toolchain/` project is the project setup and build pipeline for PSWG.

Its job is to:

- define the PSWG module graph in Java
- generate IntelliJ project metadata for the modules
- prepare the Fabric development runtime bundle and generated run configurations

## Workflow

The supported day-to-day workflow is IntelliJ-first:

1. the toolchain generates the root-project IntelliJ metadata and Fabric run configuration
2. IntelliJ builds PSWG module outputs into `out/production/...`
3. the generated `Fabric Client (platform)` run configuration launches `net.fabricmc.devlaunchinjector.Main`

The primary command is:

```bash
cd toolchain
./gradlew run --args="dev setup-intellij --username Dev --uuid 00000000-0000-0000-0000-000000000000"
```

That command:

- synchronizes the PSWG root IntelliJ metadata
- refreshes the generated Fabric client launch bundle
- defaults the injected development module from the authoritative build graph
- accepts optional launch identity overrides through `--username` and `--uuid`

The current graph default is `pswg_entrypoint`, which pulls the modeled bundle modules into the
generated launch closure.

If you need a different injected module:

```bash
./gradlew run --args="dev setup-intellij --module <id>"
```

## Getting Started

From a fresh clone:

1. run `./gradlew run --args="dev setup-intellij"` from `toolchain/`
2. open the tracked repository root in IntelliJ
3. let IntelliJ reload the generated project metadata
4. run the generated `Fabric Client (...)` configuration

The first IDE launch will populate the IntelliJ-owned module outputs under `out/production/...`.

## Normal Use

Use the same setup command whenever one of these changes:

- pulled changes that touched the toolchain
- changed dependency or version properties
- changed generated IntelliJ metadata or launch-shaping behavior
- need to switch the injected root module

Normal iteration after that is just IntelliJ:

- run or debug `Fabric Client (platform)`

## Logical Overview

- Gradle:
  Builds and runs the standalone `toolchain/` project itself.
- Toolchain:
  Owns PSWG graph definition, IntelliJ metadata generation, and Fabric runtime bundle generation.
- IntelliJ:
  Owns compilation of PSWG modules for normal development runs.

The toolchain still reads a small amount of repo-owned version metadata from `gradle.properties`, but Gradle no longer owns the development runtime shape.

The toolchain writes and maintains a few key outputs in the tracked repository:

- `.idea/modules.xml`
- `.idea/compiler.xml`
- `.idea/misc.xml`
- `.idea/modules/projects/...`
- `.idea/modules/launch/fabric/...`
- `.idea/libraries/...`
- `.idea/runConfigurations/Fabric_Client_*.xml`

It also writes the generated runtime bundle under `toolchain/work/instances/...`.

## Advanced Commands

Low-level commands still exist for inspection and diagnosis:

```bash
./gradlew run --args="idea sync-pswg"
./gradlew run --args="fabric prepare-dev --module pswg_entrypoint"
./gradlew run --args="fabric inspect-dev"
./gradlew run --args="mojang manifest"
```

These are useful when debugging the toolchain itself.
