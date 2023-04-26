# Additions

* Added sprites for remaining armor items
* Proper support for dual-wielding single-handed blasters
  * When holding one in the off-hand and one in the main-hand, left click to fire the off-hand blaster and right click
    to fire the main-hand blaster
* Blaster bolts can now deflect off of some blocks (currently just diatium)
* Blaster bolts can not detonate some blocks (currently just TNT)

# Changes

* Updated ingame links to also reference Modrinth
* Rename "Desert Insurgence Hat" to "Rebel Desert Helmet"
* Update block textures for:
  * Chromium block
  * Chromium ore
  * Cortosis block
  * Cortosis ore
  * Desh block
  * Desh ore
  * Diatium block
  * Diatium ore
  * Durasteel block
  * Helicite block
  * Helicite ore
  * Ionite block
  * Ionite ore
  * Lommite block
  * Lommite ore
  * Thorilide block
  * Zersium block
  * Zersium ore

# Fixes

* Blaster bolts now deflect properly off lightsabers
* Fix blaster bolts disappearing (again)

# Internal Changes

* Downgrade Gradle to 7.6.1 to fix loom decompilation bug
* Abstract out online version checking system
* Added attachment functionality to ion-to-gas and ion-to-repulsor type attachments
