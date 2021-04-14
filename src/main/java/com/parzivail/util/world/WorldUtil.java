package com.parzivail.util.world;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

public class WorldUtil
{
	public static boolean isSunLit(ServerWorld world, BlockPos pos)
	{
		if (!world.getDimension().hasSkyLight())
			return false;

		int skyLight = world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness();
		float skyAngle = world.getSkyAngleRadians(1.0F);

		float upperBoundAngle = skyAngle < 3.1415927F ? 0.0F : 6.2831855F;
		skyAngle += (upperBoundAngle - skyAngle) * 0.2F;
		skyLight = Math.round((float)skyLight * MathHelper.cos(skyAngle));

		skyLight = MathHelper.clamp(skyLight, 0, 15);

		return skyLight > 0;
	}
}
