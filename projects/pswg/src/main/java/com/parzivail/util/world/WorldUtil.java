package com.parzivail.util.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

public class WorldUtil
{
	public static boolean isSunLit(ServerWorld world, BlockPos pos)
	{
		return getSunlight(world, pos) > 0;
	}

	public static boolean isNightTime(ServerWorld world)
	{
		return (world.getTime() - 6000) % 24000 > 12000;
	}

	private static int getSunlight(ServerWorld world, BlockPos pos)
	{
		if (!world.getDimension().hasSkyLight())
			return 0;

		var skyLight = world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness();
		var skyAngle = world.getSkyAngleRadians(1.0F);

		var upperBoundAngle = skyAngle < Math.PI ? 0 : (2 * Math.PI);
		skyAngle += (upperBoundAngle - skyAngle) * 0.2F;
		skyLight = Math.round(skyLight * MathHelper.cos(skyAngle));

		return MathHelper.clamp(skyLight, 0, 15);
	}
}
