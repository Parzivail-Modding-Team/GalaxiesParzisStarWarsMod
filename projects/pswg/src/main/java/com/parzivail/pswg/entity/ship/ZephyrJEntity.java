package com.parzivail.pswg.entity.ship;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ZephyrJEntity extends SpeederEntity
{
	public ZephyrJEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	public Vec3d getPassengerSocket(int passengerIndex)
	{
		return new Vec3d(0, 0.13f, 0.45f);
	}
}
