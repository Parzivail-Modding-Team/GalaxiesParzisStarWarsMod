package com.parzivail.pswg.access.util;

import com.parzivail.pswg.access.IMatrix4fAccess;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class Matrix4fAccessUtil
{
	public static void set(Matrix4f mat, float a00, float a01, float a02, float a03, float a10, float a11, float a12, float a13, float a20, float a21, float a22, float a23, float a30, float a31, float a32, float a33)
	{
		var matE = from(mat);
		matE.set(a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33);
	}

	@SuppressWarnings("ConstantConditions")
	public static IMatrix4fAccess from(Matrix4f matrix)
	{
		return (IMatrix4fAccess)(Object)matrix;
	}

	@SuppressWarnings("ConstantConditions")
	public static Matrix4f to(IMatrix4fAccess matrix)
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
		var lx = from(left);
		var rx = from(right);
		var f = lx.getM00() * rx.getM00() + lx.getM01() * rx.getM10() + lx.getM02() * rx.getM20() + lx.getM03() * rx.getM30();
		var g = lx.getM00() * rx.getM01() + lx.getM01() * rx.getM11() + lx.getM02() * rx.getM21() + lx.getM03() * rx.getM31();
		var h = lx.getM00() * rx.getM02() + lx.getM01() * rx.getM12() + lx.getM02() * rx.getM22() + lx.getM03() * rx.getM32();
		var i = lx.getM00() * rx.getM03() + lx.getM01() * rx.getM13() + lx.getM02() * rx.getM23() + lx.getM03() * rx.getM33();
		var j = lx.getM10() * rx.getM00() + lx.getM11() * rx.getM10() + lx.getM12() * rx.getM20() + lx.getM13() * rx.getM30();
		var k = lx.getM10() * rx.getM01() + lx.getM11() * rx.getM11() + lx.getM12() * rx.getM21() + lx.getM13() * rx.getM31();
		var l = lx.getM10() * rx.getM02() + lx.getM11() * rx.getM12() + lx.getM12() * rx.getM22() + lx.getM13() * rx.getM32();
		var m = lx.getM10() * rx.getM03() + lx.getM11() * rx.getM13() + lx.getM12() * rx.getM23() + lx.getM13() * rx.getM33();
		var n = lx.getM20() * rx.getM00() + lx.getM21() * rx.getM10() + lx.getM22() * rx.getM20() + lx.getM23() * rx.getM30();
		var o = lx.getM20() * rx.getM01() + lx.getM21() * rx.getM11() + lx.getM22() * rx.getM21() + lx.getM23() * rx.getM31();
		var p = lx.getM20() * rx.getM02() + lx.getM21() * rx.getM12() + lx.getM22() * rx.getM22() + lx.getM23() * rx.getM32();
		var q = lx.getM20() * rx.getM03() + lx.getM21() * rx.getM13() + lx.getM22() * rx.getM23() + lx.getM23() * rx.getM33();
		var r = lx.getM30() * rx.getM00() + lx.getM31() * rx.getM10() + lx.getM32() * rx.getM20() + lx.getM33() * rx.getM30();
		var s = lx.getM30() * rx.getM01() + lx.getM31() * rx.getM11() + lx.getM32() * rx.getM21() + lx.getM33() * rx.getM31();
		var t = lx.getM30() * rx.getM02() + lx.getM31() * rx.getM12() + lx.getM32() * rx.getM22() + lx.getM33() * rx.getM32();
		var u = lx.getM30() * rx.getM03() + lx.getM31() * rx.getM13() + lx.getM32() * rx.getM23() + lx.getM33() * rx.getM33();
		lx.set(f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u);
	}

	public static Vec3d transform(Vec3d v, Matrix4f matrix)
	{
		var m = from(matrix);
		return new Vec3d(m.getM00() * v.x + m.getM01() * v.y + m.getM02() * v.z + m.getM03(), m.getM10() * v.x + m.getM11() * v.y + m.getM12() * v.z + m.getM13(), m.getM20() * v.x + m.getM21() * v.y + m.getM22() * v.z + m.getM23());
	}
}
