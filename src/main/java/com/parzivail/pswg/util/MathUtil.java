package com.parzivail.pswg.util;

import com.parzivail.util.math.Matrix4fExt;
import com.parzivail.util.math.MatrixExtUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MathUtil
{
	public static final float fPI = (float)Math.PI;

	public static final double oneOverGoldenRatio = 0.61803398875;

	public static final Vec3d POSX = new Vec3d(1, 0, 0);
	public static final Vec3d NEGX = new Vec3d(-1, 0, 0);
	public static final Vec3d POSY = new Vec3d(0, 1, 0);
	public static final Vec3d NEGY = new Vec3d(0, -1, 0);
	public static final Vec3d POSZ = new Vec3d(0, 0, 1);
	public static final Vec3d NEGZ = new Vec3d(0, 0, -1);

	public static final double toDegrees = 180.0 / Math.PI;
	public static final float toDegreesf = (float)toDegrees;

	public static Vec3d transform(Vec3d v, Matrix4f matrix)
	{
		Matrix4fExt m = MatrixExtUtil.from(matrix);
		return new Vec3d(m.getM00() * v.x + m.getM01() * v.y + m.getM02() * v.z + m.getM03(), m.getM10() * v.x + m.getM11() * v.y + m.getM12() * v.z + m.getM13(), m.getM20() * v.x + m.getM21() * v.y + m.getM22() * v.z + m.getM23());
	}

	public static float fract(double d)
	{
		return (float)(d - Math.floor(d));
	}

	public static Vec2f fract(Vec2f v)
	{
		return new Vec2f(fract(v.x), fract(v.y));
	}

	public static Vec3d fract(Vec3d v)
	{
		return new Vec3d(fract(v.x), fract(v.y), fract(v.z));
	}

	public static double seed(double d, long seed)
	{
		return Double.longBitsToDouble(Double.doubleToLongBits(d) ^ seed);
	}

	public static Vec2f floor(Vec2f v)
	{
		return new Vec2f((float)Math.floor(v.x), (float)Math.floor(v.y));
	}

	public static Vec3d floor(Vec3d v)
	{
		return new Vec3d(Math.floor(v.x), Math.floor(v.y), Math.floor(v.z));
	}

	public static Vec3d lerp(float tickDelta, Vec3d a, Vec3d b)
	{
		return new Vec3d(MathHelper.lerp(tickDelta, a.x, b.x), MathHelper.lerp(tickDelta, a.y, b.y), MathHelper.lerp(tickDelta, a.z, b.z));
	}

	public static Vec2f add(Vec2f a, Vec2f b)
	{
		return new Vec2f(a.x + b.x, a.y + b.y);
	}

	public static Vec2f sub(Vec2f a, Vec2f b)
	{
		return new Vec2f(a.x - b.x, a.y - b.y);
	}

	public static double length(Vec2f v)
	{
		return Math.sqrt(v.x * v.x + v.y * v.y);
	}

	public static int clamp(int i, int min, int max)
	{
		return Math.max(min, Math.min(i, max));
	}

	public static float remap(float x, float iMin, float iMax, float oMin, float oMax)
	{
		return (x - iMin) / (iMax - iMin) * (oMax - oMin) + oMin;
	}
}
