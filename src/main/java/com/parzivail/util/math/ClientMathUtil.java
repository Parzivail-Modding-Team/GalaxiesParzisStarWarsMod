package com.parzivail.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

@Environment(EnvType.CLIENT)
public class ClientMathUtil
{
	public static final Matrix4f MATRIX_IDENTITY = new Matrix4f();

	static
	{
		MATRIX_IDENTITY.loadIdentity();
	}

	public static Vec3f transform(Vec3f v, Matrix4f m)
	{
		Vector4f v4 = new Vector4f(v);
		v4.transform(m);
		return new Vec3f(v4.getX(), v4.getY(), v4.getZ());
	}

	public static Vec3f transform(Vec3f v, Matrix3f m)
	{
		Vec3f v3 = v.copy();
		v3.transform(m);
		return v3;
	}

	public static Quaternion getRotation(Direction direction)
	{
		// TODO

		switch (direction)
		{
			case DOWN:
				return new Quaternion(90, 0, 0, true);
			case UP:
				return new Quaternion(-90, 0, 0, true);
			case NORTH:
				return new Quaternion(0, 90, 0, true);
			case SOUTH:
				return new Quaternion(0, -90, 0, true);
			case WEST:
				return new Quaternion(0, 180, 0, true);
			case EAST:
				return new Quaternion(0, 0, 0, true);
			default:
				throw new IllegalStateException("Unexpected value: " + direction);
		}
	}
}
