![Galaxies: Parzi's Star Wars Mod](https://raw.githubusercontent.com/Parzivail-Modding-Team/StarWarsGalaxy/master/resources/images/logo_big.png "Galaxies: Parzi's Star Wars Mod")

# Galaxies: Parzi's Star Wars Mod (PSWG)

![GitHub downloads](https://img.shields.io/github/downloads/Parzivail-Modding-Team/StarWarsGalaxy/total.svg) 
![Discord](https://img.shields.io/discord/412945916476129280.svg)
![Minecraft Version](https://img.shields.io/badge/Minecraft-1.7.10-yellow.svg)


In 2015, Parzi's Star Wars Mod was launched as a cooperative effort to bring together my favorite cinematic and digital worlds. Working alongside the community, we brought to life some of the in-depth and complex systems ever implemented in a Minecraft mod, and supported over a million players worldwide.

Today, I'm working again to create the **next generation** of mods which push the limit of the immersion possible within Minecraft, essentially using it as a game engine. This starts with Galaxies: Parzi's Star Wars Mod, which is slated to have **dozens of planets, locations, and landmarks** to explore, **hundreds of NPCs** to interact with, and **more quests** than you can count.

## Community and Support
You can join the community in [the Discord server!](https://discord.gg/54MVQZZ) I also encourage everyone to add your suggestions (not issues) to our [suggestion tracker](https://pswg.nolt.io/).

If you'd like to help support and sustain the mod, please consider becoming a donor!

[![Become a donor!](https://c5.patreon.com/external/logo/become_a_patron_button.png)](https://www.patreon.com/bePatron?u=8079542)

Perks include:
* Shoutouts
* Getting to name NPCs, locations and structures
* White-list on the mod development server

Goals include:
* Community MMO server
* Ad-free website and downloads

## Building
PSWG runs on the (Forge) Gradle build system. I recommend you use IntelliJ as an IDE. Simply import the gradle project, setup your development workspace, and build.

* Set up your development workspace by running:
    * `gradlew setupDecompWorkspace`
    * `gradlew idea`
* All dependencies are met with Maven and are subsequently compiled into the resulting jarfile.
* Build a jarfile with `gradlew build` to produce `pswg-TEMP.jar` in `./build/libs`. 

Extra Info:

* If IntelliJ fails to run the mod because of a texture error, make sure under Project Structure > Modules > StarWarsGalaxy_Main that the **inherits project compile output path** option is selected.
* `BuildMod.exe` is a .NET/Mono utility that simply runs `gradlew build`, moves `./build/libs/pswg-TEMP.jar` to `./builds/pswg-<version>.jar` and creates a sidecar markdown template of the same name for changelogs.

## Contributing
Please only create pull requests for content additions or bugfixes.

Things to consider:

* Before spending a lot of time on adding a feature, please discuss feature additions with the team on Discord. 
* Pull requests changing formatting, syntax choices, and other non-functional changes are likely to be rejected.
* Conform to the coding style of the class you are editing or similar classes.
* Search previous pull requests to make sure the feature you're adding or the bug you are fixing hasn't already:
    * Been implemented.
    * Been rejected.

## License
Code and most assets Copyright (C) 2016-2018 Colby Newman

Other assets and ideas and concepts Copyright or Trademark Lucasfilm LTD., LucasArts, or Disney.

If you are a modpack owner or distributor, you are fully responsible for supporting this mod's integration with the pack in form and function. If an issue is *caused* by this mod, you or the player is responsible for posting a support ticket/issue here.

#### You CAN:

* Download PSWG and play it.
* Make a public Minecraft server with PSWG.
* Include PSWG in a modpack if you have permission from us, and only if you link back here.
* Make content showing PSWG gameplay (a link back here is not required, but is appreciated).
* Modify, compile, or otherwise use PSWG's source code privately.
* Fork PSWG and contribute content or bugfixes.

#### You CANNOT:

* Redistribute original or modified compiled PSWG binaries without express permission from us.
* Claim that you made PSWG.
