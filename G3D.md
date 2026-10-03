# G3D V1 — Galaxies 3D

G3D supplies indexed geometry, rigid named groups, materials, and attachment sockets to blocks, items, and entities. `.jg3d` is the authoring JSON format; `.g3d` is its compiled binary format. The common model code has no client or rendering dependencies.

## Resource layout

For model `example:item/tool`, use these paths within the owning module:

| Resource | Location |
| --- | --- |
| Authoring source | `src/main/resources/assets/example/g3d/source/item/tool.jg3d` |
| Generated vanilla sidecar | `src/main/generated/assets/example/models/item/tool.json` |
| Ptex definition | `src/main/resources/assets/example/ptex/tool_surface.json` |
| Generated visual model | `src/main/generated/assets/example/models/item/tool.g3d` |
| Generated gameplay rig | `src/main/generated/data/example/g3d/rigs/item/tool.g3d` |

The owning module's normal Fabric datagen run compiles the source. Official modules exclude `.jg3d` files and old `assets/**/datagen/**` references from release jars. Third-party modules should use those artifact exclusions and add `G3dModelProvider` after loading `G3dModelProvider.SOURCES` with `DataGenResourceHelper`.

Vanilla sidecars are ordinary model JSON. New Blockbench exports include their metadata in the source's `model` object, and `G3dModelProvider` generates the sidecar next to the compiled visual model. Sidecars keep display transforms, parent inheritance, GUI lighting, ambient occlusion, particle fallback, and texture slots needed by vanilla. Sources without `model` can still use an authored sidecar in `src/main/resources/assets/<namespace>/models/`. Geometry and Ptex surfaces come from the compiled model with the same identifier. Entity-only models do not require a sidecar.

## Block particle textures

Block breaking and mining-hit debris use the vanilla sidecar's `textures.particle` entry, independently of the G3D mesh materials. For example, `assets/example/models/block/tool.json` can select a dedicated debris image:

```json
{
  "textures": {
    "particle": "example:block/tool_debris"
  }
}
```

This sprite identifier resolves to `assets/example/textures/block/tool_debris.png`. Use the sprite name without the `textures/` prefix or `.png` suffix. Particle sprites must be available in the block atlas; images under `textures/block/` are included by vanilla's atlas directory source.

Vanilla texture-slot references and parent inheritance work too. A separate slot can make the choice explicit:

```json
{
  "textures": {
    "debris": "minecraft:block/iron_block",
    "particle": "#debris"
  }
}
```

Changing the particle slot does not change mesh textures, geometry, or the compiled `.g3d` file. For generated sidecars, set it in `model.textures.particle` in the `.jg3d` source; Blockbench retains imported particle choices and supports its normal particle-texture selection. An atlas-capable Ptex output can also be selected using its generated sprite identifier, such as `pswg:ptex/model/block/model/tall_lamp`; a sampled-only texture cannot supply vanilla block debris. The active G3D blocks use their dedicated `*_particle` images. `pswg:block/empty` is transparent and intentionally hides these particles.

Run the owning module's datagen after changing source `model` metadata, then rebuild and reload resources. For separately authored sidecars, rebuild and reload directly. Vanilla remains responsible for spawning and rendering block debris.

## Source document

This complete example describes one triangle and a socket on its root group:

```json
{
  "version": 1,
  "materials": [
    {
      "id": "surface",
      "texture": "example:tool_surface",
      "layers": {
        "block": "minecraft:block/cutout",
        "item": "minecraft:item/cutout",
        "entity": "minecraft:entity/cutout"
      },
      "tintIndex": -1,
      "lightEmission": 0,
      "doubleSided": false
    }
  ],
  "nodes": [
    {
      "id": "root",
      "restTransform": {
        "translation": [0, 0, 0],
        "rotation": [0, 0, 0, 1],
        "scale": [1, 1, 1]
      },
      "meshes": ["triangle"]
    }
  ],
  "meshes": [
    {
      "id": "triangle",
      "material": "surface",
      "vertices": [
        {"position": [0, 0, 0], "normal": [0, 0, 1], "uv": [0, 0]},
        {"position": [16, 0, 0], "normal": [0, 0, 1], "uv": [1, 0]},
        {"position": [0, 16, 0], "normal": [0, 0, 1], "uv": [0, 1]}
      ],
      "indices": [0, 1, 2]
    }
  ],
  "sockets": [
    {
      "id": "tip",
      "node": "root",
      "localTransform": {"translation": [0, 16, 0]}
    }
  ]
}
```

- `version`, `materials`, `nodes`, and `meshes` are required. `sockets` defaults to an empty list.
- Model-local names are case-sensitive strings. External resources and layer choices use Minecraft's `Identifier` codec.
- A node's `parent` names another node. Omit it for a root. Multiple roots and unordered source nodes are allowed.
- Missing transforms mean identity. Translation, rotation, and scale fields may also be omitted individually. Minecraft's quaternion codec also accepts its axis-angle form.
- Material layers default to native cutout. `tintIndex` defaults to `-1`, `lightEmission` to `0`, and `doubleSided` to `false`.
- Positions and source translations use **16 units per block**. Compiled geometry, compiled transforms, and runtime pose inputs use **blocks**. Normals and scales are dimensionless.
- Axes are right-handed: `+X` east, `+Y` up, `+Z` south. Front faces are counter-clockwise. UV `[0, 0]` is the image's top-left corner; runtime does not flip it.
- The exporter triangulates polygons and bakes pivots/shear into geometry. The compiler normalizes rotations and orders parents before children.
- The geometry codec ignores extra JSON fields. Datagen also reads the optional `model` metadata described below. Degenerate faces, zero scales, unusual UVs, and non-unit normals are allowed.

## Generated model sidecars

An optional `model` object stores vanilla presentation metadata in the same `.jg3d` document as the geometry. For example:

```json
{
  "model": {
    "display": {
      "gui": {
        "rotation": [30, 225, 0],
        "translation": [0, 0, 0],
        "scale": [0.625, 0.625, 0.625]
      },
      "firstperson_righthand": {
        "rotation": [0, 45, 0],
        "translation": [0, 0, 0],
        "scale": [0.4, 0.4, 0.4]
      }
    },
    "gui_light": "side",
    "ambientocclusion": true,
    "textures": {
      "particle": "example:block/tool_debris"
    }
  }
}
```

`display` uses native Java model view names, degree rotations, translations in sixteenths of a block, and dimensionless scales. Mirroring uses negative scale values. The plugin exports all nine Java views with explicit identity values for reset or unused views, including `on_shelf`; native left-hand fallback is applied when opening an older model that omits its left-hand views. The GUI light, ambient occlusion, parent, and texture slots also remain native model fields. Source groups and attachment transforms are independent of these display transforms.

Datagen validates the metadata with Minecraft's model parser, rejects malformed or non-finite display vectors, and writes a cache-backed `models/<path>.json` alongside `models/<path>.g3d`. A supplied `elements` list is omitted because geometry comes from G3D. Authored particles and parent inheritance are retained. A standalone model without a particle uses its first atlas-capable surface: an explicit Ptex definition takes precedence, and a direct image uses its vanilla sprite name. With no atlas-capable surface, the native missing sprite is used.

The metadata is read separately from the common geometry codec. It does not enter the compiled container or the shared source hash. A display-only edit updates the generated JSON without changing visual geometry or server rig bytes. The toolchain copies generated resources after authored resources, so a newly generated sidecar takes precedence during artifact assembly. Item definitions and blockstates remain the responsibility of their normal providers.

## Blockbench authoring

The [PSWG G3D plugin](resources/blockbench_plugins/README.md) provides a native Blockbench workspace for `.jg3d` sources. Artists assign textures to faces and edit **Surface**, **Glow strength**, and **Show both sides** in each texture's normal properties dialog. New textures default to showing both sides; imported material values retain their original sidedness. The exporter creates the material table and splits multi-texture meshes automatically. It uses normal group, mesh, and locator names for the exported model parts and sockets. The native **Display** workspace edits placement for hands, GUI, frames, shelves, and other Java views; those values are exported in `model` for automatic sidecar generation.

Each Blockbench `Texture` instance owns its material settings and stable material ID. Two texture entries may share an image resource but have different emission, transparency, tint, or sidedness. Imported source materials each become a separate texture entry, including unresolved Ptex surfaces. Native duplication and `.bbmodel` save/undo retain those settings. Changing a texture's display name does not change its game resource path.

Surface presets select the corresponding native solid, cutout, or translucent layers for all targets. Glow presets start at `lightEmission: 15`; transparent glow also selects `minecraft:entity/translucent_emissive`. Imported target-specific or custom layers remain intact until the author chooses a preset. Advanced texture controls retain Ptex selection, game tint slots, and custom layer paths.

The editor previews alpha, sidedness, and emission through an extension of Blockbench's native texture shader. Shader edits support the minified text used by release builds. Emission supplies a preview light floor; at level 15 it uses the image colors without diffuse shading or scene light. It does not reuse Blockbench's alpha-based emissive mask. Game tint providers and complex Ptex graphs are not evaluated in the editor. The runtime still controls world lighting and the final target-specific rendering.

## Ptex surfaces

A material's `texture` can identify a Ptex document or point directly at an existing Minecraft image resource. Use a Ptex document when the surface needs a tint/composite graph or an explicit atlas/sampler choice. For example, `example:tool_surface` resolves to `assets/example/ptex/tool_surface.json`:

```json
{
  "atlas": true,
  "graph": {
    "type": "pswg:source",
    "texture": "example:textures/item/tool.png"
  }
}
```

For an ordinary texture, use its resource identifier including the `textures/` prefix and `.png` suffix, such as `example:textures/block/tool.png`. The runtime treats a missing Ptex definition with this path shape as a direct `SourceTexture`: vanilla atlas sprites use `example:block/tool`, while sampled rendering uses the image resource itself. An explicit Ptex definition with the same identifier takes precedence. This lets Blockbench's normal texture assignment work without authoring one wrapper JSON for every image.

| Built-in service | Fields |
| --- | --- |
| `pswg:source` | `texture`: image identifier, including `textures/` and `.png` |
| `pswg:tint` | `color`: an ARGB integer or vanilla color-codec value; `input`: another graph |
| `pswg:composite` | `layers`: graphs ordered from bottom to top |

`atlas` defaults to `true`. Fabric's `SpriteSourceRegistry` generates atlas-capable Ptex graphs during reload, before model baking. Their stable sprite name prepends `ptex/` to the definition's path. The official source adds them to the block atlas, which vanilla supports for both blocks and items. Direct-image graphs retain vanilla animation metadata; tint/composite graphs produce a static image per reload. Direct texture identifiers use the vanilla sprite directly and need no generated Ptex sprite.

The atlas declaration is `projects/pswg_core/src/main/resources/assets/minecraft/atlases/blocks.json`. It belongs to the main mod resource pack, alongside `fabric.mod.json` and the Ptex definitions. A client classpath directory is not automatically a Fabric resource-pack root, so atlas declarations in `src/client/resources` can be invisible in development launches. The sprite-source implementation and its registration stay in client Java sources.

Every graph also has sampled output. `atlas: false` selects sampled-only surfaces; items then use the native special-model path. Chunk-rendered blocks require atlas output. Sampled or changing block textures need a block-entity/special renderer using `G3dRenderer`.

On the render thread, sampled-only requests return a stable texture ID immediately. Generated graphs begin with a visible checkerboard placeholder. Shared background work replaces the image at that ID, and reload cancels stale work. Failures are logged and keep the placeholder. Addons can register graph codecs with `PtexCodecs.register` during client startup.

G3D entity and special-item renderers capture atlas-capable surfaces from the current `ModelBaker`. They bind the sprite's atlas and map model UVs into its region. Vanilla then updates the current animation frame, including `.mcmeta` frame timing and interpolation. An animated PNG strip must not be bound as a whole sampled image: its model UVs describe one frame. Primed grenade entities share the same animated atlas sprites as their item forms. Sampled-only graphs keep their sampler texture and normalized UVs. Custom callers should obtain a renderer through `G3dClientModels` or use `new G3dRenderer(geometry, baker)` for atlas animation; the geometry-only constructor supplies the sampler-only path when no baker is available.

## Native layers and rendering

| Target | Identifiers |
| --- | --- |
| Block | `minecraft:block/solid`, `minecraft:block/cutout`, `minecraft:block/translucent` |
| Item | `minecraft:item/solid`, `minecraft:item/cutout`, `minecraft:item/translucent` |
| Entity | `minecraft:entity/solid`, `minecraft:entity/cutout`, `minecraft:entity/cutout_no_cull`, `minecraft:entity/translucent`, `minecraft:entity/translucent_emissive` |

Block and item layers are independent. Static baking produces vanilla `BakedQuad.MaterialInfo`; special items use native item render types, including standard foil, with either captured atlas sprites or sampled-only textures. Unknown layer identifiers are logged once and use cutout. `doubleSided` selects native no-cull layers or adds reversed faces for culling layers. `lightEmission` is a level from 0 to 15.

Static geometry uses vanilla face-direction shading. Special/entity geometry retains per-vertex normals. Native quads repeat the final vertex of each triangle. Particles, custom pipelines, skinning, and authored animation clips are outside V1.

Blockstates and item definitions stay vanilla. An ordinary `minecraft:model` leaf can reference G3D inside `condition`, `select`, `range_dispatch`, or `composite` trees:

```json
{"model": {"type": "minecraft:model", "model": "example:item/tool"}}
```

`G3dClientModels` uses Fabric's preparable model-loading hook to replace only the sidecar's geometry. Vanilla then bakes static atlas models, preserving transforms, particles, tints, foil, item properties, and blockstate rotations/UV lock. Only sampled or posed leaves need `CuboidItemModelWrapperMixin`; it intercepts the recursive leaf bake and keeps the surrounding vanilla tree.

Modules can opt into posed items during `onGalaxiesClientReady`:

```java
G3dClientModels.registerItemPose(modelId, (stack, context, level, owner, seed) -> poseInputs);
```

`poseInputs` maps node names to `G3dTransform` values in blocks. Inputs **replace** local rest transforms. Missing names use rest; unknown names are ignored. The adapter copies evaluated matrices and calculated tints during extraction so queued rendering does not read live gameplay objects.

## Gameplay sockets and entities

Evaluate the server rig from the current datapacks using gameplay-visible inputs:

```java
var rig = G3dResources.SERVER.get(modelId).orElseThrow();
var pose = new G3dPose(rig);
pose.evaluate(poseInputs);
pose.socketMatrix("tip", modelToWorld, destinationMatrix);
```

`G3dPose` reuses JOML matrices and evaluates parent-first without recursion. Reuse it while its rig is current. `nodeMatrix` is read-only until the next evaluation; `snapshot` copies matrices for render-state extraction. Caller-owned destination matrices let socket queries avoid allocating results. Treat JOML values in loaded models and transform records as read-only.

`G3dClientModels.get(modelId)` reads the current model-manager snapshot. `G3dEntityRenderer` is a module-facing entity/projectile base: override `extractPose` for named inputs and `extractTransform` for orientation/scale. It copies state and submits through `SubmitNodeCollector` and `VertexConsumer`. Block entities or other custom consumers can call `G3dRenderer.submit` with captured matrices.

Entity-only models can opt into standalone/dynamic textures during client startup with `G3dClientModels.registerSampled(modelId)`. Their reload-bound renderer skips atlas lookup and samples each direct image or Ptex runtime texture with normalized UVs. Shared animated item/entity models retain their atlas-aware path unless explicitly opted in. Sampled PNGs are whole images; vanilla atlas animation metadata does not animate this path.

## Humanoid armor

`G3dArmorRenderer` implements Fabric's worn-armor API. Register a combined set during the module's client initialization:

```java
G3dArmorRenderer.register(
		Galaxies.id("armor/stormtrooper"),
		GalaxiesItems.STORM_TROOPER,
		G3dArmorRenderer.Flag.HIDE_SKIN_OVERLAY
);
```

This registers the helmet, chestplate, leggings, and boots together. `new G3dArmorRenderer(modelId)` can also be passed to Fabric's `ArmorRenderer.register` for individual addon items. A set with separately authored Steve and Alex assets can use `G3dArmorRenderer.register(wideModelId, slimModelId, armorItems)`. Other humanoid wearers use the wide asset; a missing slim asset falls back to the current wide asset.

### Skin overlay visibility

Registration accepts optional `G3dArmorRenderer.Flag` values. `HIDE_SKIN_OVERLAY` suppresses only the outer player-skin layers for each flagged item while it is equipped:

| Equipped item | Hidden skin overlays |
| --- | --- |
| Helmet | Hat |
| Chestplate | Jacket and both sleeves |
| Leggings | Both pants legs |
| Boots | Both pants legs |

Minecraft has one pants overlay per leg, so boots suppress that complete overlay rather than only its foot section. The base skin and cape remain visible according to their normal rules. Omitting the flag preserves the player's skin-layer settings. Stormtrooper enables it for all four pieces; wearing just one piece hides only its relevant area.

To configure pieces independently, use `G3dArmorRenderer.register(modelId, armorItems.chestplate, flags...)` or its `wideModelId, slimModelId, item, flags...` overload. Register each item once. Skin suppression is captured after vanilla extracts player skin options, before queued rendering, and also reaches first-person sleeves. Removing or replacing flagged armor restores the player's own settings on the next extraction. Spectators retain their normal skin options because worn armor is not rendered for them.

### Authoring contract

Use one upright, feet-origin model in Blockbench's normal units: 16 units per block, with the head/body pivots at Y=24. Keep these group names and pivots:

| Group | Worn item | Source pivot `[x, y, z]` | Native part |
| --- | --- | --- | --- |
| `head` | Helmet | `[0, 24, 0]` | Head |
| `body` | Chestplate | `[0, 24, 0]` | Body |
| `right_arm` | Chestplate | `[5, 22, 0]` | Right arm |
| `left_arm` | Chestplate | `[-5, 22, 0]` | Left arm |
| `waist` | Leggings | `[0, 24, 0]` | Body |
| `right_leg` | Leggings | `[1.9, 12, 0]` | Right leg |
| `left_leg` | Leggings | `[-1.9, 12, 0]` | Left leg |
| `right_boot` | Boots | `[1.9, 12, 0]` | Right leg |
| `left_boot` | Boots | `[-1.9, 12, 0]` | Left leg |

The mesh coordinates are local to their group's pivot. The runtime binds those pivots to the wearer's current native `ModelPart` transforms and converts the Blockbench frame by flipping X/Y. It retains authored rotation and scale; ordinary child groups retain their full local transforms. Pauldrons, pouches, visors, and similar details follow the nearest bound parent. Organizational roots may contain the named anchors, but geometry without a bound ancestor stays hidden. Keep boot geometry separate from legging geometry so equipping both does not draw either piece twice.

For a combined Steve/Alex asset, place the alternative geometry below the same arm anchor, for example `right_arm_default` and `right_arm_slim`. `_default` branches are visible on wide/Steve humanoids; `_slim` branches are visible on slim/Alex players. The plain names `default` and `slim` also work, and filtering applies to all descendants. Shared details can stay directly under the arm anchor. The Stormtrooper source demonstrates both variants without duplicating the whole model. Variant selection uses the skin's native `PlayerModelType`, not the player's name or a hard-coded skin.

Export to `assets/<namespace>/g3d/source/armor/<name>.jg3d` and run the module's normal datagen. The visual and rig projections use the existing G3D compiler; no armor-specific model format, baked cube list, or per-set Java animation code is needed. Display metadata controls item-model views, not worn-armor placement.

### Textures and native rendering

Worn armor automatically selects the sampled path, even for an atlas-capable surface. A direct material such as `pswg:textures/armor/stormtrooper.png` binds that standalone image through Minecraft's texture manager; no atlas declaration or Ptex wrapper is required. Ptex surfaces use the existing reload-aware dynamic-texture manager. Set `atlas: false` on armor-only Ptex documents to avoid generating an unused atlas sprite. Generated graphs retain a stable sampled texture ID while their pixels become ready.

The adapter captures the already-animated context model rather than rerunning its animation or editing shared model parts. This carries head tracking, walking, crouching, riding, item-use poses, and native root transforms into queued armor geometry. Each submission owns its matrices; reload replaces cached rig bindings on the next render. Ordinary cutout surfaces use native armor cutout/no-cull and enchantment-glint shaders, with vanilla packed light, no hurt overlay, and the entity's outline color. Explicit solid/translucent/emissive surfaces retain their G3D entity-layer behavior. Armor remains visible on invisible wearers, matching vanilla. Material tint slot `0` receives the item's dye color; `-1` keeps the original image colors. Vanilla armor trims are not automatically mapped onto a custom G3D UV layout.

## Share item and entity models

Gadget entities use the same compiled models as their item forms. All six grenade renderers use `G3dGrenadeEntityRenderer`; normal and primed appearances select the corresponding `item/*_in_hand` asset. Pressure and tripwire mines use `item/pressure_mine` and `item/tripwire_mine` for inventory, held items, and placed entities. Their source `model` metadata supplies the generated vanilla sidecar. Each model keeps one geometry definition and one texture set for both consumers.

Each shared gadget source has an `entity_origin` socket on its root. In Blockbench, this is a locator placed at the gadget's base. Its position uses source units, just like other sockets. Pass its name to the entity renderer's three-argument constructor:

```java
new G3dEntityRenderer<>(context, modelId, "entity_origin");
```

The renderer places that socket at the entity position and cancels its local orientation before applying the entity's captured transform. This keeps item display placement independent of world placement. The named socket is part of the model contract and must exist in replacement assets. The two-argument constructor still uses the model's own origin. `extractModelId` can select a gameplay variant, and `extractColor` can supply an overall ARGB color. Both run during extraction; submission reads captured data only.

The tripwire's `beam` node is fully collapsed at rest with scale `[0, 0, 0]`, so it is hidden in inventory and while unarmed. Its local +Y mesh has a one-block length. The entity captures a replacement pose that starts at the body, reaches the traced endpoint, and follows wall or ceiling orientation. Its material uses the native emissive entity layer and light level 15. A fully collapsed node and its descendants are skipped before normal-matrix calculation in both static baking and dynamic submission; hidden parts and empty groups do not enlarge posed item bounds. Partial zero scales remain ordinary source transforms.

Tripwire armed state is synchronized by the server and saved with the entity, including for clients that begin tracking an existing mine. Beam length comes from the current local trace and is copied into each render state's pose. No shared model part is changed during submission. Its culling bounds include the visible beam.

`G3dEntityRenderer` respects invisible bodies and native outlines. `G3dRenderer` also has a submission overload for outline-only rendering. Both adapters restore their pose-stack entries if a collector fails, and a resource reload is resolved again on the next extraction.

Both projections expose the same `sourceHash`. Modules should compare hashes when consistency matters, refresh cached rigs on datapack reload, and synchronize gameplay pose inputs through normal networking. G3D does not introduce another animation or networking system.

## Compiled V1 container

All fields are big-endian:

1. `u32` magic `0x47334400` (`G3D` plus zero).
2. `u16` major `1`, `u16` minor `0`.
3. `i32` section count.
4. Each section: `i32` tag, `i32` payload byte length, then payload.

| Tag | Payload |
| --- | --- |
| 1 | Uncompressed vanilla NBT compound encoded by `G3dRig.CODEC`: model ID, canonical-source SHA-256 hash, parent-first nodes, sockets, local node bounds, and model-space rest bounds |
| 2 | Uncompressed NBT compound with the material list, using the source material codec |
| 3 | Packed CPU geometry |

Visual files contain all three sections; server files contain section 1. Rig readers can skip visual data in a complete file. Additive sections and minor versions are skipped; unsupported major versions fail.

Geometry starts with `i32` mesh count. Each mesh stores `i32` node index, material index, vertex count, and index count; then 8 `f32` values per vertex (position XYZ, normal XYZ, UV); then `i32` triangle indices. Positions are in blocks. Repeated source mesh references produce rigid instances on the referencing nodes.

Limits are 128 MiB per file, 64 MiB per section, 16 MiB per metadata section, and 64 sections. Vanilla NBT allocation accounting bounds nested metadata. Geometry counts must fit the remaining section before arrays are allocated. Readers reject truncation, duplicate tags, bad references/indices, and non-finite geometry. Compilation bounds mesh-instance expansion. No textures, GPU buffers, backend state, or Java class names are stored.

Identical canonical source produces identical compiled bytes. Whitespace and exporter-only fields do not affect the source hash. Metadata uses Minecraft codecs/NBT; geometry is a small packed-array contract.
