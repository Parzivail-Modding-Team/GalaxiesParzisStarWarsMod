package com.parzivail.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHitResult
{
	public final Entity entity;
	public final Vec3d hit;

	public EntityHitResult(Entity entity, Vec3d hit)
	{
		this.entity = entity;
		this.hit = hit;
	}
}
