package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.dimension.tatooine.TatooineChunkGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class SwgDimensions
{
	public static class Tatooine
	{
		public static final RegistryKey<World> DIMENSION = RegistryKey.of(Registry.DIMENSION, Resources.identifier("tatooine"));
		public static final RegistryKey<DimensionType> DIMENSION_TYPE_KEY = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, Resources.identifier("tatooine"));

		public static DimensionType TYPE;
		public static ServerWorld WORLD;

		public static void registerDimension()
		{
			Registry.register(Registry.CHUNK_GENERATOR, Resources.identifier("tatooine_chunk_generator"), TatooineChunkGenerator.CODEC);

			ServerLifecycleEvents.SERVER_STARTED.register(server -> {
				TYPE = server.getRegistryManager().getDimensionTypes().get(DIMENSION_TYPE_KEY);
				WORLD = server.getWorld(DIMENSION);
			});
		}
	}
}
