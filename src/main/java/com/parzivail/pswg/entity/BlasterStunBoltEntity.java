package com.parzivail.pswg.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class BlasterStunBoltEntity extends BlasterBoltEntity
{
	public BlasterStunBoltEntity(EntityType<? extends BlasterStunBoltEntity> type, World world)
	{
		super(type, world);
	}

	public BlasterStunBoltEntity(EntityType<? extends BlasterStunBoltEntity> type, LivingEntity owner, World world)
	{
		super(type, owner, world);
	}
}

