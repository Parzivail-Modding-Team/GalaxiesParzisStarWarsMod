package com.parzivail.util.world;

import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;

public class Zone
{
	private final int minX;
	private final int minY;
	private final int minZ;
	private final int maxX;
	private final int maxY;
	private final int maxZ;

	public final int dimension;
	public final String name;

	public Zone(int dimension, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, String name)
	{
		this.dimension = dimension;
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.minZ = Math.min(minZ, maxZ);
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
		this.maxZ = Math.max(minZ, maxZ);
		this.name = name;
	}

	public boolean contains(Vector3f pos)
	{
		return pos.x >= minX && pos.x <= maxX && pos.y >= minY && pos.y <= maxY && pos.z >= minZ && pos.z <= maxZ;
	}

	public boolean contains(Entity e)
	{
		return e.posX >= minX && e.posX <= maxX && e.posY >= minY && e.posY <= maxY && e.posZ >= minZ && e.posZ <= maxZ;
	}

	public Vector3f getCenter()
	{
		return new Vector3f(minX + (maxX - minX) / 2f, minY + (maxY - minY) / 2f, minZ + (maxZ - minZ) / 2f);
	}
}
