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

After changing annotation processors or their declarations in `toolchain.toml` (including codec/enum generation), rerun `dev setup-intellij` and reload the project so generated module metadata and processor wiring are refreshed before compiling.

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

## Define reusable data and codecs

Prefer the project's codec annotation processor to handwritten record codecs. `@GenerateCodec` produces an interface in `dev.pswg.generated.codecs` containing `MAP_CODEC`, `CODEC`, and, by default, `PACKET_CODEC`. A record implements its generated `I<RecordName>Codec` interface. Generated interface names must be unique within the module/dependency classpath; use descriptive record names instead of generic nested names that collide. `@GenerateEnumCodec` produces the enum's string `CODEC` and native `PACKET_CODEC`; enum constants serialize as lowercase names (underscores retained) unless `@CodecName` overrides them. Generated enums generally need no fields or custom constructors.

For strict datapack-only records, use `@GenerateCodec(packetCodec = false, strict = true)`. Strict codecs reject undeclared fields and report constructor/decode validation failures through DFU. Disabling the packet codec also avoids accidentally treating a registry-aware definition codec as a synchronization protocol.

The processor supports:

- `@SelfCodec` to reuse a type's existing codec, including elements in optional/list/map values.
- `@CodecDefault("<Java expression>")` for absent-field defaults.
- `@CodecName("<serialized name>")` when a Java component name differs from its data field, such as `defaultMode` serialized as `default`.
- `@CodecRange(min = ..., max = ...)` for inclusive numeric limits, including optional numeric fields.
- `@CodecSize(min = ..., max = ...)` for list/map counts.
- `@CodecUnique` and `@CodecUnique(key = "id")` for unique list values or unique record keys.
- Native codecs for JOML vectors/quaternions and Minecraft `Ingredient`, plus recursive optionals, lists, and maps.
- `@UseCodec` for a pre-existing codec or a genuinely specialized field adapter. A custom source describes the complete component type; `@SelfCodec` retains precedence when both annotations are present.

Use native DFU/Minecraft validation and collection utilities first. Let field codecs and generated annotations enforce local nullability, ranges, sizes, and uniqueness; do not add explicit constructors or re-encode decoded values merely to validate local fields. Cross-field rules may wrap a generated codec with validation; sibling-dependent defaults may normalize a generated wire record. Keep reusable missing helpers in `pswg_core`'s `GalaxiesCodecs`, not individual content modules.

## Verify gameplay changes

Integration acceptance may be user-run gameplay through the normal client and server configurations. No separate testing project or new runner is required. Still compile all affected modules and use focused codec/data/generated-source checks where appropriate. Such checks do not replace singleplayer, dedicated-server, reload, persistence, or visual gameplay acceptance. Temporary verification helpers belong outside the tracked repository.

## Generate data

Run the generated `Fabric Datagen pswg_core`, `Fabric Datagen pswg_blasters`, or `Fabric Datagen pswg_gadgets` configuration for the module whose resources should be regenerated. Each configuration sets its module ID and output directory; generated resources are written under that module's `src/main/generated` directory.

When changing generators, check both a clean run and a subsequent incremental run. Fabric's 26.3 data generator is cache-backed; verify that removed or renamed generated resources do not remain stale.

## Port to another Minecraft or Fabric version

1. Update `minecraft_version`, `loader_version`, and the Fabric API coordinates in `toolchain.toml`.
2. Update the supported Minecraft, Loader, Java, and Fabric API ranges in `projects/pswg_core/src/main/resources/fabric.mod.json` to match the versions the release is intended to support.
3. Regenerate IntelliJ metadata and launch bundles with `dev setup-intellij`; this also refreshes annotation-processor wiring from `toolchain.toml`.
4. Compile every configured module, run all datagen configurations, then validate client and dedicated-server launches.
5. Validate saved worlds, datapack resources, network synchronization, and the assembled bundle separately from compilation.

PSWG uses Mojmap.
