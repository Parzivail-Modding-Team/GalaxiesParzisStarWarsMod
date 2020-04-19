package com.parzivail.pswg.util;

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
}
