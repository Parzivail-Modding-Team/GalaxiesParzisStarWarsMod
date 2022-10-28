package com.parzivail.pswg.entity.ship;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class X34LandspeederEntity extends SpeederEntity
{
	public X34LandspeederEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	public Vec3d getPassengerSocket(int passengerIndex)
	{
		if (passengerIndex > 0)
			return new Vec3d(0.5f, 0.1f, 1.25f);
		return new Vec3d(-0.5f, 0.1f, 1.25f);
	}
}
