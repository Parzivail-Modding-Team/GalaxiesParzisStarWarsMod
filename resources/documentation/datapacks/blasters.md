# Blaster Datapacks

Blasters require two components in a datapack to be available ingame:

* JSON component
  * Defines the properties, statistics, and attachment definitions
* Model component
  * Defines how the item visually appears ingame

## Model component

TODO

## JSON Component

### Table of Terminology

| Term | Definition | Example |
| --- | --- | --- |
| | **Technical Terms**  | |
| Identifier | A string consisting of a namespace and a path, separated by a colon. | `pswg:a280` |
| Number | A decimal number | `1.23` |
| Integer | A non-decimal number | `87654` |
| Short integer | A non-decimal number between 1 and 32768 | `32767` |
| Bitmap | A 32-bit integer where every bit corresponds to a boolean | `6` (`110` in binary, the first two bits are `true` and the last is `false`)
| Bitfield | A bitmask with exactly one bit set, i.e. a power of two | `4` |
| | **Mechanics**  | |
| Tick | The interval at which the game updates. Ideally 1/20th of a second | |
| Cooling | The period during which the blaster is losing heat instead of accumulating it | |
| Venting | The period during which the blaster is cooling down and cannot be fired | |
| Overheating | The condition when a blaster is fired without manually venting heat for long enough that the heat accumulated is greater thean the heat capacity, and the player is forced to wait for the blaster to completely cool before firing again | |
| Overcharge | A grace period during which the blaster can be fired without accumulating heat | |
| Cooling bypass | A way to end an overheat vent early by completing a timing-based minigame on the blaster cooldown bar | |
| Primary bypass | The first, typically wider, cooling bypass target | |
| Secondary bypass | The second, typically more difficult, cooling bypass target which, if triggered, provides an overcharge | |

### Structure

* (JSON root)
  * `version` (integer) Must be `1`.
  * `type` (string) The blaster's general category. Must be one of: `pistol`, `rifle`, `sniper`, `heavy`, `slug`, or `ion`.
  * `sound` (identifier) The namespaced ID of the sound made when firing. Example: `pswg:a280`
  * `firingModes` (string array) The available modes of operation in the order they appear when cycling modes ingame. Each item must be one of: `semi`, `burst`, `auto`, `stun`, `slug`, `ion`.
  * `damage` (number) The damage, in hit points (half hearts) a single shot inflicts.
  * `range` (number) The maximum distance, in blocks, a blaster can fire a bolt
  * `boltColor` (number) The color of the fired bolt as a hue between zero and one.
  * `magazineSize` (integer) The number of shots the blaster can fire before reloading.
  * `automaticRepeatTime` (short integer) The time, in ticks, between shots firing during automatic fire.
  * `burstRepeatTime` (short integer) The time, in ticks, between shots firing during burst fire.
  * `burstSize` (short integer) The number of shots fired during a burst.
  * `recoil`
    * `horizontal` (number) The maximum amount of horizontal (yaw) displacement, in degrees, the gun may apply to the player after firing one bolt.
    * `vertical` (number) The maximum amount of upwards-only vertical (pitch) displacement, in degrees, the gun may apply to the player after firing one bolt.
  * `spread`
    * `horizontal` (number) The maximum amount of horizontal (yaw) displacement, in degrees, that may be applied to a bolt when fired.
    * `vertical` (number) The maximum amount of vertical (pitch) displacement, in degrees, that may be applied to a bolt when fired.
  * `heat`
    * `capacity` (integer) The maximum amount of heat units that may be accumulated before overheating.
    * `perRound` (short integer) The amount of heat units accumulated per shot fired.
    * `passiveCooldownDelay` (short integer) The amount of time, in ticks, after the most recent shot was fired before the blaster will begin to passively cool without venting.
    * `drainSpeed` (short integer) The amount of heat units removed from the blaster per tick while passively cooling or venting.
    * `overheatDrainSpeed` (short integer) The amount of heat units removed from the blaster per tick while venting due to an overheat.
    * `overheatPenalty` (short integer) The amount of "extra" heat units accumulated when the blaster overheats, effectively delaying the blaster from beginning to cool by `overheatPenalty / (overheatDrainSpeed * 20)` seconds.
    * `overchargeBonus` (short integer) The amount of time, in ticks, the blaster stays in overcharge when the secondary bypass is triggered.
  * `cooling`
    * `primaryBypassTime` (number) The center position of the primary bypass target, as a percentage from 0 (leftmost) to 1 (rightmost).
    * `primaryBypassTolerance` (number) The radius of the primary bypass target, as a percentage from 0 (disabled) to 1 (width of cooldown bar).
    * `secondaryBypassTime` (number) The center position of the secondary bypass target, from 0 (leftmost) to 1 (rightmost)
    * `secondaryBypassTolerance` (number) The radius of the secondary bypass target, as a percentage from 0 (disabled) to 1 (width of cooldown bar).
  * `attachmentDefault` (bitmap) The set of bits corresponding to the attachments found on the blaster in the Creative inventory. (See: Attachment System)
  * `attachmentMinimum` (bitmap) The set of bits corresponding to the attachments which will be automatically added back to the blaster if the player attempts to remove all attachments in a given category. (See: Attachment System)
  * `attachmentMap` (array)
    * *n* (string) A unique bitfield that defines the following attachment
      * `id` (string) The name of the attachment rendered in the UI
      * `visualComponent` (string) The name of the submesh that should be rendered or hidden with respect to the attachment (See: Attachment System)
      * `icon` (integer) The icon ID that best represents the attachment (See: Icon Table)
      * `mutex` (bitmap) The set of bits corresponding to the attachments that make up the **mut**ually-**ex**clusive attachment set this attachment belongs to (See: Attachment System)

### Icon Table

| ID | Description | Image |
| --- | --- | --- |
| 0 | Blank | ![Blank icon](blaster_attachment_icon_blank.png) |
| 1 | Barrel | ![Barrel icon](blaster_attachment_icon_barrel.png) |
| 2 | Scope | ![Scope icon](blaster_attachment_icon_scope.png) |
| 3 | Stock | ![Stock icon](blaster_attachment_icon_stock.png) |
| 4 | Holographic crosshair | ![Holographic crosshair icon](blaster_attachment_icon_holo_crosshair.png) |
| 5 | Sniper crosshair | ![Sniper crosshair icon](blaster_attachment_icon_sniper_crosshair.png) |
| 6 | Snowflake crosshair | ![Snowflake crosshair icon](blaster_attachment_icon_snowflake_crosshair.png) |

### Example

This example blaster defines three attachments: one scope, and two barrel sizes, of which exactly one must be applied at a time.

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

The attachment system has four fundemental components:

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

Since both Attachments B and C share the same mutex, and the mutex is comprised of the bitfields for both Attachment B and C, the blaster is required to have exactly one of B _or_ C attached at all times. Hence, they are **mut**ually **ex**clusive.

The examples also consider the hypothetical blaster to have these initial properties:

```
Attachment bitmap    : 00000101
Attachment minumum   : 00000010
```

### Rendering

When a blaster is rendered, only the stack's attachment bitmap and each attachments' bitfield are considered. The attachments of the example blaster are rendered as follows:

```
Attachment bitmap    : 00000101
                       ||||||||
Attachment A bitfield: 00000001
Attachment B bitfield: 00000010
Attachment C bitfield: 00000100
```

Because the first and third bits of the attachment bitmap are set, the attachments with bitfield `1` (Attachment A) and `100` (Attachment C) are rendered, because their fields have the corresponding bits set.

### Attachment Addition

When an attempt is made to apply a new attachment, the system considers all four fundemental components.

Consider the example blaster, with has Attachment A and Attachment C:

```
Attachment bitmap    : 00000101
```

Suppose we wish to add Attachment B. First, the system uses the mutex of Attachment B to remove any attachments that are mutually exclusive to it by clearing all bits from the attachment bitmap present in the mutex:

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

Then, the mutex of Attachment B is combined with the attachment minumum and the attachment bitmap to determine if the attachment requirements are met:

```
Attachment bitmap    : 00000001
                   AND ||||||||
Attachment minimum   : 00000010
                   AND ||||||||
Attachment B mutex   : 00000110
                     = ||||||||
Resulting bitmap     : 00000000
```

Since the resulting bitmap is zero, no attachments in the set defined by Attachment B's mutex are present. Therefore, the required attachment from the attachment minimum is selected.

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

The resulting bitmap is then stored in the stack. In this example we removed Attachment B only to have it immediately added back, but in practice, any situation that results in an attachment mutually-exclusive set being empty will add the minimum attachment for that set back to the blaster.