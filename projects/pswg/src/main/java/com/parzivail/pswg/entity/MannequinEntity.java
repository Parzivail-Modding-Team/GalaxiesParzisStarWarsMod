package com.parzivail.pswg.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;

public class MannequinEntity extends ArmorStandEntity
{
	public MannequinEntity(EntityType<? extends MannequinEntity> entityType, World world)
	{
		super(entityType, world);
	}

	public MannequinEntity(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}
}
