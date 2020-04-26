package com.parzivail.pswg.util;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil
{
	public static final float PI = (float)Math.PI;

	public static final Vec3d POSX = new Vec3d(1, 0, 0);
	public static final Vec3d NEGX = new Vec3d(-1, 0, 0);
	public static final Vec3d POSY = new Vec3d(0, 1, 0);
	public static final Vec3d NEGY = new Vec3d(0, -1, 0);
	public static final Vec3d POSZ = new Vec3d(0, 0, 1);
	public static final Vec3d NEGZ = new Vec3d(0, 0, -1);

	public static Rotation lerp(Rotation start, Rotation end, float t)
	{
		return new Rotation(MathUtil.lerp(start.toEulerAngles(), end.toEulerAngles(), t));
	}

	public static EulerAngle lerp(EulerAngle start, EulerAngle end, float t)
	{
		float sPitch = start.getPitch();
		float ePitch = end.getPitch();
		float sYaw = start.getYaw();
		float eYaw = end.getYaw();
		float sRoll = start.getRoll();
		float eRoll = end.getRoll();

		// -179 -> 179 transition

		float pitch = MathHelper.lerpAngleDegrees(t, sPitch, ePitch);
		float yaw = MathHelper.lerpAngleDegrees(t, sYaw, eYaw);
		float roll = MathHelper.lerpAngleDegrees(t, sRoll, eRoll);

		return new EulerAngle(pitch, yaw + 360, roll);
	}
}
