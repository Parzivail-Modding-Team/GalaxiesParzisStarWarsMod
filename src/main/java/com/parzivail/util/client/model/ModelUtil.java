package com.parzivail.util.client.model;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Collection;

public class ModelUtil
{
	public static Box getBounds(Collection<Vec3f> verts)
	{
		var min = new Vec3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		var max = new Vec3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

		for (var v : verts)
		{
			if (v.getX() < min.getX())
				min.set(v.getX(), min.getY(), min.getZ());
			if (v.getY() < min.getY())
				min.set(min.getX(), v.getY(), min.getZ());
			if (v.getZ() < min.getZ())
				min.set(min.getX(), min.getY(), v.getZ());

			if (v.getX() > max.getX())
				max.set(v.getX(), max.getY(), max.getZ());
			if (v.getY() > max.getY())
				max.set(max.getX(), v.getY(), max.getZ());
			if (v.getZ() > max.getZ())
				max.set(max.getX(), max.getY(), v.getZ());
		}

		return new Box(new Vec3d(min), new Vec3d(max));
	}
}
