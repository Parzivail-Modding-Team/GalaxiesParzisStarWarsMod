package com.parzivail.util.math;

import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

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

	public static void loadIdentity(Matrix4f m)
	{
		set(m, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
	}

	public static void multiply(Matrix4f left, Quaternion right)
	{
		multiply(left, new Matrix4f(right));
	}

	public static void multiply(Matrix4f left, Matrix4f right)
	{
		Matrix4fExt lx = from(left);
		Matrix4fExt rx = from(right);
		float f = lx.getM00() * rx.getM00() + lx.getM01() * rx.getM10() + lx.getM02() * rx.getM20() + lx.getM03() * rx.getM30();
		float g = lx.getM00() * rx.getM01() + lx.getM01() * rx.getM11() + lx.getM02() * rx.getM21() + lx.getM03() * rx.getM31();
		float h = lx.getM00() * rx.getM02() + lx.getM01() * rx.getM12() + lx.getM02() * rx.getM22() + lx.getM03() * rx.getM32();
		float i = lx.getM00() * rx.getM03() + lx.getM01() * rx.getM13() + lx.getM02() * rx.getM23() + lx.getM03() * rx.getM33();
		float j = lx.getM10() * rx.getM00() + lx.getM11() * rx.getM10() + lx.getM12() * rx.getM20() + lx.getM13() * rx.getM30();
		float k = lx.getM10() * rx.getM01() + lx.getM11() * rx.getM11() + lx.getM12() * rx.getM21() + lx.getM13() * rx.getM31();
		float l = lx.getM10() * rx.getM02() + lx.getM11() * rx.getM12() + lx.getM12() * rx.getM22() + lx.getM13() * rx.getM32();
		float m = lx.getM10() * rx.getM03() + lx.getM11() * rx.getM13() + lx.getM12() * rx.getM23() + lx.getM13() * rx.getM33();
		float n = lx.getM20() * rx.getM00() + lx.getM21() * rx.getM10() + lx.getM22() * rx.getM20() + lx.getM23() * rx.getM30();
		float o = lx.getM20() * rx.getM01() + lx.getM21() * rx.getM11() + lx.getM22() * rx.getM21() + lx.getM23() * rx.getM31();
		float p = lx.getM20() * rx.getM02() + lx.getM21() * rx.getM12() + lx.getM22() * rx.getM22() + lx.getM23() * rx.getM32();
		float q = lx.getM20() * rx.getM03() + lx.getM21() * rx.getM13() + lx.getM22() * rx.getM23() + lx.getM23() * rx.getM33();
		float r = lx.getM30() * rx.getM00() + lx.getM31() * rx.getM10() + lx.getM32() * rx.getM20() + lx.getM33() * rx.getM30();
		float s = lx.getM30() * rx.getM01() + lx.getM31() * rx.getM11() + lx.getM32() * rx.getM21() + lx.getM33() * rx.getM31();
		float t = lx.getM30() * rx.getM02() + lx.getM31() * rx.getM12() + lx.getM32() * rx.getM22() + lx.getM33() * rx.getM32();
		float u = lx.getM30() * rx.getM03() + lx.getM31() * rx.getM13() + lx.getM32() * rx.getM23() + lx.getM33() * rx.getM33();
		lx.set(f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u);
	}
}
