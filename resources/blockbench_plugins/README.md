# PSWG G3D for Blockbench

Build, paint, and place PSWG models with Blockbench's normal mesh, texture, group, locator, and display tools. The plugin handles the G3D material table and the model sidecar for you.

## Load the plugin

Drag `pswg_g3d.js` onto Blockbench, or choose **Load Plugin from File** in the plugin manager. Use Blockbench 5.2.0 or newer.

Choose **G3D V1** when you create a project, or open a `.jg3d` model. To bring over another Blockbench project, use **File → Convert Project** and choose **G3D V1**.

## A normal modeling workflow

1. Build your model with meshes. If you start with cubes, select them and use **Tools → Convert to Mesh** before export.
2. Import or create a texture in the **Textures** panel. Apply it to faces with the normal Blockbench tools, then edit the UVs or paint the image.
3. Right-click a texture and open **Properties** to choose its surface settings.
4. Open the **Display** workspace. Place the model in its hands, inventory view, item frame, shelf, or other views.
5. Save a `.bbmodel` working copy. Use **File → Export → Export G3D Model** to write the `.jg3d` model used by the mod's asset build.

The module's normal datagen run creates the game model sidecar, compiled geometry, and attachment rig from this export. You do not need to copy the display values into a second file.

Groups become model parts. Their names are the names used in the game, and **Scale** changes their rest size. Locators become attachment points, such as a grip or muzzle; name and place them with the normal locator tools. Keep a locator inside the group it should follow.

Gadget models are also used by their thrown or placed entities. Their **entity_origin** locator marks the point placed at the entity's position. Keep that locator when editing a gadget; moving it changes world placement without changing the item's Display values. The tripwire mine's **beam** group is hidden at rest with Scale `[0, 0, 0]`. The entity supplies its live length and orientation. Temporarily set the group's scale to `[1, 1, 1]` to inspect its shape, then restore the rest scale before export.

A mesh can use several textures. The exporter splits it into the required surfaces for you. For faces without a texture, it uses the mesh's only assigned texture, or Blockbench's default/selected texture.

## Make worn armor

The Stormtrooper model is a working combined armor template at `projects/pswg_core/src/main/resources/assets/pswg/g3d/source/armor/stormtrooper.jg3d`. Open it with the plugin to reuse its group layout, pivots, and Steve/Alex arm branches.

- Keep the `head`, `body`, `right_arm`, `left_arm`, `right_leg`, and `left_leg` group names. Put boots in `right_boot` and `left_boot`; a leggings belt can use a separate `waist` group.
- Add details beneath the part they should follow. A pouch under an arm moves with that arm; a pauldron under the body follows the torso.
- Put Steve's four-unit arms in the `_default` child groups and Alex's three-unit arms in `_slim` child groups. The game selects the appropriate branches from the player's skin model. Keep shared details outside those two branches.
- Model upright in the normal feet-origin frame. The armor renderer handles Minecraft's coordinate conversion and uses the wearer's native animation. Display values do not position worn armor.
- Assign a normal image, such as `pswg:textures/armor/stormtrooper.png`. Worn armor samples it directly; it does not need to be stitched into an atlas. Runtime Ptex surfaces can also supply dynamic textures.
- Export to the module's `assets/<namespace>/g3d/source/armor/` folder and run its normal datagen. Register the set once with `G3dArmorRenderer.register(modelId, armorItems)` during client startup.

See [the humanoid armor contract](../../G3D.md#humanoid-armor) for pivot coordinates, separate Steve/Alex assets, dye slots, and rendering behavior. The same plugin/export format handles armor and ordinary models.

## Texture surface settings

Each entry in the **Textures** panel owns one set of settings:

| Control | What it does |
| --- | --- |
| **Opaque** | Ignores image alpha and shows every pixel as solid. |
| **Cutout** | Shows solid pixels and removes clear pixels. This is the default. |
| **Translucent** | Keeps partial transparency, such as glass. |
| **Glow** | Starts with a fully bright cutout surface. Clear pixels stay clear. |
| **Glow with transparency** | Starts with a fully bright surface that keeps partial transparency. |
| **Glow strength** | Sets the surface light level from 0 to 15. At 0 it uses scene light; at 15 it stays fully bright. It does not light nearby blocks. |
| **Show both sides** | Shows the back of each face too, which is useful for sheets and fins. New textures have this enabled. Imported settings are retained. |

You can also choose a surface preset from the texture's **Render Mode** context menu. The choice applies to that texture and any other selected textures. Use **Properties** to fine-tune it.

The viewport previews changes while the properties dialog is open. **Cancel** restores the previous preview. Confirmed changes use Blockbench's normal undo/redo history.

### One image, different surfaces

Use **Duplicate** in the texture context menu, or import the same image again. Each copy gets its own name and surface settings. For example:

1. Keep one copy as **Cutout** for the body of a blaster.
2. Set the other copy to **Glow** for its indicator lights.
3. Apply each texture to the faces that need it.

Both surfaces can use the same game image. Renaming a texture in Blockbench changes its display name, not that game image path. Native Duplicate copies the current image and settings; importing the file again creates a new texture with default settings.

## Open an existing model

The plugin creates one editable texture for each imported surface. It keeps different surfaces separate even if they use the same image. Existing texture settings and original vertex normals are retained. After a vertex moves, export uses Blockbench's smooth or flat normals for that vertex.

Images load automatically when the model is inside its usual resource-pack `assets/<namespace>/g3d/source/` folder. Direct images and simple Ptex source graphs can be shown locally. If an image cannot be found, the texture entry and its settings still remain. Use **Change File** in the texture context menu to select an image for it.

**Change File** also updates the game image path from the new file. To keep a Ptex graph as the game surface, restore its path in **Advanced settings → Game texture** after choosing the local preview image.

## Advanced settings

Most artists can leave this section closed. It keeps imported settings available for content authors:

- **Game texture** is set from the imported image or model. It can point to a direct Minecraft image or a Ptex surface.
- **Game tint slot** selects a tint supplied by the game. `-1` keeps the image colors. The viewport shows the image colors because it has no game tint provider.
- **Preview as** selects the item, block, or entity settings used by the viewport. It changes only the editor preview.
- **Keep imported surface settings** preserves models with different or custom game layers. Their layer paths appear in Advanced settings. Choosing a normal preset replaces all three layer choices.

The viewport uses Blockbench's lighting with a glow light floor. At full strength, glow shows the image colors without diffuse shading or scene light. It keeps clear pixels clear. The game supplies its own world lighting; unsupported custom layers use a cutout preview. Ptex graphs other than simple image sources retain their game path but need a local image for the editor preview.

## Place the model with Display

Use the normal **Display** workspace to adjust rotation, position, and size for each view. All nine Java views are supported: both first-person hands, both third-person hands, head, ground, item frame, shelf, and GUI. Native presets, copy/paste, mirroring, and undo/redo remain available.

The GUI view also has Blockbench's **GUI Light** control. Shelf alignment changes the reference preview. Display values use the game's normal model origin and units, so they do not need an extra conversion after export.

Resetting a view exports an explicit identity transform. This keeps a parent model from putting an old transform back. Display changes are retained in `.bbmodel` files and in the `.jg3d` export.

When you open an older source that has a separate local model sidecar, the plugin carries over its display, GUI light, ambient occlusion, parent, and texture slots. Existing particle choices are retained. For a new standalone model, datagen chooses a particle from the first atlas-capable surface. You can also select a texture for particles with Blockbench's normal texture context menu.

## For plugin developers

The plugin stores material data on `Texture` properties so native save, copy, and undo operations retain it. Faces refer to each texture's Blockbench UUID. Export generates stable, distinct material IDs per texture instance. Earlier `.bbmodel` files with a project-level material table are upgraded when opened or exported.

The adapters are based on the Blockbench 5.2 implementation. They extend the native properties form before it builds, suppress file-path merging during native texture loading, and extend the native preview shader. Shader edits match GLSL tokens because release builds minify the shader text. The new uniforms live on the material itself. Other formats keep their normal texture behavior. Unloading cancels live previews and removes the adapters, listeners, shader changes, and registered properties.

The source's optional `model` object contains vanilla sidecar metadata. `G3dModelProvider` validates it with the native model parser and writes `src/main/generated/assets/<namespace>/models/<path>.json`. Older sources without `model` can still use a separately authored sidecar. The geometry codec ignores this client-only metadata, so display-only changes do not change the compiled geometry or shared rig hash. The toolchain copies generated resources after authored resources when it assembles artifacts.

See [`../../G3D.md`](../../G3D.md) for the source schema, runtime layers, and resource layout. The exported `.jg3d` file remains G3D V1; the plugin does not add a new runtime material format.
