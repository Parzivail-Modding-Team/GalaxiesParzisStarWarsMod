package com.parzivail.pswg.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class QuatUtil
{
	public static final double toDegrees = 180.0 / Math.PI;
	public static final float toDegreesf = (float)toDegrees;

	public static final Vec3d POSX = new Vec3d(1, 0, 0);
	public static final Vec3d NEGX = new Vec3d(-1, 0, 0);
	public static final Vec3d POSY = new Vec3d(0, 1, 0);
	public static final Vec3d NEGY = new Vec3d(0, -1, 0);
	public static final Vec3d POSZ = new Vec3d(0, 0, 1);
	public static final Vec3d NEGZ = new Vec3d(0, 0, -1);

	public static EulerAngle toEulerAngles(Quaternion q)
	{
		Vec3d forward = rotate(new Vec3d(0, 0, 1), q);
		Vec3d up = rotate(new Vec3d(0, 1, 0), q);

		// Yaw is the bearing of the forward vector's shadow in the xy plane.
		double yaw = Math.atan2(forward.z, forward.x) - Math.PI / 2;

		// Pitch is the altitude of the forward vector off the xy plane, toward the down direction.
		double pitch = -Math.asin(forward.y);

		// Find the vector in the xy plane 90 degrees to the right of our bearing.
		double planeRightX = Math.sin(yaw);
		double planeRightY = -Math.cos(yaw);

		// Roll is the rightward lean of our up vector, computed here using a dot product.
		double roll = Math.asin(up.x * planeRightX + up.z * planeRightY);
		// If we're twisted upside-down, return a roll in the range +-(pi/2, pi)
		if (up.y < 0)
			roll = Math.signum(roll) * Math.PI - roll;

		return new EulerAngle((float)pitch, (float)yaw, (float)roll);
	}

	public static Vec3d rotate(Vec3d self, Quaternion q)
	{
		// Extract the vector part of the quaternion
		Vec3d u = new Vec3d(q.getB(), q.getC(), q.getD());

		// Extract the scalar part of the quaternion
		float s = q.getA();

		// Do the math
		return u.multiply(2.0f * u.dotProduct(self)).add(self.multiply(s * s - u.dotProduct(u))).add(u.crossProduct(self).multiply(2.0f * s));
	}

	public static Quaternion getRotationTowards(Vec3d from, Vec3d to)
	{
		Quaternion q = copy(Quaternion.IDENTITY);
		Vec3d cross = from.crossProduct(to);
		float w = (float)(Math.sqrt(from.lengthSquared() * to.lengthSquared()) + from.dotProduct(to));
		q.set(w, (float)cross.x, (float)cross.y, (float)cross.z);
		q.normalize();
		return q;
	}

	public static void set(Quaternion self, Quaternion other)
	{
		self.set(other.getA(), other.getB(), other.getC(), other.getD());
	}

	public static float dot_product(Quaternion q1, Quaternion q2)
	{
		return q1.getA() * q2.getA() + q1.getB() * q2.getB() + q1.getC() * q2.getC() + q1.getD() * q2.getD();
	}

	public static Quaternion slerp(Quaternion start, Quaternion end, float t)
	{
		// Only unit quaternions are valid rotations.
		// Normalize to avoid undefined behavior.
		start.normalize();
		end.normalize();

		// Compute the cosine of the angle between the two vectors.
		double dot = dot_product(start, end);

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

			float f = 1 - t;

			float a = f * start.getA() + t * end.getA();
			float b = f * start.getB() + t * end.getB();
			float c = f * start.getC() + t * end.getC();
			float d = f * start.getD() + t * end.getD();

			Quaternion result = new Quaternion(b, c, d, a);
			result.normalize();
			return result;
		}

		// Since dot is in range [0, DOT_THRESHOLD], acos is safe
		double theta_0 = Math.acos(dot);        // theta_0 = angle between input vectors
		double theta = theta_0 * t;          // theta = angle between start and result
		double sin_theta = Math.sin(theta);     // compute this value only once
		double sin_theta_0 = Math.sin(theta_0); // compute this value only once

		double f1 = Math.cos(theta) - dot * sin_theta / sin_theta_0;  // == sin(theta_0 - theta) / sin(theta_0)
		double f2 = sin_theta / sin_theta_0;

		float a = (float)(f1 * start.getA() + f2 * end.getA());
		float b = (float)(f1 * start.getB() + f2 * end.getB());
		float c = (float)(f1 * start.getC() + f2 * end.getC());
		float d = (float)(f1 * start.getD() + f2 * end.getD());

		Quaternion result = new Quaternion(b, c, d, a);
		result.normalize();
		return result;
	}

	public static void rotateTowards(Quaternion self, Vec3d orientation, float speed, Matrix4f mat, VertexConsumer debugConsumer)
	{
		normalize(self);
		Vec3d vec2 = rotate(orientation, self);
		Vec3d cross = orientation.crossProduct(vec2).multiply(-1.0);
		Vec3d axis = cross.normalize();
		float f1 = (float)cross.length();
		if (debugConsumer != null)
		{
			debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(1f, 0f, 0f, 1f).next();
			debugConsumer.vertex(mat, (float)orientation.x, (float)orientation.y, (float)orientation.z).color(1f, 0f, 0f, 1f).next();

			debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(0f, 0f, 1f, 1f).next();
			debugConsumer.vertex(mat, (float)vec2.x, (float)vec2.y, (float)vec2.z).color(0f, 0f, 1f, 1f).next();

			debugConsumer.vertex(mat, 0.0f, 0.0f, 0.0f).color(0f, 1f, 0f, 1f).next();
			debugConsumer.vertex(mat, (float)cross.x, (float)cross.y, (float)cross.z).color(0f, 1f, 0f, 1f).next();
		}
		Quaternion other = new Quaternion(new Vector3f(axis), speed * f1, false);
		other.hamiltonProduct(self);
		set(self, other);
	}

	public static void invert(Quaternion self)
	{
		float l = self.getA() * self.getA() + self.getB() * self.getB() + self.getC() * self.getC() + self.getD() * self.getD();
		self.set(self.getA() / l, -self.getB() / l, -self.getC() / l, -self.getD() / l);
	}

	public static Quaternion invertCopy(Quaternion q)
	{
		Quaternion q1 = copy(q);
		invert(q1);
		return q1;
	}

	public static Vec3d project(Vec3d self, Vec3d ref)
	{
		Vec3d v = ref.normalize();
		double len = self.dotProduct(v);
		return v.multiply(len);
	}

	public static Vec3d reject(Vec3d self, Vec3d ref)
	{
		return self.subtract(project(self, ref));
	}

	public static Quaternion copy(Quaternion self)
	{
		return new Quaternion(self);
	}

	public static void scale(Quaternion self, float factor)
	{
		self.set(self.getA() * factor, self.getB() * factor, self.getC() * factor, self.getD() * factor);
	}

	public static void normalize(Quaternion self)
	{
		float f = self.getB() * self.getB() + self.getC() * self.getC() + self.getD() * self.getD() + self.getA() * self.getA();
		if (f > 1.0E-6F)
		{
			float g = (float)MathHelper.fastInverseSqrt((double)f);
			scale(self, g);
		}
		else
		{
			scale(self, 0f);
		}
	}
}
