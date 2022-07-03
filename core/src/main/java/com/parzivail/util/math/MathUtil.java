package com.parzivail.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class MathUtil
{
	public static final int TICKS_PER_SECOND = 20;

	public static final float fPI = (float)Math.PI;

	public static final double ONE_OVER_GOLDEN_RATIO = 0.61803398875;

	public static final float SPEED_OF_SOUND = 275f / TICKS_PER_SECOND; // m/tick

	public static final Vec3d POSX = new Vec3d(1, 0, 0);
	public static final Vec3d NEGX = new Vec3d(-1, 0, 0);
	public static final Vec3d POSY = new Vec3d(0, 1, 0);
	public static final Vec3d NEGY = new Vec3d(0, -1, 0);
	public static final Vec3d POSZ = new Vec3d(0, 0, 1);
	public static final Vec3d NEGZ = new Vec3d(0, 0, -1);

	public static final double toDegrees = 180.0 / Math.PI;
	public static final float toDegreesf = (float)toDegrees;

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

	public static float calculateDopplerShift(Entity a, Entity b)
	{
		// TODO: move doppler handling to OpenAL through SoundSystem's updateListenerPosition call?
		var velA = a.getPos().subtract(a.prevX, a.prevY, a.prevZ);
		var velB = b.getPos().subtract(b.prevX, b.prevY, b.prevZ);

		var posA = a.getEyePos();
		var posB = b.getEyePos();

		var relativeSpeed = posA.distanceTo(posB) - posA.add(velA).distanceTo(posB.add(velB));

		return MathHelper.clamp((float)(relativeSpeed / SPEED_OF_SOUND), -1, 1);
	}

	public static Vec3d project(Vec3d v, Vec3d onto)
	{
		return onto.multiply(v.dotProduct(onto) / onto.dotProduct(onto));
	}

	@NotNull
	public static Vec3d anglesToLook(float pitch, float yaw)
	{
		var x = -MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(pitch * MathHelper.RADIANS_PER_DEGREE);
		var y = -MathHelper.sin(pitch * MathHelper.RADIANS_PER_DEGREE);
		var z = MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE) * MathHelper.cos(pitch * MathHelper.RADIANS_PER_DEGREE);

		return new Vec3d(x, y, z).normalize();
	}

	@NotNull
	public static EulerAngle lookToAngles(Vec3d forward)
	{
		forward = forward.normalize();

		var yaw = -(float)Math.atan2(forward.x, forward.z);
		var pitch = -(float)Math.asin(forward.y);

		return new EulerAngle(pitch * toDegreesf, yaw * toDegreesf, 0);
	}

	public static boolean rectContains(int l, int t, int w, int h, double x, double y)
	{
		return x >= l && x <= l + w && y >= t && y <= t + h;
	}
}
