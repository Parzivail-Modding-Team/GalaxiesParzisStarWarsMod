# Development workflow

PSWG uses the repository's TOML-driven toolchain to generate IntelliJ modules, dependency metadata, and Fabric development run configurations. Gameplay modules are not built from a root Gradle/Loom project; `toolchain.toml` is the source of truth for their Minecraft, Loader, Fabric API, module, and datagen configuration.

## Requirements

- JDK 25 or newer
- IntelliJ IDEA with the project root opened after toolchain setup

The configured target is Minecraft `26.3`, Fabric Loader `0.19.5`, and Fabric API `0.161.0+26.3`.

## Set up the development project

From the repository root, run:

```powershell
.\toolchain\toolchain.ps1 dev setup-intellij
```

On Linux or macOS, run:

```bash
./toolchain/toolchain.sh dev setup-intellij
```

Then open or reload the repository root in IntelliJ. The toolchain prepares client, dedicated-server, and per-module datagen configurations. Use `Fabric Client (platform)` and `Fabric Server (platform)` to run or debug the mod.

To inspect the generated launch contract:

```powershell
.\toolchain\toolchain.ps1 fabric inspect-dev --environment client
.\toolchain\toolchain.ps1 fabric inspect-dev --environment server
```

## Compile and assemble artifacts

Build the configured modules in IntelliJ, or run the toolchain's CI compilation and artifact assembly from the repository root:

```powershell
.\toolchain\toolchain.ps1 artifacts assemble --module pswg_entrypoint --ci-build
```

The resulting module and bundle jars are written beneath `toolchain/work/artifacts/<version>/`.

## Generate data

Run the generated `Fabric Datagen pswg_core`, `Fabric Datagen pswg_blasters`, or `Fabric Datagen pswg_gadgets` configuration for the module whose resources should be regenerated. Each configuration sets its module ID and output directory; generated resources are written under that module's `src/main/generated` directory.

When changing generators, check both a clean run and a subsequent incremental run. Fabric's 26.3 data generator is cache-backed; verify that removed or renamed generated resources do not remain stale.

## Port to another Minecraft or Fabric version

1. Update `minecraft_version`, `loader_version`, and the Fabric API coordinates in `toolchain.toml`.
2. Update the supported Minecraft, Loader, Java, and Fabric API ranges in `projects/pswg_core/src/main/resources/fabric.mod.json` to match the versions the release is intended to support.
3. Regenerate IntelliJ metadata and launch bundles with `dev setup-intellij`.
4. Compile every configured module, run all datagen configurations, then validate client and dedicated-server launches.
5. Validate saved worlds, datapack resources, network synchronization, and the assembled bundle separately from compilation.

PSWG uses Mojmap.
