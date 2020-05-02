package com.parzivail.util.math;

import net.minecraft.client.util.math.Matrix4f;

public class MatrixExtUtil
{
	public static void set(Matrix4f mat, float a00, float a01, float a02, float a03, float a10, float a11, float a12, float a13, float a20, float a21, float a22, float a23, float a30, float a31, float a32, float a33)
	{
		Matrix4fExt matE = from(mat);
		matE.set(a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33);
	}

	@SuppressWarnings("ConstantConditions")
	public static Matrix4fExt from(Matrix4f matrix)
	{
		return (Matrix4fExt)(Object)matrix;
	}

	@SuppressWarnings("ConstantConditions")
	public static Matrix4f to(Matrix4fExt matrix)
	{
		return (Matrix4f)(Object)matrix;
	}
}
