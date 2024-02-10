# Additions

* Added thermal detonator
  * Can be primed before being thrown
  * Will explode in your inventory if left primed for too long or if you take explosion damage
  * Will bounce off different blocks before reaching its target
  * Can be thrown without being primed
  * Can be set off with blaster bolts
* Added block of thermal detonators
* Added rusted imperial panel stairs
* Added rusted imperial panel slabs
* Added 12 new imperial flooring blocks
* Added 5 new imperial lighting panels
* Added a medical corrugated crate
* Added a new config option for servers that can disable destruction from PSWG sources, `allowDestruction`

# Changes

* Imperial blocks with "lit" and "unlit" variants have now been merged into single blocks
  * These blocks respond to redstone and to right-clicking with an empty hand to toggle the light
* Updated texture for most imperial blocks
* Updated texture for scaffolding blocks
* Updated texture for fuel tank
* Removed support for Sodium when Indium is not also installed
* Dramatically improved cable rendering
* Improved vertical slab placement interactions

# Fixes

* Blaster bolts no longer make the "fly-by" sound immediately when you shoot them
* Fixed game hanging when loading with specific combinations of Sodium and Indium
* Fixed loose sand texture
* Fixed crash when opening and closing the character customizer in a certain order
