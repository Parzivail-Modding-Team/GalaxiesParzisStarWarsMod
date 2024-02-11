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
* Dramatically improved lightsaber and blaster bolt rendering when Iris is installed
* Improved vertical slab placement interactions
* Blasters now destroy some ice blocks

# Fixes

* Blaster bolts no longer make the "fly-by" sound immediately when you shoot them
* Fixed game hanging when loading with specific combinations of Sodium and Indium
* Fixed loose sand texture
* Fixed crash when opening and closing the character customizer in a certain order
* Fixed crash when another mod overrides the player model but a species is still applied
* Fixed hand rendering when a character was selected
* Fixed a situation where editing your own character might edit a recently-customized mannequin instead
* Fixed dragging the character to rotate it in the character customizer
* Mannequins now are properly previewed in the character customizer when using the wizard
* Fixed player models rotating at odd angles when mannequins are rendering nearby
* Fixed some cubes being shown or hidden on mannequins at the wrong time due to gender or armor
* Fixed left-handed lightsaber blocking
