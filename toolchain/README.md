# PSWG Toolchain

## Background

The `toolchain/` project is the project setup and build pipeline for PSWG.

Its job is to:

- define the PSWG module graph in Java
- generate IntelliJ project metadata for the modules
- prepare the Fabric development runtime bundles and generated run configurations

## Workflow

The supported day-to-day workflow is IntelliJ-first:

1. the toolchain generates the root-project IntelliJ metadata and Fabric run configurations
2. IntelliJ builds PSWG module outputs into `out/production/...`
3. the generated `Fabric Client (platform)`, `Fabric Server (platform)`, or `Fabric Datagen <module> (platform)` run configuration launches `net.fabricmc.devlaunchinjector.Main`

The primary command is:

```bash
cd toolchain
./gradlew run --args="dev setup-intellij --username Dev --uuid 00000000-0000-0000-0000-000000000000"
```

That command:

- synchronizes the PSWG root IntelliJ metadata
- refreshes the generated Fabric client, server, and datagen development launch bundles
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
4. run the generated `Fabric Client (...)`, `Fabric Server (...)`, or module-scoped `Fabric Datagen ...` configuration

The first IDE launch will populate the IntelliJ-owned module outputs under `out/production/...`.

## Normal Use

Use the same setup command whenever one of these changes:

- pulled changes that touched the toolchain
- changed dependency or version properties
- changed generated IntelliJ metadata or launch-shaping behavior
- need to switch the injected root module

Normal iteration after that is just IntelliJ:

- run or debug `Fabric Client (platform)` or `Fabric Server (platform)`
- run the generated `Fabric Datagen <module> (platform)` configuration for the module whose checked-in `src/main/generated` output you want to refresh

Datagen is always client-derived in the bespoke toolchain. Each generated datagen configuration is
scoped to one module by:

- `fabric-api.datagen.modid=<that module's Fabric mod id>`
- `fabric-api.datagen.output-dir=<that module's checked-in src/main/generated>`

That keeps the runtime classpath broad enough for downstream generators to build on upstream
modules, while still preventing the common "ran the wrong datagen config and wrote into the wrong
module" failure mode.

## Version Upgrades

Most routine version bumps start in the tracked repository's `gradle.properties`.

- Minecraft:
  update `minecraft_version`, then rerun `dev setup-intellij` so the toolchain regenerates the
  transformed compile jars, IntelliJ metadata, and Fabric launch bundle for the new version.
- Fabric Loader or Fabric API:
  update `loader_version` and/or `fabric_version`, then rerun `dev setup-intellij` and verify one
  real IntelliJ debug launch.
- Other Maven dependencies:
  update the owning module declaration in the toolchain graph or the tracked Gradle properties,
  then rerun `dev setup-intellij` so IntelliJ project libraries and launch inputs stay aligned.

After any version change:

1. run `./gradlew run --args="dev setup-intellij"`
2. run `./gradlew run --args="fabric inspect-dev --environment <client|server>"` if the launch contract might have changed
3. verify the generated `Fabric Client (platform)` or `Fabric Server (platform)` configuration still launches and debugs cleanly

If the update involves dev-launch-injector behavior, Fabric bootstrap changes, or launch-property
changes, see `toolchain/DEVELOPMENT.md` before changing the launch workflow code.
