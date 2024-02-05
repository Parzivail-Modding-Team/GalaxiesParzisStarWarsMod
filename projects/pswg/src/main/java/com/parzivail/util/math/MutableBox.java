package com.parzivail.util.math;

import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Box;

/*
	Based on: https://github.com/FoundationGames/Phonos
 */
public class MutableBox
{
	private double minX, minY, minZ, maxX, maxY, maxZ;

	public void clear()
	{
		minX = Float.POSITIVE_INFINITY;
		minY = Float.POSITIVE_INFINITY;
		minZ = Float.POSITIVE_INFINITY;
		maxX = Float.NEGATIVE_INFINITY;
		maxY = Float.NEGATIVE_INFINITY;
		maxZ = Float.NEGATIVE_INFINITY;
	}

	public void fit(double x, double y, double z)
	{
		minX = Math.min(minX, x);
		minY = Math.min(minY, y);
		minZ = Math.min(minZ, z);
		maxX = Math.max(maxX, x);
		maxY = Math.max(maxY, y);
		maxZ = Math.max(maxZ, z);
	}

	public void fitTwo(double x0, double y0, double z0, double x1, double y1, double z1)
	{
		minX = Math.min(x0, x1);
		minY = Math.min(y0, y1);
		minZ = Math.min(z0, z1);
		maxX = Math.max(x0, x1);
		maxY = Math.max(y0, y1);
		maxZ = Math.max(z0, z1);
	}

	public boolean visible(Frustum frustum)
	{
		return frustum.isVisible(new Box(minX, minY, minZ, maxX, maxY, maxZ));
	}
}
