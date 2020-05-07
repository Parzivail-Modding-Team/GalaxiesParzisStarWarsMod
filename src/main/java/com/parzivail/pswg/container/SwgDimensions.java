package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.dimension.TatooineDimension;
import com.parzivail.pswg.dimension.tatooine.TatooineBiome;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;

public class SwgDimensions
{
	public static class Tatooine
	{
		public static final TatooineBiome BIOME = new TatooineBiome();
		public static FabricDimensionType DIMENSION_TYPE;

		public static void registerDimension()
		{
			final Identifier identifier = Resources.identifier("tatooine");
			Registry.register(Registry.BIOME, identifier, BIOME);
			DIMENSION_TYPE = FabricDimensionType.builder()
			                                    .defaultPlacer((oldEntity, destinationWorld, portalDir, horizontalOffset, verticalOffset) -> new BlockPattern.TeleportTarget(new Vec3d(destinationWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE, BlockPos.ORIGIN)), oldEntity.getVelocity(), (int)oldEntity.yaw))
			                                    .factory(TatooineDimension::new)
			                                    .skyLight(true)
			                                    .buildAndRegister(identifier);
		}
	}
}
