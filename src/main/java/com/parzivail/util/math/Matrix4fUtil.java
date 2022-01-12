package com.parzivail.util.math;

import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import java.nio.FloatBuffer;

public class Matrix4fUtil
{
	private static final float[] _matrixDataBuffer = new float[16];
	private static final FloatBuffer _matrixBuffer;

	public static final Matrix4f IDENTITY = new Matrix4f();

	static
	{
		_matrixBuffer = FloatBuffer.wrap(_matrixDataBuffer);
		IDENTITY.loadIdentity();
	}

	private static int pack(int x, int y)
	{
		return y * 4 + x;
	}

	public static Vec3d transform(Vec3d v, Matrix4f transform)
	{
		// TODO: find a way to do this without a mixin or serializing it
		_matrixBuffer.rewind();
		transform.write(_matrixBuffer, true);

		_matrixBuffer.rewind();
		var a00 = _matrixBuffer.get(pack(0, 0));
		var a01 = _matrixBuffer.get(pack(1, 0));
		var a02 = _matrixBuffer.get(pack(2, 0));
		var a03 = _matrixBuffer.get(pack(3, 0));

		var a10 = _matrixBuffer.get(pack(0, 1));
		var a11 = _matrixBuffer.get(pack(1, 1));
		var a12 = _matrixBuffer.get(pack(2, 1));
		var a13 = _matrixBuffer.get(pack(3, 1));

		var a20 = _matrixBuffer.get(pack(0, 2));
		var a21 = _matrixBuffer.get(pack(1, 2));
		var a22 = _matrixBuffer.get(pack(2, 2));
		var a23 = _matrixBuffer.get(pack(3, 2));

		return new Vec3d(a00 * v.x + a01 * v.y + a02 * v.z + a03, a10 * v.x + a11 * v.y + a12 * v.z + a13, a20 * v.x + a21 * v.y + a22 * v.z + a23);
	}
}
