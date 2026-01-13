package dev.pswg.container.structure;

import dev.pswg.Galaxies;
import dev.pswg.structure.ContainerStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.structure.StructureType;

public class GalaxiesStructureTypes
{
	public static final StructureType<ContainerStructure> CONTAINER_STRUCTURE = Registry.register(Registries.STRUCTURE_TYPE, Galaxies.id("derelict_imperial_container"), () -> ContainerStructure.CODEC);

	public static void register()
	{

	}
}
