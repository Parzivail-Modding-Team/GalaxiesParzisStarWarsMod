package dev.pswg.container.worldgen;

import dev.pswg.Galaxies;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
public class GalaxiesStructureKeys
{
	public static final ResourceKey<Structure> DERELICT_IMPERIAL_CONTAINER = of("derelict_imperial_container");

	private static ResourceKey<Structure> of(String id)
	{
		return ResourceKey.create(Registries.STRUCTURE, Galaxies.id(id));
	}

	public static void register()
	{
	}
}
