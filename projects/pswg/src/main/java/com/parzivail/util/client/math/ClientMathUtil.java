package com.parzivail.util.client.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.*;

@Environment(EnvType.CLIENT)
public class ClientMathUtil
{
	public static Vec3f transform(Vec3f v, Matrix4f m)
	{
		var v4 = new Vector4f(v);
		v4.transform(m);
		return new Vec3f(v4.getX(), v4.getY(), v4.getZ());
	}

	public static Quaternion getRotation(Direction direction)
	{
		return switch (direction)
				{
					case DOWN -> new Quaternion(0, 0, -90, true);
					case UP -> new Quaternion(0, 0, 90, true);
					case NORTH -> new Quaternion(0, 90, 0, true);
					case SOUTH -> new Quaternion(0, -90, 0, true);
					case WEST -> new Quaternion(0, 180, 0, true);
					case EAST -> new Quaternion(0, 0, 0, true);
				};
	}
}
