# PSWG Toolchain

## Background

The `toolchain/` project is the project setup and build pipeline for PSWG.

Its job is to:

- load the PSWG project and module graph from `../toolchain.toml`
- generate IntelliJ project metadata for the modules
- prepare the Fabric development runtime bundles and generated run configurations

`toolchain.toml` is the authoritative project configuration boundary. It captures the
project id/name, tracked Minecraft version, default development module, and the per-module roots,
dependencies, aggregate members, artifact ids, Fabric mod ids, mixins, and datagen outputs that
the reusable toolchain engine consumes.

For the full schema and worked examples, see `toolchain/PROJECT_SPEC.md`.

The preferred TOML shape is intentionally compact:

- use `kind = "fabric_split_sources"` for normal Fabric modules with shared `src/main` and `src/client`
- use `dependency = "..."` and `annotation_processor = "..."` when only one entry is needed
- use `main_mixins = ["..."]` and `client_mixins = ["..."]` with resource-relative file names
- use `generated_sources = true`, `generated_client_sources = true`, or `datagen = true` when the default roots are
  correct
- use external dependency shorthand like `"group:artifact:version @ maven_central"` when table syntax is unnecessary

## Workflow

The supported day-to-day workflow is wrapper-first:

1. the toolchain generates the root-project IntelliJ metadata and Fabric run configurations
2. IntelliJ builds PSWG module outputs into `out/production/...`
3. the generated `Fabric Client (platform)`, `Fabric Server (platform)`, or `Fabric Datagen <module> (platform)` run
   configuration launches `net.fabricmc.devlaunchinjector.Main`
4. `toolchain.sh` or `toolchain.bat` runs the packaged toolchain jar from `toolchain/bin/`

The primary command is:

```bash
cd toolchain
./toolchain.sh dev setup-intellij --username Dev --uuid 00000000-0000-0000-0000-000000000000
```

That command:

- synchronizes the PSWG root IntelliJ metadata
- refreshes the generated Fabric client, server, and datagen development launch bundles
- resolves IntelliJ source attachments for Minecraft and published library jars when sources are available
- defaults the injected development module from the authoritative build graph
- accepts optional launch identity overrides through `--username` and `--uuid`

The graph default is `pswg_entrypoint`, which pulls the modeled bundle modules into the
generated launch closure.

That aggregation is owned by the authoritative toolchain graph itself. `pswg_entrypoint`
declares aggregate members for development-time launch and datagen workflows.

If you need a different injected module:

```bash
./toolchain.sh dev setup-intellij --module <id>
```

## Getting Started

From a fresh clone:

1. run `./toolchain.sh dev setup-intellij` from `toolchain/`
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
- run the generated `Fabric Datagen <module> (platform)` configuration for the module whose checked-in
  `src/main/generated` output you want to refresh
- inspect Minecraft, Fabric, and other attached libraries directly from the IDE when their source jars are available

## Artifact Assembly

The toolchain can assemble local release-style jars from IntelliJ-owned outputs:

```bash
./toolchain.sh artifacts assemble --module pswg_entrypoint
```

For CI or other environments without IntelliJ-managed compilation, use the toolchain-owned compile
path first:

```bash
./toolchain.sh artifacts assemble --module pswg_entrypoint --ci-build
```

That command:

- reads compiled classes and resources from `out/production/...`
- expands `${version}` inside packaged `fabric.mod.json`
- writes standalone module jars and aggregate bundle jars under `toolchain/work/artifacts/<version>/`

With `--ci-build`, the toolchain first compiles the required module closure into
`toolchain/work/ci-build/out/production/...` using the JDK compiler and the same authoritative
module graph used by IntelliJ metadata generation.

The artifact workflow is local assembly only. It does not publish to Maven Central,
Modrinth, or CurseForge, and it expects the relevant modules to have already been compiled by
IntelliJ before packaging unless `--ci-build` is used.

To build the standalone toolchain jar that the wrapper launches:

```bash
./gradlew toolchainJar
```

That writes `toolchain/bin/toolchain-<version>.jar`, which `toolchain.sh` and `toolchain.bat`
pick up automatically.

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
  update the owning module declaration in `toolchain.toml` or the tracked Gradle properties,
  then rerun `dev setup-intellij` so IntelliJ project libraries and launch inputs stay aligned.

After any version change:

1. run `./toolchain.sh dev setup-intellij`
2. run `./toolchain.sh fabric inspect-dev --environment <client|server>` if the launch contract might have changed
3. verify the generated `Fabric Client (platform)` or `Fabric Server (platform)` configuration still launches and debugs
   cleanly

If the update involves dev-launch-injector behavior, Fabric bootstrap changes, or launch-property
changes, see `toolchain/DEVELOPMENT.md` before changing the launch workflow code.
