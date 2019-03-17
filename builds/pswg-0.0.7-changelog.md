# 0.0.7
Changelog:

Feature Additions:
* Added oredict compatibility
* Added reliable multi-part (i.e. ships with multiple seats) ship framework
    * Modified the T-65 X-Wing to use this new system (`/ship xwing`)
    * Added scoot-em-around ("Yavin Troop Transport") ground vehicle (`/ship scoot`)
* New lightsaber trail rendering mechanics
* New ship cinematic follow camera
* Add hyperspace dimension
* Add basic species switching system (`/species <type>` command, supports: `Human`, `Bith_M`, `Bothan_F`, `Bothan_M`, `Chagrian_F`, `Chagrian_M`, `Togruta_F`, `Togruta_M`, `Twilek_F`, and `Twilek_M`)

Changes:
* Lightsabers do less damage when closed
* Cleaned up blaster crosshair overlay
* Updated blaster bolt to glow
* Made smoke grenades more dense

Bufgixes:
* Corrected camera viewport when the player wasn't the camera origin
* Fixed ISD worldgen crash