package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.world.tatooine.TatooineChunkGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

public class SwgDimensions
{
	public static class Tatooine
	{
		public static final RegistryKey<DimensionOptions> DIMENSION_KEY = RegistryKey.of(Registry.DIMENSION_KEY, Resources.id("tatooine"));
		public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(Registry.WORLD_KEY, DIMENSION_KEY.getValue());

		public static void register()
		{
			Registry.register(Registry.CHUNK_GENERATOR, Resources.id("tatooine_generator"), TatooineChunkGenerator.CODEC);
		}
	}
}
