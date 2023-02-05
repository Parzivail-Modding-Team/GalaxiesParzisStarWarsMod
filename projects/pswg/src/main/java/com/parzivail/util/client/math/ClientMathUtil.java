package com.parzivail.util.client.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class ClientMathUtil
{
	public static Vector3f transform(Vector3f v, Matrix4f m)
	{
		return v.mulPosition(m, new Vector3f());
	}

	public static Quaternionf getRotation(Direction direction)
	{
		return switch (direction)
				{
					case DOWN -> new Quaternionf().rotationXYZ(0, 0, (float)(Math.PI / -2));
					case UP -> new Quaternionf().rotationXYZ(0, 0, (float)(Math.PI / 2));
					case NORTH -> new Quaternionf().rotationXYZ(0, (float)(Math.PI / 2), 0);
					case SOUTH -> new Quaternionf().rotationXYZ(0, (float)(Math.PI / -2), 0);
					case WEST -> new Quaternionf().rotationXYZ(0, (float)Math.PI, 0);
					case EAST -> new Quaternionf().rotationXYZ(0, 0, 0);
				};
	}
}
