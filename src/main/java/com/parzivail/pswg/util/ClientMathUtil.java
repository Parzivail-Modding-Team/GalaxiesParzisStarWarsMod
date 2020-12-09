package com.parzivail.pswg.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(EnvType.CLIENT)
public class ClientMathUtil
{
	public static final Matrix4f MATRIX_IDENTITY = new Matrix4f();

	static
	{
		MATRIX_IDENTITY.loadIdentity();
	}

	@Environment(EnvType.CLIENT)
	public static Vector3f transform(Vector3f v, Matrix4f m)
	{
		Vector4f v4 = new Vector4f(v);
		v4.transform(m);
		return new Vector3f(v4.getX(), v4.getY(), v4.getZ());
	}

	@Environment(EnvType.CLIENT)
	public static Vector3f transformNormal(Vector3f v, Matrix4f m)
	{
		Vector4f v4 = new Vector4f(v);
		v4.transform(m);
		Vector4f zero = new Vector4f(0, 0, 0, 0);
		zero.transform(m);

		Vector3f out = new Vector3f(v4.getX() - zero.getX(), v4.getY() - zero.getY(), v4.getZ() - zero.getZ());
		out.normalize();
		return out;
	}

	@Environment(EnvType.CLIENT)
	public static Vector3f transform(Vector3f v, Matrix3f m)
	{
		Vector3f v3 = v.copy();
		v3.transform(m);
		return v3;
	}
}
