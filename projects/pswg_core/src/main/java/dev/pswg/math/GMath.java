package dev.pswg.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Utilities related to math
 */
public final class GMath
{
	/**
	 * Returns a forward vector for the given yaw and pitch. Angles are applied
	 * in yaw-roll order.
	 *
	 * @param yaw   The yaw, in degrees
	 * @param pitch The pitch, in degrees
	 *
	 * @return The forward vector for the given yaw and pitch
	 */
	public static Vec3d getForwardVector(double yaw, double pitch)
	{
		var rYaw = Math.toRadians(yaw);
		var rPitch = Math.toRadians(pitch);

		var x = -Math.sin(rYaw) * Math.cos(rPitch);
		var y = -Math.sin(rPitch);
		var z = Math.cos(rYaw) * Math.cos(rPitch);

		return new Vec3d(x, y, z);
	}
}
