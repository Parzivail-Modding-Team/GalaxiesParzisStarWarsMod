package com.parzivail.pswg.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
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

	public static final double toDegrees = 180.0 / Math.PI;
	public static final float toDegreesf = (float)toDegrees;

	public static EulerAngle toEulerAngles(Quaternion q)
	{
		Vec3d forward = rotate(POSZ, q);

		float yaw = -(float)Math.atan2(forward.x, forward.z);
		float pitch = (float)Math.atan2(forward.y, Math.sqrt(forward.x * forward.x + forward.z * forward.z));

		return new EulerAngle(pitch * toDegreesf, yaw * toDegreesf + 180, 0);
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
