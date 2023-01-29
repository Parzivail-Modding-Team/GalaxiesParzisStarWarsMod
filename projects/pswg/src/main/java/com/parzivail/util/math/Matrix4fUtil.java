package com.parzivail.util.math;

import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.nio.FloatBuffer;

public class Matrix4fUtil
{
	private static final float[] _matrixDataBuffer = new float[16];
	private static final FloatBuffer _matrixBuffer;

	public static final Matrix4f IDENTITY = new Matrix4f();
	public static final Matrix4f SCALE_10_16THS = new Matrix4f().scale(10 / 16f, 10 / 16f, 10 / 16f);

	static
	{
		_matrixBuffer = FloatBuffer.wrap(_matrixDataBuffer);
	}

	private static int pack(int x, int y)
	{
		return y * 4 + x;
	}

	public static Vec3d transform(Vec3d v, Matrix4f transform)
	{
		var vec3d = new Vector3d(v.x, v.y, v.z).mulPosition(transform);
		return new Vec3d(vec3d.x, vec3d.y, vec3d.z);
	}
}
