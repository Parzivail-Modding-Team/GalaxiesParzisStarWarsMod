package dev.pswg.container;

import dev.pswg.Gadgets;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;

public class GadgetsStructureKeys
{
	public static final RegistryKey<Structure> DERELICT_IMPERIAL_CONTAINER = of("derelict_imperial_container");

	private static RegistryKey<Structure> of(String id)
	{
		return RegistryKey.of(RegistryKeys.STRUCTURE, Gadgets.id(id));
	}

	public static void register()
	{
	}
}
