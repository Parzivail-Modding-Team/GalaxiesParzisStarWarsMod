# PSWG Toolchain

## Purpose

The `toolchain/` project is the authoritative developer workflow for PSWG.

Its job is to:

- define the PSWG module graph in Java
- generate IntelliJ project metadata for the tracked repository
- prepare the Fabric development runtime bundle and generated run configurations
- keep the supported development path understandable after a fresh clone

The toolchain is not a generic external build system. It is a PSWG-specific development helper.

## Supported Workflow

The supported day-to-day workflow is IntelliJ-first:

1. the toolchain generates the root-project IntelliJ metadata and Fabric run configuration
2. IntelliJ builds PSWG module outputs into `out/production/...`
3. the generated `Fabric Client (...)` run configuration launches `net.fabricmc.devlaunchinjector.Main`
4. breakpoints, stop, and hotswap all happen through the normal IntelliJ debugger flow

The primary command is:

```bash
cd toolchain
./gradlew run --args="dev setup-intellij --username parzi --uuid 76554910-92a9-4507-8e5b-6340d7e77d50"
```

That command:

- synchronizes the PSWG root IntelliJ metadata
- refreshes the generated Fabric client launch bundle
- defaults the injected development module from the authoritative build graph
- accepts optional launch identity overrides through `--username` and `--uuid`

The current graph default is `pswg_core`.

If you need a different injected module:

```bash
./gradlew run --args="dev setup-intellij --module <id>"
```

## Fresh Clone Setup

From a fresh clone:

1. run `./gradlew run --args="dev setup-intellij"` from `toolchain/`
2. open the tracked repository root in IntelliJ
3. let IntelliJ reload the generated project metadata
4. run the generated `Fabric Client (...)` configuration

The first IDE launch will populate the IntelliJ-owned module outputs under `out/production/...`.

## Day-To-Day Development

Use the same setup command whenever one of these changes:

- pulled changes that touched the toolchain
- changed dependency or version properties
- changed generated IntelliJ metadata or launch-shaping behavior
- need to switch the injected root module

Normal iteration after that is just IntelliJ:

- edit code
- run or debug `Fabric Client (...)`
- rely on IntelliJ `Make` for module compilation
- rely on debugger hotswap for supported changes

## Ownership Model

The easiest way to understand the current system is to separate concerns:

- Gradle:
  Builds and runs the standalone `toolchain/` project itself.
- Toolchain:
  Owns PSWG graph definition, IntelliJ metadata generation, and Fabric runtime bundle generation.
- IntelliJ:
  Owns compilation of PSWG modules for normal development runs.

The toolchain still reads a small amount of repo-owned version metadata from `gradle.properties`, but Gradle no longer owns the development runtime shape.

## Important Outputs

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
./gradlew run --args="fabric prepare-dev --module pswg_core"
./gradlew run --args="fabric inspect-dev"
./gradlew run --args="mojang manifest"
```

These are useful when debugging the toolchain itself. They are not the primary onboarding path.

## Design Notes

The current codebase intentionally optimizes for one supported workflow instead of many partial ones:

- generated Fabric runs launch DLI directly
- IntelliJ output directories are the only supported PSWG module runtime inputs
- launch-time classpath shaping follows the prepared runtime bundle exactly
- low-level commands are retained for diagnostics, but the default mental model is `dev setup-intellij`

When changing the toolchain, prefer making that supported path simpler instead of adding another parallel path.
