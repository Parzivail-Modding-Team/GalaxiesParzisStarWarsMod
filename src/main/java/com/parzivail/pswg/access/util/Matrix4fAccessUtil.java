package com.parzivail.pswg.access.util;

import com.parzivail.pswg.access.IMatrix4fAccess;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class Matrix4fAccessUtil
{
	@SuppressWarnings("ConstantConditions")
	public static IMatrix4fAccess from(Matrix4f matrix)
	{
		return (IMatrix4fAccess)(Object)matrix;
	}

	public static Vec3d transform(Vec3d v, Matrix4f matrix)
	{
		var m = from(matrix);
		return new Vec3d(m.getM00() * v.x + m.getM01() * v.y + m.getM02() * v.z + m.getM03(), m.getM10() * v.x + m.getM11() * v.y + m.getM12() * v.z + m.getM13(), m.getM20() * v.x + m.getM21() * v.y + m.getM22() * v.z + m.getM23());
	}
}
