package com.parzivail.pswg.util;

import com.parzivail.pswg.util.struct.Matrix4f;
import com.parzivail.util.math.Matrix4fExt;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;

public class VertUtil
{
	public static Vector3f toMinecraftNormal(Vec3d v)
	{
		return new Vector3f(v);
	}

	public static Vector3f toMinecraftVertex(Vec3d v)
	{
		return new Vector3f(v.add(0.5, 0, 0.5));
	}

	public static net.minecraft.client.util.math.Matrix4f toClientMat(Matrix4f m)
	{
		Matrix4fExt mat = MatUtil.from(new net.minecraft.client.util.math.Matrix4f());
		mat.set(m.m00, m.m10, m.m20, m.m30, m.m01, m.m11, m.m21, m.m31, m.m02, m.m12, m.m22, m.m32, m.m03, m.m13, m.m23, m.m33);
		return MatUtil.to(mat);
	}
}
