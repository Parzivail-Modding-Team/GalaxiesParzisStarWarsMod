package dev.pswg.container.worldgen;

import dev.pswg.Galaxies;
import dev.pswg.structure.ContainerStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class GalaxiesStructureTypes
{
	public static final StructureType<ContainerStructure> CONTAINER_STRUCTURE = Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Galaxies.id("derelict_imperial_container"), () -> ContainerStructure.CODEC);

	public static void register()
	{

	}
}
