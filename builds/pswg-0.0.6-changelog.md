# 0.0.6
Changelog:

Feature Additions:
* Added health bars over entities when aiming at them with a blaster
* Added Battlefront-style blaster overheating
* Added the ability to hold the attack button to repeatedly fire a blaster
* Added a chair block
* Added chromium ingot (https://github.com/Parzivail-Modding-Team/StarWarsGalaxy/pull/21)
* Added a rough draft of modular lightsabers
* Added basic lightsaber "forge" block for creative lightsaber editing
* Added temporary Imperial Star Destroyer hovering at `x=-450, z=0` on Tatooine to test the performance of world generation on different platforms
* Added new stair blocks (white metal, caution metal, light gray metal, dark gray metal, black metal)

Changes:
* Blaster burn marks fade over time and are removed when the block they're on is removed
* Changed model of vertical imperial light blocks
* Attempting to run the jarfile as a standalone program will inform the user of the mistake (https://github.com/Parzivail-Modding-Team/StarWarsGalaxy/pull/18)
* Temporarily disable atmospheric sound tile entities

Bugfixes:
* Fixed block picking on slabs
* Fixed block lighting on stairs and slabs
* Fixed smoke grenades on servers (https://github.com/Parzivail-Modding-Team/StarWarsGalaxy/issues/13)
* Fixed sounds not working on servers (https://github.com/Parzivail-Modding-Team/StarWarsGalaxy/issues/14)
* Fixed hand rendering for JSON models (https://github.com/Parzivail-Modding-Team/StarWarsGalaxy/issues/15)
* Fixed JSON model textures breaking when resource packs were reloaded
* Fixed some entities rendering with a bounding box visible