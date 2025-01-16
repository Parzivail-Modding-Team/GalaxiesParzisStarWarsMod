package dev.pswg.entity;

import dev.pswg.item.GrenadeItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.world.World;

public class NeuralGasGrenadeEntity extends GrenadeEntity
{
	public NeuralGasGrenadeEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public GrenadeItem getItem()
	{
		return null;
	}

	@Override
	public void explode()
	{
		super.explode();
	}
}
