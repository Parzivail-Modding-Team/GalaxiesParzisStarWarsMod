package com.parzivail.util.math;

import net.minecraft.entity.Entity;

public class RaytraceHitEntity extends RaytraceHit
{
	public final Entity entity;

	public RaytraceHitEntity(Entity pointedEntity)
	{
		entity = pointedEntity;
	}
}
