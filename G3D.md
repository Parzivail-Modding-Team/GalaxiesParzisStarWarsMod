# G3D V1 — Galaxies 3D

G3D supplies indexed geometry, rigid named groups, materials, and attachment sockets to blocks, items, and entities. `.jg3d` is the authoring JSON format; `.g3d` is its compiled binary format. The common model code has no client or rendering dependencies.

## Resource layout

For model `example:item/tool`, use these paths within the owning module:

| Resource | Location |
| --- | --- |
| Authoring source | `src/main/resources/assets/example/g3d/source/item/tool.jg3d` |
| Vanilla sidecar | `src/main/resources/assets/example/models/item/tool.json` |
| Ptex definition | `src/main/resources/assets/example/ptex/tool_surface.json` |
| Generated visual model | `src/main/generated/assets/example/models/item/tool.g3d` |
| Generated gameplay rig | `src/main/generated/data/example/g3d/rigs/item/tool.g3d` |

The owning module's normal Fabric datagen run compiles the source. Official modules exclude `.jg3d` files and old `assets/**/datagen/**` references from release jars. Third-party modules should use those artifact exclusions and add `G3dModelProvider` after loading `G3dModelProvider.SOURCES` with `DataGenResourceHelper`.

Vanilla sidecars are ordinary authored model JSON. They keep display transforms, parent inheritance, GUI lighting, ambient occlusion, particle fallback, and texture slots needed by vanilla. Geometry and Ptex surfaces come from the compiled model with the same identifier. Entity-only models do not need a sidecar.

The active GQB geometry has been reauthored as 56 G3D sources with the existing model IDs, texture coordinates, groups, and display sidecars. The old JSON authoring references remain available for inspection; the GQB compiler, decoder, and rendering adapters have been removed. G3D datagen reads only `.jg3d` sources.

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
- Extra JSON fields are ignored. Degenerate faces, zero scales, unusual UVs, and non-unit normals are allowed.

## Ptex surfaces

A material's `texture` identifies a Ptex document. For `example:tool_surface`, write `assets/example/ptex/tool_surface.json`:

```json
{
  "atlas": true,
  "graph": {
    "type": "pswg:source",
    "texture": "example:textures/item/tool.png"
  }
}
```

| Built-in service | Fields |
| --- | --- |
| `pswg:source` | `texture`: image identifier, including `textures/` and `.png` |
| `pswg:tint` | `color`: an ARGB integer or vanilla color-codec value; `input`: another graph |
| `pswg:composite` | `layers`: graphs ordered from bottom to top |

`atlas` defaults to `true`. Fabric's `SpriteSourceRegistry` generates atlas-capable graphs during reload, before model baking. Their stable sprite name prepends `ptex/` to the definition's path. The official source adds them to the block atlas, which vanilla supports for both blocks and items. Direct-image graphs retain vanilla animation metadata; tint/composite graphs produce a static image per reload.

The atlas declaration is `projects/pswg_core/src/main/resources/assets/minecraft/atlases/blocks.json`. It belongs to the main mod resource pack, alongside `fabric.mod.json` and the Ptex definitions. A client classpath directory is not automatically a Fabric resource-pack root, so atlas declarations in `src/client/resources` can be invisible in development launches. The sprite-source implementation and its registration stay in client Java sources.

Every graph also has sampled output. `atlas: false` selects sampled-only surfaces; items then use the native special-model path. Chunk-rendered blocks require atlas output. Sampled or changing block textures need a block-entity/special renderer using `G3dRenderer`.

On the render thread, sampled requests return a stable texture ID immediately. Generated graphs begin with a visible checkerboard placeholder. Shared background work replaces the image at that ID, and reload cancels stale work. Failures are logged and keep the placeholder. Addons can register graph codecs with `PtexCodecs.register` during client startup.

## Native layers and rendering

| Target | Identifiers |
| --- | --- |
| Block | `minecraft:block/solid`, `minecraft:block/cutout`, `minecraft:block/translucent` |
| Item | `minecraft:item/solid`, `minecraft:item/cutout`, `minecraft:item/translucent` |
| Entity | `minecraft:entity/solid`, `minecraft:entity/cutout`, `minecraft:entity/cutout_no_cull`, `minecraft:entity/translucent`, `minecraft:entity/translucent_emissive` |

Block and item layers are independent. Static baking produces vanilla `BakedQuad.MaterialInfo`; sampled items use native item render types, including standard foil. Unknown layer identifiers are logged once and use cutout. `doubleSided` selects native no-cull layers or adds reversed faces for culling layers. `lightEmission` is a level from 0 to 15.

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
