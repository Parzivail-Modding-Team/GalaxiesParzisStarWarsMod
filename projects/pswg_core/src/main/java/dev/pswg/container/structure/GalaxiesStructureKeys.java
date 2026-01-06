package dev.pswg.container.structure;

import dev.pswg.Galaxies;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.structure.Structure;
public class GalaxiesStructureKeys
{
	public static final RegistryKey<Structure> DERELICT_IMPERIAL_CONTAINER = of("derelict_imperial_container");

	private static RegistryKey<Structure> of(String id)
	{
		return RegistryKey.of(RegistryKeys.STRUCTURE, Galaxies.id(id));
	}

	public static void register()
	{
	}
}
