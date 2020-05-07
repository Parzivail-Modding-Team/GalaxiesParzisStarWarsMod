package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.dimension.TatooineDimension;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class SwgDimensions
{
	public static class Tatooine
	{
		public static final FabricDimensionType DIMENSION_TYPE = FabricDimensionType.builder().defaultPlacer((oldEntity, destinationWorld, portalDir, horizontalOffset, verticalOffset) -> new BlockPattern.TeleportTarget(new Vec3d(destinationWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE, BlockPos.ORIGIN)), oldEntity.getVelocity(), (int)oldEntity.yaw)).factory(TatooineDimension::new).skyLight(true).buildAndRegister(Resources.identifier("tatooine"));

		public static void register()
		{
		}
	}
}
