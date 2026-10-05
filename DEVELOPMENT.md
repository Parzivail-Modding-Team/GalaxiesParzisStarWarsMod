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

## Define reusable data and codecs

Prefer the project's codec annotation processor to handwritten record codecs. `@GenerateCodec` produces an interface in `dev.pswg.generated.codecs` containing `MAP_CODEC`, `CODEC`, and, by default, `PACKET_CODEC`. A record implements its generated `I<RecordName>Codec` interface. Generated interface names must be unique within the module/dependency classpath; use descriptive record names instead of generic nested names that collide.

For strict datapack-only records, use `@GenerateCodec(packetCodec = false, strict = true)`. Strict codecs reject undeclared fields and report constructor/decode validation failures through DFU. Disabling the packet codec also avoids accidentally treating a registry-aware definition codec as a synchronization protocol.

The processor supports:

- `@SelfCodec` to reuse a type's existing codec, including elements in optional/list/map values.
- `@CodecDefault("<Java expression>")` for absent-field defaults.
- `@CodecName("<serialized name>")` when a Java component name differs from its data field, such as `defaultMode` serialized as `default`.
- `@CodecRange(min = ..., max = ...)` for inclusive numeric limits, including optional numeric fields.
- `@CodecSize(min = ..., max = ...)` for list/map counts.
- Native codecs for JOML vectors/quaternions and Minecraft `Ingredient`, plus recursive optionals, lists, and maps.
- `@UseCodec` for a pre-existing codec or a genuinely specialized field adapter. A custom source describes the complete component type; `@SelfCodec` retains precedence when both annotations are present.

Use native DFU/Minecraft validation and collection utilities first. Cross-field rules may wrap a generated codec with validation; sibling-dependent defaults may normalize a generated wire record. Keep reusable missing helpers in `pswg_core`'s `GalaxiesCodecs`, not individual content modules.

Generic numeric pose data lives in `pswg_core` under `dev.pswg.model.animation`: `EulerTransform` uses JOML vectors and converts to the existing quaternion-based `G3dTransform` without changing block units; `TransformAnimation` uses Minecraft keyframes, easing, and its native sampler. The `timeTicks` wire adapter preserves the authored format while using native `Keyframe` values. Core codecs require finite values; content modules impose their own target vocabulary and limits. Blaster archetype selection, arm masks, deployment, and gameplay eligibility remain in `pswg_blasters`.

## Verify gameplay changes

Integration acceptance may be user-run gameplay through the normal client and server configurations. No separate testing project or new runner is required. Still compile all affected modules and use focused codec/data/generated-source checks where appropriate. Such checks do not replace singleplayer, dedicated-server, reload, persistence, or visual gameplay acceptance. Temporary verification helpers belong outside the tracked repository.

### Blaster definition loading and synchronization

The blasters module registers four native Fabric reloadable registries. Their entries use Fabric's namespaced registry directories:

| Registry | Entry path for `example:probe` |
|---|---|
| `pswg_blasters:blasters` | `data/example/pswg_blasters/blasters/probe.json` |
| `pswg_blasters:attachments` | `data/example/pswg_blasters/attachments/probe.json` |
| `pswg_blasters:behavior_profiles` | `data/example/pswg_blasters/behavior_profiles/probe.json` |
| `pswg_blasters:stance_profiles` | `data/example/pswg_blasters/stance_profiles/probe.json` |

The built-in `pswg_blasters:test_blaster` is under `projects/pswg_blasters/src/main/resources/data/pswg_blasters/pswg_blasters/blasters/test_blaster.json`. Its six attachment options preserve their literal values; cooling is an explicit reference to the shared attachment entry. Weapon JSON retains `stats` and `attachments` with camelCase fields, and now requires explicit ammo, configuration, and modes.

`BlasterData` validates the complete candidate, caches resolved options, prepares a standalone projection, and writes both together to the candidate `DataResourceStore`. Consumers call `BlasterData.get(level)` or `BlasterItem.getDefinition(level, stack)` to read the installed generation. Failed candidate loading/validation leaves the installed generation untouched; initial invalid definitions fail world loading.

Minecraft's pending tag lookup exposes tag keys before their contents are bound. The preparation pass reuses native `TagLoader` to resolve candidate item-tag contents and the core `IngredientSnapshots` adapter freezes ammo membership into direct items without applying live tags. Native item/list/tag ingredients are supported; custom predicate ingredients are rejected rather than losing their extra matching semantics. `StoredCharge` provides the common bounded `{current, capacity}` representation required by registered component-charge sources; official pack components and gameplay consumption are subsequent work.

Clients receive an ID/value projection on join and committed reload, with a channel-registration fallback for late receiver advertisement. The projection has a hard 8 MiB UTF-8 limit and bounded maps/lists. Native large-payload splitting handles transport; no custom registry raw IDs or holder references are encoded. The client validates the complete projection, replaces one connection-owned snapshot, and invalidates native creative contents. Disconnect clears that connection's data, including integrated singleplayer. Removed weapon/attachment IDs stay on the physical stack but resolve as unavailable/inactive until restored.

Register addon trigger, delivery/effect codecs, and stat-function descriptors during `onGalaxiesReady`, before module finalization freezes those tables. Ordinary data-only catalog entries need no Java registration. Damage-type definitions are native world-registry entries; adding a new damage type requires reopening the world rather than assuming `/reload` loads it. The built-in direct-blaster type and its `minecraft:bypasses_cooldown` contribution are supplied before damaging firing behavior is implemented.

### Phase 1 gameplay checklist

Build the current code and use the normal client/server configurations. This acceptance is tracked in `pswg-37u.12`.

1. **Initial load and join:** open a world, select **Test Blaster** from the Combat tab, and confirm the client log reports `Received blaster definitions <generation> (1 weapons)`. Check the existing heat/cooling HUD and default attachment appearance. Repeat on a dedicated server with two clients; both should receive the same generation.
2. **Data-only addon:** create a normal world datapack with `data/example/pswg_blasters/blasters/probe.json`, copied from the built-in complete definition. Reuse the built-in stance and behavior references. After `/reload`, the creative tab should include the new ID, including when that screen was already open. Add a resource-pack translation if a friendly name is desired.
3. **Committed update:** override the built-in weapon with a complete JSON entry and change a visible HUD value such as `stats.heat.capacity`. Reload; both clients should receive the new generation and query the new value, while their held stack IDs, counts, and applied attachment selections remain intact. Join a third client after the reload and compare its generation.
4. **Rejected candidate:** change a required behavior/stance/shared reference to a missing ID, omit required ammo, or use an out-of-bounds value. `/reload` should fail with a resource-keyed error; clients should retain their previous generation and working definitions. Restore the valid entry and reload successfully. Also check that an invalid definition fails initial loading of a development world.
5. **Candidate ammo tags:** set the addon ingredient to `"#example:ammo"` and provide `data/example/tags/item/ammo.json` with real item IDs. Introducing the tag must load successfully; changing its members must produce the new snapshot without prematurely changing the old generation. Ammo consumption itself belongs to the later firing phase.
6. **Removed/restored IDs:** keep an addon weapon in the inventory, remove its entry, and reload. It should disappear from creative contents and cease weapon-specific actions without deleting or replacing the stack. Restore the entry and confirm the same stack resolves again. Repeat for an equipped attachment option, including a change that makes its slot incompatible: its condition/model and prototype recoil contribution must both become inactive.
7. **Connection isolation:** disconnect, join a world/server with different definitions, and verify no previous catalog appears. Repeat between integrated and dedicated play, then reconnect to the first server. Repeat join/reload while a creative screen is open.

Phase 1 supplies data and synchronization. The shared effective-stat evaluator, ammo transactions, new trigger/damage delivery, stance rendering, and workbench remain in their respective implementation phases. The existing prototype firing/HUD route is retained while those systems are built.

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
