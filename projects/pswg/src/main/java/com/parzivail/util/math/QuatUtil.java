package com.parzivail.util.math;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class QuatUtil
{
	public static final Quaternionf ROT_X_POS45 = new Quaternionf().rotationX(Math.toRadians(45));

	public static final Quaternionf ROT_Y_POS10 = new Quaternionf().rotationY(Math.toRadians(10));
	public static final Quaternionf ROT_Y_POS90 = new Quaternionf().rotationY(Math.toRadians(90));
	public static final Quaternionf ROT_Y_180 = new Quaternionf().rotationY(Math.toRadians(180));

	public static final Quaternionf ROT_Z_POS80 = new Quaternionf().rotationZ(Math.toRadians(80));

	public static final Quaternionf IDENTITY = new Quaternionf();

	private static final Vec3d UP = new Vec3d(0, 1, 0);
	private static final Vec3d FORWARD = new Vec3d(0, 0, 1);

	public static EulerAngle toEulerAngles(Quaternionf q)
	{
		var forward = rotate(MathUtil.V3D_NEG_Z, q);

		return MathUtil.lookToAngles(forward);
	}

	public static Quaternionf lookAt(Vec3d sourcePoint, Vec3d destPoint)
	{
		var forwardVector = destPoint.subtract(sourcePoint).normalize();

		var dot = FORWARD.dotProduct(forwardVector);

		if (Math.abs(dot - (-1.0f)) < 0.000001f)
			return new Quaternionf().rotationAxis(MathHelper.PI, new Vector3f((float)UP.x, (float)UP.y, (float)UP.z));
		if (Math.abs(dot - (1.0f)) < 0.000001f)
			return new Quaternionf(QuatUtil.IDENTITY);

		var rotAngle = Math.acos(dot);
		var rotAxis = FORWARD.crossProduct(forwardVector);
		rotAxis = rotAxis.normalize();

		return new Quaternionf().rotationAxis((float)rotAngle, rotAxis.toVector3f());
	}

	public static Vec3d rotate(Vec3d self, Quaternionf q)
	{
		var u = new Vec3d(q.x, q.y, q.z);
		var s = q.w;
		return u.multiply(2.0f * u.dotProduct(self))
		        .add(self.multiply(s * s - u.dotProduct(u)))
		        .add(u.crossProduct(self).multiply(2.0f * s));
	}

	public static void putQuaternion(NbtCompound tag, String key, Quaternionf q)
	{
		var list = new NbtList();
		list.add(NbtFloat.of(q.w));
		list.add(NbtFloat.of(q.x));
		list.add(NbtFloat.of(q.y));
		list.add(NbtFloat.of(q.z));
		tag.put(key, list);
	}

	public static Quaternionf getQuaternion(NbtCompound tag, String key)
	{
		var list = tag.getList(key, NbtElement.FLOAT_TYPE);
		return new Quaternionf(list.getFloat(1), list.getFloat(2), list.getFloat(3), list.getFloat(0));
	}

	public static void rotateTowards(Quaternionf self, Vec3d orientation, float speed)
	{
		self.normalize();
		var vec2 = rotate(orientation, self);
		var cross = orientation.crossProduct(vec2).multiply(-1.0);
		var axis = cross.normalize();
		var f1 = (float)cross.length();
		var other = new Quaternionf().rotationAxis(speed * f1, axis.toVector3f());
		other.mul(self);
		self.set(other);
	}

	public static Quaternionf getRotationTowards(Vec3d from, Vec3d to)
	{
		var cross = from.crossProduct(to);
		var w = (float)(Math.sqrt(from.lengthSquared() * to.lengthSquared()) + from.dotProduct(to));
		var q = new Quaternionf(w, (float)cross.x, (float)cross.y, (float)cross.z);
		q.normalize();
		return q;
	}

	/**
	 * Finds a global vector in local terms
	 */
	public static Vec3d project(Vec3d v, Quaternionf q)
	{
		var c = new Quaternionf(q);
		c.conjugate();
		return rotate(v, c);
	}

	public static Quaternionf slerp(Quaternionf start, Quaternionf end, float t)
	{
		// Only unit quaternions are valid rotations.
		// Normalize to avoid undefined behavior.
		start.normalize();
		end.normalize();

		// Compute the cosine of the angle between the two vectors.
		double dot = start.dot(end);

		// If the dot product is negative, slerp won't take
		// the shorter path. Note that end and -end are equivalent when
		// the negation is applied to all four components. Fix by
		// reversing one quaternion.
		if (dot < 0.0f)
		{
			end.scale(-1);
			dot = -dot;
		}

		if (dot > 0.9995)
		{
			// If the inputs are too close for comfort, linearly interpolate
			// and normalize the result.

			var f = 1 - t;

			var a = f * start.w + t * end.w;
			var b = f * start.x + t * end.x;
			var c = f * start.y + t * end.y;
			var d = f * start.z + t * end.z;

			var result = new Quaternionf(b, c, d, a);
			result.normalize();
			return result;
		}

		// Since dot is in range [0, DOT_THRESHOLD], acos is safe
		var theta_0 = Math.acos(dot);        // theta_0 = angle between input vectors
		var theta = theta_0 * t;          // theta = angle between start and result
		var sin_theta = Math.sin(theta);     // compute this value only once
		var sin_theta_0 = Math.sin(theta_0); // compute this value only once

		var f1 = Math.cos(theta) - dot * sin_theta / sin_theta_0;  // == sin(theta_0 - theta) / sin(theta_0)
		var f2 = sin_theta / sin_theta_0;

		var a = (float)(f1 * start.w + f2 * end.w);
		var b = (float)(f1 * start.x + f2 * end.x);
		var c = (float)(f1 * start.y + f2 * end.y);
		var d = (float)(f1 * start.z + f2 * end.z);

		var result = new Quaternionf(b, c, d, a);
		result.normalize();
		return result;
	}
}
