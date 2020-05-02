package com.parzivail.pswg.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class EntityUtil
{
	public static void updateEulerRotation(Entity entity, Quaternion rotation)
	{
		EulerAngle eulerAngle = MathUtil.toEulerAngles(rotation);
		entity.yaw = eulerAngle.getYaw();
		entity.pitch = eulerAngle.getPitch();

		while (entity.pitch - entity.prevPitch >= 180.0F)
		{
			entity.prevPitch += 360.0F;
		}

		while (entity.yaw - entity.prevYaw < -180.0F)
		{
			entity.prevYaw -= 360.0F;
		}

		while (entity.yaw - entity.prevYaw >= 180.0F)
		{
			entity.prevYaw += 360.0F;
		}
	}

	public static Vec3d getPosition(Entity e, float tickDelta)
	{
		Vec3d parentPos = e.getPos();
		Vec3d prevParentPos = new Vec3d(e.prevX, e.prevY, e.prevZ);

		return MathUtil.lerp(tickDelta, prevParentPos, parentPos);
	}
}
