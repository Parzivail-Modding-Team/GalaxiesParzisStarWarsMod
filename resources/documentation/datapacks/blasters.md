| Document Information   |               |
|------------------------|--------------:|
| Specification revision | Jan. 25, 2022 |
| Supported file version |             1 |

---

**All information in this file is subject to change without notice. While we make our best effort to maintain backwards
compatibility with previous revisions of this specification, we cannot guarantee it while PSWG is in alpha.**

---

# Blaster Datapacks

Blasters require two components in a datapack to be available in-game:

* Model component
    * Defines how the item visually appears in-game
* JSON component
    * Defines the properties, statistics, and attachment definitions

## Model component

### Location

The mod searches for blaster models in the `assets/pswg/models/item/blaster` directory of a datapack. The name of the
model should be the same as the name of the accompanying JSON file.

### Format

Blasters use a custom model format called P3D (Parzi 3D). P3D files can be obtained by compiling P3Di (Parzi 3D
Intermediary) models which can currently be exported by the following software:

* Blender (using addon: [io_scene_p3di](https://raw.githubusercontent.com/Parzivail-Modding-Team/GalaxiesParzisStarWarsMod/master/resources/blender_addons/io_scene_p3di.py))

#### Conventions

The following conventions will assume you're using Blender, and will use Blender terminology.

##### General Model Conventions

* Up is +Z. This is converted to +Y during compilation.
* Forward is +Y. This is converted to +Z during compilation.
* One pixel is 0.1m (10cm).
* The name of the Object becomes the name of the submesh.
* All Objects **must** have their *rotation* and *scale* transformations **applied** _(Ctrl+A, Rotation & Scale)_ before
  export.
    * The origin of an Object becomes the rotation point of a submesh in a compiled model.
* **Sockets** are defined by adding Empties of type Arrows _(Add, Empty, Arrows)_.
    * The name of the Empty becomes the name of the socket.
    * The axes as drawn directly correspond to the local coordinate space of the in-game object.
* Objects may use the scene hierarchy to define rotation-based parenting.
    * This applies to both Empties (sockets) and Objects (submeshes).
* All polygons must either be triangles (3 vertices) or quads (4 vertices). No polygon may be an "n-gon" (more than 4
  vertices).
  * A quick way to ensure this is to enter Edit mode _(Tab)_, select all _(A)_, Triangulate Faces _(Ctrl+T_ or _Face,
  Triangulate Faces)_, then convert to quads as desired _(Alt-J_ or _Face, Tris to Quads)_.
* Every material used on an exported Object must be named one of the following:
  * `MAT_DIFFUSE_OPAQUE`: No transparency will be applied. Uses standard diffuse lighting.
  * `MAT_DIFFUSE_CUTOUT`: Pixels will be rounded to full or zero opacity (cutout transparency). Uses standard diffuse lighting.
  * `MAT_DIFFUSE_TRANSLUCENT`: Partially translucent pixels will be rendered accordingly. Uses standard diffuse
    lighting.
  * `MAT_EMISSIVE`: Cutout transparency. All pixels will be rendered at full brightness regardless of lighting condition.

##### Blaster Model Conventions

* Blasters should be scaled such that the trigger is roughly three pixels (30cm) tall. This is a good reference because, 
  across guns, the trigger, and therefore trigger guard, does not scale with the caliber or firepower of the gun. You may
  find it helpful to compare your model against a blaster model in the [model resource folder](https://github.com/Parzivail-Modding-Team/GalaxiesParzisStarWarsMod/tree/master/resources/models).
* Blasters may __optionally__ define any of the following sockets:
  * `main_hand`: The position that will be gripped by the player's main hand. If undefined, the model origin will be used.
  * `off_hand`: The position *and orientation* of the player's offhand. If undefined, no offhand will be rendered.
  * `muzzle_flash`: The position and orientation of the default (see `muzzle_flash.[x]`) muzzle flash animation.
  * `muzzle_flash.[x]`: The position and orientation of the muzzle flash when an attachment named `x` is attached. For
    example: A hypothetical blaster has an attachment (and Object) named `long_barrel`. If a socket named `muzzle_flash.long_barrel`
    exists, the muzzle flash will render at that socket instead of the default `muzzle_flash` socket when that attachment is
    attached.
* All attachments should be visible when exported and in the location they should appear in-game.

#### Exporting a P3Di model

With the **io_scene_p3di** addon installed and enabled, simply _(File, Export, "Parzi 3D Intermediary (.p3di)")_, select
a filename and location for the exported model, and export. Only visible Objects will be exported.

#### Compiling a P3Di model into a P3D model

A tool called P3diTools is required to compile the model. Download the [latest release here](https://github.com/Parzivail-Modding-Team/P3diTools/releases).

From the command line in the directory where P3diTools is located, run the following command, replacing `/path/to/my_model.p3di`
with the absolute path of the P3Di model you exported in the previous step.

```
P3diTools compile -m /path/to/my_model.p3di
```

If all goes well, an output file called `my_model.p3d` will be created. If something goes wrong, the software will alert
you to the issue. The resulting P3D model is ready to use in-game.

In the command above, the `-m` option specifies that a model file (`p3d`) should be exported. You may find it helpful to
read the other processing and export at your disposal by running the following command in the same manner as before:

```
P3diTools compile --help
```

## JSON Component

### Location

The mod searches for blaster descriptors in the `data/pswg/items/blasters` directory of a datapack. The name of the JSON
file is authoritative, that is, the name of the file becomes the ID of the blaster descriptor. For example, a file at `
data/pswg/items/blasters/a280.json` would be referred to as `pswg:a280` internally.

### Tables of Terminology

#### Technical Terms

| Term          | Definition                                                           | Example                                                                      |
|---------------|----------------------------------------------------------------------|------------------------------------------------------------------------------|
| Identifier    | A string consisting of a namespace and a path, separated by a colon. | `pswg:a280`                                                                  |
| Number        | A decimal number                                                     | `1.23`                                                                       |
| Integer       | A non-decimal number                                                 | `87654`                                                                      |
| Short integer | A non-decimal number between 1 and 32768                             | `32767`                                                                      |
| Bitmap        | A 32-bit integer where every bit corresponds to a boolean            | `6` (`110` in binary, the first two bits are `true` and the last is `false`) |
| Bitfield      | A bitmask with exactly one bit set, i.e. a power of two              | `4`                                                                          |

#### Mechanics

| Term             | Definition                                                                                                                                                                                                                                |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Tick             | The interval at which the game updates. Ideally 1/20th of a second                                                                                                                                                                        |
| Cooling          | The period during which the blaster is losing heat instead of accumulating it                                                                                                                                                             |
| Venting          | The period during which the blaster is cooling down and cannot be fired                                                                                                                                                                   |
| Overheating      | The condition when a blaster is fired without manually venting heat for long enough that the heat accumulated is greater thean the heat capacity, and the player is forced to wait for the blaster to completely cool before firing again |
| Overcharge       | A grace period during which the blaster can be fired without accumulating heat                                                                                                                                                            |
| Cooling bypass   | A way to end an overheat vent early by completing a timing-based minigame on the blaster cooldown bar                                                                                                                                     |
| Primary bypass   | The first, typically wider, cooling bypass target                                                                                                                                                                                         |
| Secondary bypass | The second, typically more difficult, cooling bypass target which, if triggered, provides an overcharge                                                                                                                                   |

### Structure

* (JSON root)
    * `version` (integer) Must be `1`.
    * `type` (string) The blaster's general category. Must be one of: `pistol`, `rifle`, `sniper`, `heavy`, `slug`,
      or `ion`.
    * `sound` (identifier) The namespaced ID of the sound made when firing. Example: `pswg:a280`
    * `firingModes` (string array) The available modes of operation in the order they appear when cycling modes in-game.
      Each item must be one of: `semi`, `burst`, `auto`, `stun`, `slug`, `ion`.
    * `damage` (number) The damage, in hit points (half hearts) a single shot inflicts.
    * `range` (number) The maximum distance, in blocks, a blaster can fire a bolt
    * `boltColor` (number) The color of the fired bolt as a hue between zero and one.
    * `magazineSize` (integer) The number of shots the blaster can fire before reloading.
    * `automaticRepeatTime` (short integer) The time, in ticks, between shots firing during automatic fire.
    * `burstRepeatTime` (short integer) The time, in ticks, between shots firing during burst fire.
    * `burstSize` (short integer) The number of shots fired during a burst.
    * `recoil`
        * `horizontal` (number) The maximum amount of horizontal (yaw) displacement, in degrees, the gun may apply to
          the player after firing one bolt.
        * `vertical` (number) The maximum amount of upwards-only vertical (pitch) displacement, in degrees, the gun may
          apply to the player after firing one bolt.
    * `spread`
        * `horizontal` (number) The maximum amount of horizontal (yaw) displacement, in degrees, that may be applied to
          a bolt when fired.
        * `vertical` (number) The maximum amount of vertical (pitch) displacement, in degrees, that may be applied to a
          bolt when fired.
    * `heat`
        * `capacity` (integer) The maximum amount of heat units that may be accumulated before overheating.
        * `perRound` (short integer) The amount of heat units accumulated per shot fired.
        * `passiveCooldownDelay` (short integer) The amount of time, in ticks, after the most recent shot was fired
          before the blaster will begin to passively cool without venting.
        * `drainSpeed` (short integer) The amount of heat units removed from the blaster per tick while passively
          cooling or venting.
        * `overheatDrainSpeed` (short integer) The amount of heat units removed from the blaster per tick while venting
          due to an overheat.
        * `overheatPenalty` (short integer) The amount of "extra" heat units accumulated when the blaster overheats,
          effectively delaying the blaster from beginning to cool by `overheatPenalty / (overheatDrainSpeed * 20)`
          seconds.
        * `overchargeBonus` (short integer) The amount of time, in ticks, the blaster stays in overcharge when the
          secondary bypass is triggered.
    * `cooling`
        * `primaryBypassTime` (number) The center position of the primary bypass target, as a percentage from 0 (
          leftmost) to 1 (rightmost).
        * `primaryBypassTolerance` (number) The radius of the primary bypass target, as a percentage from 0 (disabled)
          to 1 (width of cooldown bar).
        * `secondaryBypassTime` (number) The center position of the secondary bypass target, from 0 (leftmost) to 1 (
          rightmost)
        * `secondaryBypassTolerance` (number) The radius of the secondary bypass target, as a percentage from 0 (
          disabled) to 1 (width of cooldown bar).
    * `attachmentDefault` (bitmap) The set of bits corresponding to the attachments found on the blaster in the Creative
      inventory. (See: [Attachment System](#attachment-system))
    * `attachmentMinimum` (bitmap) The set of bits corresponding to the attachments which will be automatically added
      back to the blaster if the player attempts to remove all attachments in a given category. (
      See: [Attachment System](#attachment-system))
    * `attachmentMap` (array)
        * *n* (string) A unique bitfield that defines the following attachment
            * `id` (string) The name of the attachment rendered in the UI
            * `visualComponent` (string) The name of the submesh that should be rendered or hidden with respect to the
              attachment (See: [Attachment System](#attachment-system))
            * `texture` (Identifier) The optional override texture for the attachment to render with, defaulting to the
              blaster base texture.
            * `icon` (integer) The icon ID that best represents the attachment (See: [Icon Table](#icon-table))
            * `mutex` (bitmap) The set of bits corresponding to the attachments that make up the **mut**ually-**ex**
              clusive attachment set this attachment belongs to (See: [Attachment System](#attachment-system))

### Icon Table

| ID  | Description           | Image                                                                        |
|-----|-----------------------|------------------------------------------------------------------------------|
| 0   | Blank                 | ![Blank icon](blaster_attachment_icon_blank.png)                             |
| 1   | Barrel                | ![Barrel icon](blaster_attachment_icon_barrel.png)                           |
| 2   | Scope                 | ![Scope icon](blaster_attachment_icon_scope.png)                             |
| 3   | Stock                 | ![Stock icon](blaster_attachment_icon_stock.png)                             |
| 4   | Holographic crosshair | ![Holographic crosshair icon](blaster_attachment_icon_holo_crosshair.png)    |
| 5   | Sniper crosshair      | ![Sniper crosshair icon](blaster_attachment_icon_sniper_crosshair.png)       |
| 6   | Snowflake crosshair   | ![Snowflake crosshair icon](blaster_attachment_icon_snowflake_crosshair.png) |

### Example

This example blaster defines three attachments: one scope, and two barrel sizes, of which exactly one must be applied at
a time.

```json
{
	"version": 1,
	"type": "rifle",
	"sound": "my_datapack:firing_sound_1",
	"firingModes": [
		"auto",
		"burst",
		"stun"
	],
	"damage": 3.06,
	"range": 188.0,
	"boltColor": 0.98,
	"magazineSize": 20,
	"automaticRepeatTime": 3,
	"burstRepeatTime": 2,
	"burstSize": 3,
	"recoil": {
		"horizontal": 1.5,
		"vertical": 3
	},
	"spread": {
		"horizontal": 0.25,
		"vertical": 0.25
	},
	"heat": {
		"capacity": 1008,
		"perRound": 42,
		"drainSpeed": 12,
		"overheatPenalty": 20,
		"overheatDrainSpeed": 14,
		"passiveCooldownDelay": 100,
		"overchargeBonus": 80
	},
	"cooling": {
		"primaryBypassTime": 0.7,
		"primaryBypassTolerance": 0.1,
		"secondaryBypassTime": 0.3,
		"secondaryBypassTolerance": 0.05
	},
	"attachmentDefault": 1,
	"attachmentMinimum": 1,
	"attachmentMap": {
		"1": {
			"mutex": 3,
			"id": "long_barrel",
			"icon": 1,
			"visualComponent": "long_barrel_mesh"
		},
		"2": {
			"mutex": 3,
			"id": "short_barrel",
			"icon": 1,
			"visualComponent": "short_barrel_mesh"
		},
		"4": {
			"mutex": 4,
			"id": "scope",
			"icon": 2,
			"visualComponent": "scope_mesh"
		}
	}
}
```

## Attachment System

The attachment system has four fundamental components:

* The blaster stack's attachment bitmap
* The attachment's unique bitfield
* The attachment's mutex bitmap
* The blaster's _minimum attachment_

For the following examples, three hypothetical attachments are defined:

```
              Bitfield  Mutex
Attachment A  00000001  00000001
Attachment B  00000010  00000110
Attachment C  00000100  00000110
```

Since both Attachments B and C share the same mutex, and the mutex is comprised of the bitfields for both Attachment B
and C, the blaster can have only one of B or C attached at any time. Hence, they are **mut**ually **ex**clusive. *(Note:
this example blaster must have exactly one of B and C attached at all times due to the later described **attachment
minimum**, see [Attachment Removal](#attachment-removal))*

The examples also consider the hypothetical blaster to have these initial properties:

```
Attachment bitmap    : 00000101
Attachment minumum   : 00000010
```

### Rendering

When a blaster is rendered, only the stack's attachment bitmap and each attachments' bitfield are considered. The
attachments of the example blaster are rendered as follows:

```
Attachment bitmap    : 00000101
                       ||||||||
Attachment A bitfield: 00000001
Attachment B bitfield: 00000010
Attachment C bitfield: 00000100
```

Because the first and third bits of the attachment bitmap are set, the attachments with bitfield `1` (Attachment A)
and `100` (Attachment C) are rendered, because their fields have the corresponding bits set.

### Attachment Addition

When an attempt is made to apply a new attachment, the system considers all four fundemental components.

Consider the example blaster, with has Attachment A and Attachment C:

```
Attachment bitmap    : 00000101
```

Suppose we wish to add Attachment B. First, the system uses the mutex of Attachment B to remove any attachments that are
mutually exclusive to it by clearing all bits from the attachment bitmap present in the mutex:

```
Attachment bitmap    : 00000101
                 CLEAR ||||||||
Attachment B mutex   : 00000110
                     = ||||||||
Resulting bitmap     : 00000001
```

Then, the attachment bitfield of Attachment B is set in the attachment bitmask:

```
Attachment bitmap    : 00000101
                   SET ||||||||
Attachment B bitfield: 00000010
                     = ||||||||
Resulting bitmap     : 00000011
```

The resulting bitmap is then stored in the stack.

### Attachment Removal

Suppose we wish to now remove Attachment B. First, the system clears the bit corresponding to Attachment B's bitfield:

```
Attachment bitmap    : 00000011
                 CLEAR ||||||||
Attachment B bitfield: 00000010
                     = ||||||||
Resulting bitmap     : 00000001
```

Then, the mutex of Attachment B is combined with the attachment minumum and the attachment bitmap to determine if the
attachment requirements are met:

```
Attachment bitmap    : 00000001
                   AND ||||||||
Attachment minimum   : 00000010
                   AND ||||||||
Attachment B mutex   : 00000110
                     = ||||||||
Resulting bitmap     : 00000000
```

Since the resulting bitmap is zero, no attachments in the set defined by Attachment B's mutex are present. Therefore,
the required attachment from the attachment minimum is selected.

```
Attachment minimum   : 00000010
                   AND ||||||||
Attachment B mutex   : 00000110
                     = ||||||||
Required attachment  : 00000010
```

The attachment defined by the resulting bitmap is then applied.

```
Attachment bitmap    : 00000001
                   SET ||||||||
Required attachment  : 00000010
                     = ||||||||
Resulting bitmap       00000011
```

The resulting bitmap is then stored in the stack. In this example we removed Attachment B only to have it immediately
added back, but in practice, any situation that results in an attachment mutually-exclusive set being empty will add the
minimum attachment for that set back to the blaster.
